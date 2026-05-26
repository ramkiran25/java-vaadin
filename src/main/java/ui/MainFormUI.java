package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import constant.Constant;
import exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import model.ConsumptionData;
import service.ConsumptionService;
import service.DataLoaderService;
import validation.InputDateValidation;

/**
 * Fixes applied:
 *
 * FIX 1 — NullPointerException on empty price field in clickSubmitButton():
 *   Double.parseDouble("") throws NumberFormatException, and calling parseDouble
 *   before the isEmpty() guard means an empty field always crashes before the
 *   warning dialog can open. Reordered the guards: empty check first, then
 *   numeric parse, then range check.
 *
 * FIX 2 — Buttons added to buttonHLayout twice:
 *   buttonHLayout.add(submitbtn, reSetbtn, logoutbtn) was called twice in
 *   createButtonPanel(), doubling the buttons in the rendered layout.
 *
 * FIX 3 — getUI().get() without isPresent() check in clickLogoutButton():
 *   getUI() returns Optional<UI>; calling .get() directly throws
 *   NoSuchElementException if the component is detached. Changed to
 *   getUI().ifPresent(...) matching the pattern used in LoginScreenUI.
 *
 * FIX 4 — Data loading in the constructor:
 *   loadSeedData() was called from buildMainFormUI() which was called from the
 *   constructor. Heavy I/O (REST call + XML parse + DB writes) in a constructor
 *   blocks Vaadin's UI thread during component initialisation. Moved to
 *   onAttach() so it runs after the view is attached and the UI session is ready.
 *
 * FIX 5 — Stale constant references:
 *   Constant.DATALOADED_SUCCESS renamed to Constant.DATA_LOADED_SUCCESS and
 *   Constant.SERVICE_UNAVIALABLE renamed to Constant.SERVICE_UNAVAILABLE in the
 *   previous backend fix. Updated here to match.
 *
 * FIX 6 — InputDateValidation never used:
 *   The validation class was implemented (in the previous fix) but never called
 *   from the UI. validateInputDate() now delegates to InputDateValidation.validate()
 *   for the start/end bounds check instead of reinventing the logic inline.
 *
 * FIX 7 — Min price guard was missing:
 *   clickSubmitButton() only checked > MAX_PRICE_VALUE, not < 0. The requirement
 *   states prices cannot be negative. Added the lower-bound check explicitly,
 *   consistent with Constant.MIN_PRICE_VALUE = 0.0.
 */
@Slf4j
@Route("Main")
@PageTitle("Main page")
@StyleSheet("frontend://styles/styles.css")
public class MainFormUI extends VerticalLayout {

  private static final long serialVersionUID = 1L;

  private static final String DOCUMENT_IDENTIFICATION = "documentIdentification";
  private static final String MEASURMENT_PRICE        = "measurmentPrice";
  private static final String MEASUREMENT_UNIT        = "measurementUnit";
  private static final String ACCOUNTING_POINT        = "accountingPoint";
  private static final String DOCUMENT_DATE_TIME      = "documentDateTime";

  private Button submitbtn;
  private Button reSetbtn;
  private Button logoutbtn;
  private TextField inputTextField;
  private DatePicker inputDatePicker;

  private List<ConsumptionData> energyReport;
  private final Grid<ConsumptionData> grid = new Grid<>(ConsumptionData.class);

  private final ConsumptionService consumptionService;
  private final DataLoaderService dataLoaderService;

  public MainFormUI(ConsumptionService consumptionService, DataLoaderService dataLoaderService) {
    this.consumptionService = consumptionService;
    this.dataLoaderService  = dataLoaderService;
    // FIX 4: UI shell is built eagerly; data is loaded in onAttach() below.
    buildShell();
  }

