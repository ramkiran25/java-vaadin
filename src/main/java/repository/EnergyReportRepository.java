package repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import model.ConsumptionData;

/**
 * FIX: Removed the erroneous `void save(EnergyReport energyReport)` declaration.
 *
 * CrudRepository<ConsumptionData, Integer> already provides save(ConsumptionData).
 * Declaring an additional overload for the unmanaged type EnergyReport causes Spring
 * Data to attempt to generate a query for it at startup, which fails because EnergyReport
 * is not a JPA @Entity. DataLoaderServiceImpl correctly builds a ConsumptionData object
 * via buildCustomerData() — that ConsumptionData is what should be (and now is) passed
 * to the standard save().
 */
@Repository
public interface EnergyReportRepository extends CrudRepository<ConsumptionData, Integer> {

  List<ConsumptionData> findByMeasurmentPrice(String measurmentPrice);
}
