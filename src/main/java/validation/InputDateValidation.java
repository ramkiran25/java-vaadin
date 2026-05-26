package validation;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Validates start and end dates supplied by the user on the main filter form.
 *
 * Rules (aligned with requirements):
 *  - Neither date may be null.
 *  - startDate must not be after endDate.
 *  - Neither date may be in the future (data only exists up to yesterday).
 *  - The date range may not exceed the configured DAYS_TO_SUBTRACT window (2 years).
 */
@Component
public class InputDateValidation {

  private static final int MAX_RANGE_DAYS = constant.Constant.DAYS_TO_SUBTRACT;

  public InputDateValidation() {}

  /**
   * Returns true when the date range is valid, false otherwise.
   */
  public static boolean validate(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      return false;
    }

    LocalDate today = LocalDate.now();

    // Dates must not be in the future
    if (startDate.isAfter(today) || endDate.isAfter(today)) {
      return false;
    }

    // startDate must be before or equal to endDate
    if (startDate.isAfter(endDate)) {
      return false;
    }

    // Range must not exceed the maximum window
    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    if (daysBetween > MAX_RANGE_DAYS) {
      return false;
    }

    return true;
  }
}
