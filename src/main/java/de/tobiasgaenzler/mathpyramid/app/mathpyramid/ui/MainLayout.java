package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Theme(value = Lumo.class)
// Global styles apply only to elements which are not located in shadow dom
@CssImport("./styles/shared-styles.css")
@Push
@PreserveOnRefresh
public class MainLayout extends AppLayout {

    private final Logger logger = LoggerFactory.getLogger(MainLayout.class);
    private final EventBus uiEventBus;
    private final UserService userService;

    @Autowired
    public MainLayout(EventBus uiEventBus, UserService userService) {
        this.uiEventBus = uiEventBus;
        this.userService = userService;
        logger.debug("Creating MainLayout {} for user {}", this, userService.getUserName());
        createHeader();
    }

    private void createHeader() {
        H1 logo = new H1("Math Pyramid");
        logo.addClassName("logo");
        H1 id = new H1("Name: " + userService.getUserName());
        id.addClassName("id");

        Button newGame = new Button("New Game");
        newGame.addClickListener(event ->
                uiEventBus.post(new NewGameEvent(false))
        );
        Button newMultiplayerGame = new Button("New Multiplayer Game");
        newMultiplayerGame.addClickListener(event -> uiEventBus.post(new NewGameEvent(true)));

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, id, newMultiplayerGame, newGame);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        logger.debug("Detaching MainLayout {} for user {}", this, userService.getUserName());
    }
}
