package ui;

/*
 * D Rama Kiron
 */

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * FIX 1: Credentials ("john"/"john") were hardcoded as plain-text string literals
 *   directly in this class. Moved to named constants with a comment that they must
 *   migrate to Spring Security configuration (or a UserDetailsService) before
 *   any production deployment. The PDF itself acknowledges security is a future
 *   improvement — this at minimum centralises the risk.
 *
 * FIX 2: The constant name
 *   THIS_DASH_BOARD_UI_TOOL_CAN_BE_USED_TO_MONITOR_THE_ENERGY_COMPTION_FOR_SPECIFIC_DATE_RANGE
 *   is 95 characters, misspelled ("COMPTION"), and encodes the full sentence as its
 *   identifier — a clear violation of Java naming conventions. Replaced with a short,
 *   descriptive name.
 *
 * FIX 3: serialVersionUID is declared but FlexLayout is not Serializable in the
 *   standard sense — the field has no effect here and just adds noise. Removed.
 */
@Route("Login")
@PageTitle("Log in")
@StyleSheet("frontend://styles/shared-styles.css")
public class LoginScreenUI extends FlexLayout {

  private static final String LOGIN_SCREEN     = "login-screen";
  private static final String LOGIN_INFORMATION = "login-information";
  private static final String DASHBOARD_TITLE  = "Energy Consumption Dashboard";
  private static final String DASHBOARD_DESCRIPTION =
      "This dashboard UI tool can be used to monitor energy consumption with respect to price for a specific date range.";

  // FIX 1: credentials centralised here.
  // TODO: replace with Spring Security UserDetailsService before production.
  private static final String DEFAULT_USERNAME = "john";
  private static final String DEFAULT_PASSWORD = "john";

  public LoginScreenUI() {
    buildUI();
  }

  private void buildUI() {
    setSizeFull();
    setClassName(LOGIN_SCREEN);

    LoginForm loginForm = new LoginForm();
    loginForm.addLoginListener(this::login);
    loginForm.addForgotPasswordListener(
        event -> Notification.show("Hint: same as username"));

    FlexLayout centeringLayout = new FlexLayout();
    centeringLayout.setSizeFull();
    centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    centeringLayout.setAlignItems(Alignment.CENTER);
    centeringLayout.add(loginForm);

    add(buildLoginInformation());
    add(centeringLayout);
  }

  private Component buildLoginInformation() {
    VerticalLayout loginInformation = new VerticalLayout();
    loginInformation.setAlignItems(Alignment.CENTER);
    loginInformation.setClassName(LOGIN_INFORMATION);
    loginInformation.add(new H1(DASHBOARD_TITLE));
    loginInformation.add(new Span(DASHBOARD_DESCRIPTION));
    return loginInformation;
  }

  private void login(LoginForm.LoginEvent event) {
    if (event.getUsername().equals(DEFAULT_USERNAME)
        && event.getPassword().equals(DEFAULT_PASSWORD)) {
      getUI().ifPresent(ui -> ui.navigate("Main"));
    } else {
      event.getSource().setError(true);
    }
  }
}
