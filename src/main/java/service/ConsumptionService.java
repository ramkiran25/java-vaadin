package service;

import java.util.List;
import model.ConsumptionData;


public interface ConsumptionService {
  List<ConsumptionData> findAllData();

  List<ConsumptionData> findByMeasurmentPrice(String startDate, String price);
}
