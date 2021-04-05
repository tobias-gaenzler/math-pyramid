package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;


import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.MathPyramidView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Theme(value = Lumo.class)
// Global styles apply only to elements which are not located in shadow dom
@CssImport("./styles/shared-styles.css")
@Push
@PreserveOnRefresh
public class MainLayout extends AppLayout {

    private final EventBus uiEventBus;

    @Autowired
    public MainLayout(EventBus uiEventBus) {
        this.uiEventBus = uiEventBus;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Math Pyramid");
        logo.addClassName("logo");
        if (UI.getCurrent().getSession().getAttribute("username") == null) {
            UI.getCurrent().getSession().setAttribute("username", UUID.randomUUID().toString().substring(0, 5));
        }
        H1 id = new H1("Name: " + UI.getCurrent().getSession().getAttribute("username"));
        id.addClassName("id");

        Button newGame = new Button("New Game");
        newGame.addClickListener(event ->
                uiEventBus.post(new NewGameEvent())
        );
        Button newMultiplayerGame = new Button("New Multiplayer Game");
        newMultiplayerGame.addClickListener(event -> uiEventBus.post(new NewMultiplayerGameEvent()));
        Button harderButton = new Button("bigger");
        harderButton.addClickListener(event -> uiEventBus.post(new IncreaseDifficultyEvent()));
        Button easierButton = new Button("smaller");
        easierButton.addClickListener(event -> uiEventBus.post(new DecreaseDifficultyEvent()));

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, id, easierButton, harderButton,
                newMultiplayerGame, newGame);
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
