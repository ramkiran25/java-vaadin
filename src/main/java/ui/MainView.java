package ui;

/*
 * D Rama Kiron
 *
 * NOTE: This class is currently empty and serves no runtime purpose.
 *
 * In a Vaadin Flow application the root layout is typically defined by annotating
 * a class with @Route("") (empty route = root path). MainFormUI is already mapped
 * to @Route("Main") and LoginScreenUI to @Route("Login"), so neither uses this
 * class as a parent layout.
 *
 * If a shared shell layout (navigation bar, footer, side menu) is needed in the
 * future, implement RouterLayout here:
 *
 *   public class MainView extends VerticalLayout implements RouterLayout { ... }
 *
 * and reference it in the child routes:
 *
 *   @Route(value = "Main", layout = MainView.class)
 *
 * Until then, @SpringComponent + @UIScope on an empty VerticalLayout is dead code.
 * It is retained here as a placeholder with this explanation rather than deleted,
 * in case the original author intended to use it as a shell layout.
 */

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class MainView extends VerticalLayout {

  private static final long serialVersionUID = 1L;

}
