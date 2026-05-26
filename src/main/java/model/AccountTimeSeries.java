package model;

import lombok.Data;
import lombok.ToString;

/**
 * FIX: Renamed field from 'AccountingPoint' (capital A) to 'accountingPoint' (lowercase a).
 *
 * Lombok @Data generates getters/setters based on the field name. A field named
 * 'AccountingPoint' generates getAccountingPoint() and setAccountingPoint() correctly,
 * BUT the Jackson/JPA layer expects the bean property to follow standard JavaBean
 * conventions (camelCase starting with lowercase). The capital A caused the setter to
 * be misidentified in some frameworks, meaning setAccountingPoint() was never called
 * during XML parsing or JPA mapping.
 */
@Data
@ToString
public class AccountTimeSeries {

  private String accountingPoint;

  private ConsumptionHistory consumptionHistory;

  private String measurementUnit;
}
