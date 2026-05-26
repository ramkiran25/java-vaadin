package consumption.energy.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import exception.ApiRequestException;
import model.ConsumptionData;
import repository.EnergyReportRepository;
import service.ConsumptionServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ConsumptionServiceImplTest {

  @Mock
  private EnergyReportRepository repository;
  @InjectMocks
  private ConsumptionServiceImpl consumptionService;

  // Test Fix 2: Price Validation
  @Test
  public void findByMeasurmentPrice_ThrowsExceptionForInvalidPrice() {
    assertThrows(ApiRequestException.class, () -> {
      consumptionService.findByMeasurmentPrice("2021", "invalid_price");
    });
  }

  // Test Fix 3: Date Filtering logic
  @Test
  public void findByMeasurmentPrice_FiltersCorrectYear() {
    List<ConsumptionData> mockData = new ArrayList<>();
    ConsumptionData d1 = new ConsumptionData();
    d1.setDocumentDateTime("2021-01-01"); // Match
    d1.setMeasurmentPrice("0.12");

    ConsumptionData d2 = new ConsumptionData();
    d2.setDocumentDateTime("2022-01-01"); // Mismatch
    d2.setMeasurmentPrice("0.12");

    mockData.add(d1);
    mockData.add(d2);

    when(repository.findByMeasurmentPrice("0.12")).thenReturn(mockData);

    List<ConsumptionData> result = consumptionService.findByMeasurmentPrice("2021", "0.12");

    assertEquals(1, result.size());
    assertEquals("2021-01-01", result.get(0).getDocumentDateTime());
  }
}
