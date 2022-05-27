package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.events.NewGameEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout {

    @Autowired
    public Header(EventBus uiEventBus) {
        H1 logo = new H1("Math Pyramid");
        logo.addClassName("logo");
        add(logo);

        Button startMultiplayerGameButton = new Button("Start Multiplayer Game");
        startMultiplayerGameButton.addClickListener(event -> uiEventBus.post(new NewGameEvent(true)));
        add(startMultiplayerGameButton);

        Button startNewGameButton = new Button("Start Game");
        startNewGameButton.addClickListener(event -> uiEventBus.post(new NewGameEvent(false)));
        add(startNewGameButton);

        addClassName("header");
        setWidth("100%");
        expand(logo);
        setDefaultVerticalComponentAlignment(CENTER);
    }
}
