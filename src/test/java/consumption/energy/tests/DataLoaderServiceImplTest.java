package consumption.energy.tests;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import model.EnergyReport;
import repository.EnergyReportRepository;
import service.DataLoaderServiceImpl;
import util.SaxXmlParser;
import constant.Constant;

@RunWith(MockitoJUnitRunner.class)
public class DataLoaderServiceImplTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private EnergyReportRepository repository;
  @Mock
  private SaxXmlParser saxXmlParser;

  @InjectMocks
  private DataLoaderServiceImpl dataLoaderService;

  @Test
  public void testLoadSeedData_Success() throws Exception {
    // Setup mock response
    String xmlResponse = "<xml>data</xml>";
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(ResponseEntity.ok(xmlResponse));

    // Mock parser to return a valid object
    EnergyReport mockReport = new EnergyReport();
    // ... (fill in necessary child objects: AccountTimeSeries, etc.)
    when(saxXmlParser.parse(anyString())).thenReturn(mockReport);

    try {
      // ... (your existing setup)
      String result = dataLoaderService.loadSeedData();
      assertEquals(Constant.DATA_LOADED_SUCCESS, result);
    } catch (Exception e) {
      e.printStackTrace(); // This will print the ACTUAL cause in your console
      throw e;
    }
  }

}
