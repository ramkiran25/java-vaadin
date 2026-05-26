package service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import annotation.TrackExecutionTime;
import constant.Constant;
import exception.ApiRequestException;
import model.ConsumptionData;
import model.EnergyReport;
import model.HourConsumption;
import repository.EnergyReportRepository;
import util.SaxXmlParser;

/**
 * Changes from previous version:
 *
 * FIX 1 — SaxXmlParser is now injected as a Spring bean (instance method) instead of
 *   called as a static utility. This allows AOP (@TrackExecutionTime) to work and
 *   removes the coupling to static handler state.
 *
 * FIX 2 — URL base is now read from application.properties via @Value, not hardcoded
 *   in the Constant class. Falls back to the constant if the property is absent.
 *
 * FIX 3 — XML parse exceptions are no longer swallowed. SaxXmlParser now rethrows,
 *   and we surface it as an ApiRequestException so the caller knows loading failed.
 *
 * FIX 4 — loadSeedDataFallBack() is wired via the method name convention; the comment
 *   is updated to clarify it requires a circuit-breaker library (e.g. Resilience4j) to
 *   be activated — it does nothing by itself.
 */
@Component
public class DataLoaderServiceImpl implements DataLoaderService {

  private static final String INVALID = "Invalid";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private EnergyReportRepository energyReportRepository;

  // FIX 1: inject the parser bean rather than calling a static method
  @Autowired
  private SaxXmlParser saxXmlParser;

  // FIX 2: externalised URL — set energy.api.url.start in application.properties
  @Value("${energy.api.url.start:" + Constant.URL_START + "}")
  private String urlStart;

  @Value("${energy.api.url.end.part:" + Constant.URL_END_PART + "}")
  private String urlEndPart;

  @Override
  @TrackExecutionTime
  public String loadSeedData() throws ParserConfigurationException, SAXException, IOException {
    try {
      String url = createUrl();
      ResponseEntity<String> respXmlString = restTemplate.getForEntity(url, String.class);

      if (respXmlString.getBody() == null || respXmlString.getBody().contains(INVALID)) {
        throw new ApiRequestException(Constant.SERVICE_UNAVAILABLE);
      }

      // FIX 1 & 3: use injected bean; exceptions propagate correctly now
      EnergyReport energyReport = saxXmlParser.parse(respXmlString.getBody());

      int size = energyReport
          .getAccountTimeSeries()
          .getConsumptionHistory()
          .getHourConsumption()
          .size();

      for (int index = 0; index < size; index++) {
        ConsumptionData consumptionData = buildCustomerData(energyReport, index);
        energyReportRepository.save(consumptionData);
      }

    } catch (HttpStatusCodeException ex) {
      throw new ApiRequestException(ex.getMessage());
    } catch (RuntimeException ex) {
      // Covers parse failures rethrown from SaxXmlParser
      throw new ApiRequestException("Data load failed: " + ex.getMessage());
    }
    return Constant.DATA_LOADED_SUCCESS;
  }

  /**
   * FIX 4: This is a circuit-breaker fallback stub. It will only be invoked automatically
   * if wired with Resilience4j @CircuitBreaker(fallbackMethod = "loadSeedDataFallBack")
   * or equivalent. Without that annotation on loadSeedData(), this method is never called.
   */
  public String loadSeedDataFallBack(Exception e) {
    return Constant.SERVICE_UNAVAILABLE;
  }

  private ConsumptionData buildCustomerData(EnergyReport data, int index) {
    ConsumptionData consumptionData = new ConsumptionData();
    consumptionData.setDocumentIdentification(data.getDocumentIdentification());
    consumptionData.setAccountingPoint(data.getAccountTimeSeries().getAccountingPoint());
    consumptionData.setMeasurementUnit(data.getAccountTimeSeries().getMeasurementUnit());

    HourConsumption hourConsumption =
        data.getAccountTimeSeries().getConsumptionHistory().getHourConsumption().get(index);

    // Reformat ts from yyyy-MM-dd to dd-MM-yyyy to match the stored date format
    String[] str = hourConsumption.getTs().substring(0, 10).split("-");
    String formattedDate = str[2] + "-" + str[1] + "-" + str[0];

    consumptionData.setMeasurmentPrice(hourConsumption.getContent());
    consumptionData.setDocumentDateTime(formattedDate);
    return consumptionData;
  }

  private String createUrl() {
    // FIX: DATE_PATTERN now uses lowercase yyyy — see Constant.java fix
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATE_PATTERN);
    LocalDate currentTime = LocalDate.now();
    LocalDate endTime = currentTime.minusDays(1);
    LocalDate prev = currentTime.minusDays(Constant.DAYS_TO_SUBTRACT);
    String startTime = formatter.format(prev);
    String currentLocalTime = formatter.format(endTime);
    return urlStart + startTime + urlEndPart + currentLocalTime;
  }
}
