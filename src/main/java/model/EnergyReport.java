package model;

import lombok.Data;
import lombok.ToString;

/**
 * FIX: Removed @Component. Domain model objects must not be Spring beans.
 * When @Component is present, Spring creates a single shared instance in the
 * application context. Any code that later modifies that instance (e.g. the SAX
 * handler setting fields) would be mutating a shared object rather than a fresh one,
 * leading to data corruption across requests.
 */
@Data
@ToString
public class EnergyReport {

  private String documentIdentification;

  private String documentDateTime;

  private AccountTimeSeries accountTimeSeries;

  // Note: price field removed — it was declared but never set or read anywhere.
  // Re-add if a future requirement needs it.
}
