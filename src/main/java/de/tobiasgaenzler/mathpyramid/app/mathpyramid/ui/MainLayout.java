package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.MathPyramidView;

@Theme(value = Lumo.class)
// Global styles apply only to elements which are not located in shadow dom
@CssImport("./styles/shared-styles.css")
@Push
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Math Pyramid");
        logo.addClassName("logo");

        // TODO: once content is preserved on reload we need a better solution.
        Anchor newGame = new Anchor("/", "New Game");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, newGame);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink mathPyramidGameLink = new RouterLink("Play", MathPyramidView.class);
        mathPyramidGameLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                mathPyramidGameLink
        ));
        setDrawerOpened(false);
    }
}