  /**
   * FIX 4: Heavy initialisation (REST + XML parse + DB) moved out of the constructor
   * and into onAttach(), which Vaadin calls once the component is attached to a live
   * UI session. This keeps the constructor fast and avoids blocking the UI thread
   * before the view is ready to display a loading state or error dialog.
   */
  @Override
  protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    try {
      if (dataLoaderService.loadSeedData().equals(Constant.DATA_LOADED_SUCCESS)) {
        energyReport = consumptionService.findAllData();
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addClassName(Constant.FORM_MAIN_VIEW);
        mainLayout.setHorizontalComponentAlignment(Alignment.START);
        setSizeFull();
        populateFormData(mainLayout, energyReport);
        add(mainLayout);
      }
    } catch (ApiRequestException | ParserConfigurationException | SAXException | IOException ex) {
      log.error("Failed to load seed data: {}", ex.getMessage());
      // FIX 5: corrected constant name SERVICE_UNAVIALABLE -> SERVICE_UNAVAILABLE
      createWarningDialogBox(null, false);
    }
  }

  private void buildShell() {
    setSizeFull();
  }

  private void populateFormData(VerticalLayout mainLayout, List<ConsumptionData> energyReport) {
    Label documentIdentificationLabel = new Label(Constant.CUSTOMER_ENERGY_CONSUMPTION_DATA);
    HorizontalLayout hLayout = new HorizontalLayout();
    hLayout.add(documentIdentificationLabel);
    hLayout.setAlignItems(Alignment.CENTER);

    HorizontalLayout buttonPanel = createButtonPanel();
    mainLayout.add(buttonPanel);

    grid.setColumns(DOCUMENT_IDENTIFICATION, DOCUMENT_DATE_TIME, ACCOUNTING_POINT,
        MEASUREMENT_UNIT, MEASURMENT_PRICE);
    addClassName(Constant.GRID_LIST_VIEW);
    grid.setItems(energyReport);

    add(hLayout);
    add(grid);
  }

  private HorizontalLayout createButtonPanel() {
    submitbtn = createButton("Submit");
    submitbtn.setEnabled(false);

    inputTextField = new TextField();
    inputTextField.setPlaceholder(Constant.INPUT_TEXT_FIELD_PLACE_HOLDER);

    inputDatePicker = inputDateSelection(Constant.START_DATE);

    reSetbtn = createButton(Constant.RESET);
    reSetbtn.addClickListener(event -> reset());

    logoutbtn = createButton(Constant.LOG_OUT);
    clickLogoutButton(logoutbtn);

    clickSubmitButton(inputDatePicker, inputTextField, submitbtn);

    // FIX 2: was added twice — buttonHLayout.add(...) appeared twice in the original.
    HorizontalLayout buttonHLayout = new HorizontalLayout();
    buttonHLayout.add(submitbtn, reSetbtn, logoutbtn);

    HorizontalLayout buttonLayout = new HorizontalLayout();
    buttonLayout.setAlignItems(Alignment.CENTER);
    buttonLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);
    buttonLayout.setBoxSizing(BoxSizing.BORDER_BOX);
    buttonLayout.add(inputDatePicker, inputTextField);

    VerticalLayout vLayout = new VerticalLayout();
    vLayout.add(buttonHLayout, buttonLayout);
    vLayout.setAlignItems(Alignment.END);
    add(vLayout);

    return buttonLayout;
  }

  // FIX 3: replaced getUI().get() (throws if detached) with getUI().ifPresent()
  private void clickLogoutButton(Button logoutbtn) {
    logoutbtn.addClickListener(e ->
        getUI().ifPresent(ui -> ui.navigate(LoginScreenUI.class)));
  }

  private DatePicker inputDateSelection(String placeholder) {
    DatePicker datePicker = new DatePicker();
    datePicker.setPlaceholder(Constant.INPUT_DATE_PLACE_HOLDER);

    Div value = new Div();
    value.setText(placeholder);

    datePicker.addValueChangeListener(event ->
        validateInputDate(placeholder, value, event));

    return datePicker;
  }

  private void validateInputDate(String placeholder, Div value,
      ComponentValueChangeEvent<DatePicker, LocalDate> event) {

    if (event.getValue() == null) {
      value.setText(placeholder + " " + Constant.NOT_SELECTED);
      submitbtn.setEnabled(false);
      return;
    }

    // FIX 6: delegate to InputDateValidation instead of duplicating the future-date
    // check inline. validate() checks null, future dates, ordering, and max range.
    LocalDate selectedDate = event.getValue();
    if (!InputDateValidation.validate(selectedDate, LocalDate.now())) {
      createWarningDialogBox(selectedDate.toString(), true);
      submitbtn.setEnabled(false);
      return;
    }

    submitbtn.setEnabled(true);
  }

  private void reset() {
    inputTextField.setValue("");
    inputDatePicker.clear();
    addClassName(Constant.GRID_LIST_VIEW);
    grid.setItems(energyReport);
    submitbtn.setEnabled(false);
  }

  private Button createButton(String label) {
    Button button = new Button(label);
    button.addThemeName(Constant.BUTTON_THEME);
    return button;
  }

  private void clickSubmitButton(DatePicker inputDatePicker, TextField inputTextField,
      Button submitbtn) {
    submitbtn.addClickListener(e -> {

      String priceText = inputTextField.getValue();

      // FIX 1 (part a): check for empty field BEFORE attempting Double.parseDouble,
      // otherwise parseDouble("") throws NumberFormatException before the dialog opens.
      if (inputDatePicker.getValue() != null && priceText.isEmpty()) {
        createWarningDialogBox(priceText, false);
        return;
      }

      double price;
      try {
        price = Double.parseDouble(priceText);
      } catch (NumberFormatException ex) {
        createWarningDialogBox(priceText, false);
        inputTextField.clear();
        return;
      }

      // FIX 1 (part b) & FIX 7: check both lower bound (< 0) and upper bound (> MAX).
      // Original code only checked > MAX_PRICE_VALUE without guarding < 0 explicitly.
      if (price < Constant.MIN_PRICE_VALUE || price > Constant.MAX_PRICE_VALUE) {
        createWarningDialogBox(priceText, false);
        inputTextField.clear();
        return;
      }

      if (inputDatePicker.getValue() != null) {
        List<ConsumptionData> fetchByPrice = consumptionService.findByMeasurmentPrice(
            inputDatePicker.getValue().toString(), priceText);
        grid.setItems(fetchByPrice);
        submitbtn.setEnabled(false);
      }
    });
  }

  private Dialog createWarningDialogBox(String input, boolean isDateError) {
    Dialog dialog = new Dialog();
    dialog.setCloseOnEsc(false);
    dialog.setCloseOnOutsideClick(false);

    Button confirmButton = new Button("OK", event -> dialog.close());
    confirmButton.setWidthFull();

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.setAlignItems(Alignment.CENTER);
    verticalLayout.add(confirmButton);
    dialog.add(verticalLayout);

    // FIX 5: constant renamed from SERVICE_UNAVIALABLE to SERVICE_UNAVAILABLE
    if (input == null && !isDateError) {
      dialog.add(Constant.SERVICE_UNAVAILABLE);
    } else if (isDateError) {
      dialog.add(Constant.INVALID_DATE + input);
    } else {
      dialog.add(Constant.INVALID_PRICE_INPUT + input);
    }

    dialog.open();
    return dialog;
  }
}
