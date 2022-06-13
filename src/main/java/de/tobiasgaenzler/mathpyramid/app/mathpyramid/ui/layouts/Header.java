package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.events.NewGameEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout {

    private final EventBus uiEventBus;

    @Autowired
    public Header(EventBus uiEventBus) {
        setWidth("100%");
        setDefaultVerticalComponentAlignment(CENTER);
        this.uiEventBus = uiEventBus;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        removeAll();
        addLogo();
        addButtons(uiEventBus);
        addClassName("header");
    }

    private void addButtons(EventBus uiEventBus) {
        AtomicReference<String> segment = new AtomicReference<>("");
        getUI().ifPresent(ui ->
                segment.set(ui.getInternals().getActiveViewLocation().getFirstSegment())
        );
        // no buttons on help view
        if (segment.get().equals("help")) {
            return;
        }
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setAlignItems(Alignment.END);

        Button startMultiplayerGameButton = new Button("Start Multiplayer Game");
        startMultiplayerGameButton.addClickListener(event -> uiEventBus.post(new NewGameEvent(true)));
        buttonsLayout.add(startMultiplayerGameButton);

        Button startNewGameButton = new Button("Start Game");
        startNewGameButton.addClickListener(event -> uiEventBus.post(new NewGameEvent(false)));
        buttonsLayout.add(startNewGameButton);

        Button helpButton = new Button(new Icon(VaadinIcon.QUESTION_CIRCLE));
        helpButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        helpButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("help")));
        buttonsLayout.add(helpButton);

        add(buttonsLayout);
    }

    private void addLogo() {
        VerticalLayout logoLayout = new VerticalLayout();
        logoLayout.setAlignItems(Alignment.START);
        logoLayout.addClassName("logo-layout");
        Button logo = new Button("Math Pyramid");
        logo.addClassName("logo");
        logo.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);
        logo.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("/")));
        logoLayout.add(logo);
        add(logoLayout);
        expand(logo);
    }
}
