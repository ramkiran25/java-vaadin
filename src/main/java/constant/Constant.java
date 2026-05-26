package constant;

public class Constant {

  public static final String RESET = "Reset";
  public static final String LOG_OUT = "LogOut";
  public static final String NOT_SELECTED = "Not selected";
  public static final String ENERGY_CONSUMPTION = "Filter by start date, end date and price";
  public static final String DOCUMENT_IDENTIFICATION = "DocumentIdentification ";
  public static final String MEASUREMENT_UNIT = "MeasurementUnit ";
  public static final String ACCOUNTING_POINT = "AccountingPoint ";
  public static final String DOCUMENT_DATE_TIME = "DocumentDateTime ";

  public static final String MEASUREMENT_PRICE = "MeasurementPrice ";
  public static final String START_DATE = "Enter start date";
  public static final String ENTER_CONSUMPTION_VALUE = "Enter KW/h price";

  // FIX: URL moved to application.properties (energy.api.url.start / energy.api.url.end)
  // Kept here only as fallback reference — prefer @Value injection in services.
  public static final String SERVICE_UNAVAILABLE = "Service unavailable";

  public static final String FORM_MAIN_VIEW = "main-view";
  public static final String INPUT_TEXT_FIELD_PLACE_HOLDER = "filter kw/h price ..";
  public static final String INPUT_DATE_PLACE_HOLDER = "Input date..";
  public static final String GRID_LIST_VIEW = "list-view";
  public static final String BUTTON_THEME = "primary contained";
  public static final String INVALID_PRICE_INPUT = "Invalid Price Input..  ";
  public static final String INVALID_DATE = "Invalid Selected date..  ";
  public static final double MAX_PRICE_VALUE = 5.0;
  public static final double MIN_PRICE_VALUE = 0.0;
  public static final String DATA_LOADED_SUCCESS = "Data loaded successfully";

  public static final String CUSTOMER_ENERGY_CONSUMPTION_DATA = "Customer energy consumption data";

  public static final String URL_START = "https://finestmedia.ee/kwh/?start=";
  public static final String URL_END_PART = "&end=";
  public static final int DAYS_TO_SUBTRACT = 2 * 365;

  // FIX: was "dd-MM-YYYY" — capital YYYY is ISO week-based year, must be lowercase yyyy
  public static final String DATE_PATTERN = "dd-MM-yyyy";
}
