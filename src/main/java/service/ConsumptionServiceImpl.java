package service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import annotation.TrackExecutionTime;
import constant.Constant;
import exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import model.ConsumptionData;
import repository.EnergyReportRepository;

@Slf4j
@Component
public class ConsumptionServiceImpl implements ConsumptionService {

  @Autowired
  private EnergyReportRepository energyReportRepository;

  @Override
  public List<ConsumptionData> findAllData() {
    return (List<ConsumptionData>) energyReportRepository.findAll();
  }

  @Override
  @TrackExecutionTime
  @Cacheable(value = "consumptionCache", key = "#startDate + '_' + #measurmentPrice")
  public List<ConsumptionData> findByMeasurmentPrice(String startDate, String measurmentPrice) {

    // FIX 2: Validate price range
    validatePrice(measurmentPrice);

    List<ConsumptionData> consumptionDatas = energyReportRepository.findByMeasurmentPrice(measurmentPrice);
    log.info("Records from DB for price {}: {}", measurmentPrice, consumptionDatas.size());

    // FIX 3: Java 8 Stream implementation
    String inputYear = (startDate != null && startDate.length() >= 4) ? startDate.substring(0, 4) : null;

    List<ConsumptionData> filtered = consumptionDatas.stream()
        .filter(data -> inputYear != null && 
                        data.getDocumentDateTime() != null && 
                        data.getDocumentDateTime().length() >= 4 &&
                        data.getDocumentDateTime().startsWith(inputYear))
        .collect(Collectors.toList());

    log.info("Filtered records: {} for date: {}, price: {}", filtered.size(), startDate, measurmentPrice);
    return filtered;
  }

  // Extracted validation method for cleaner logic flow
  private void validatePrice(String measurmentPrice) {
    try {
      double price = Double.parseDouble(measurmentPrice);
      if (price < Constant.MIN_PRICE_VALUE || price > Constant.MAX_PRICE_VALUE) {
        throw new ApiRequestException(Constant.INVALID_PRICE_INPUT + measurmentPrice);
      }
    } catch (NumberFormatException e) {
      throw new ApiRequestException(Constant.INVALID_PRICE_INPUT + measurmentPrice);
    }
  }
}
