package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.events.NewGameEvent;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.HelpView;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.training.TrainingVaadinView;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.training.TrainingView;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout implements BeforeEnterObserver {

    private final EventBus uiEventBus;

    @Autowired
    public Header(EventBus uiEventBus) {
        setWidth("100%");
        setDefaultVerticalComponentAlignment(CENTER);
        this.uiEventBus = uiEventBus;
        addClassName("header");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        addLogo();
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setAlignItems(Alignment.END);
        if (event.getNavigationTarget().equals(HelpView.class)
                || event.getNavigationTarget().equals(TrainingVaadinView.class)) {
            addHelpAndTrainingButtons(buttonsLayout);
        } else {
            addAllButtons(uiEventBus, buttonsLayout);
        }
        add(buttonsLayout);
    }

    private void addAllButtons(EventBus uiEventBus, HorizontalLayout buttonsLayout) {
        Button startMultiplayerGameButton = new Button("Start Multiplayer Game");
        startMultiplayerGameButton.addClickListener(event -> uiEventBus.post(new NewGameEvent(true)));
        buttonsLayout.add(startMultiplayerGameButton);

        addHelpAndTrainingButtons(buttonsLayout);

    }

    private void addHelpAndTrainingButtons(HorizontalLayout buttonsLayout) {
        Button trainingButton = new Button("Start Game (Practice)");
        trainingButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("training")));
        buttonsLayout.add(trainingButton);

        Button helpButton = new Button(new Icon(VaadinIcon.QUESTION_CIRCLE));
        helpButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        helpButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("help")));
        buttonsLayout.add(helpButton);
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
