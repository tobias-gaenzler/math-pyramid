package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidCalculator;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster.broadcast;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class) // use this view as default view ("/"), set this as content in MainLayout
// Local styles for text fields (can style shadow dom, parts, ...)
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Math-Pyramid")
public class MathPyramidView extends VerticalLayout {

    public static final int DEFAULT_SIZE = 3;
    public static final int DEFAULT_MAX_VALUE = 100;
    private final Environment env;
    private final MathPyramidModelFactory mathPyramidModelFactory;
    private final MathPyramidLayout layout;
    private final EventBus uiEventBus;
    private final MathPyramidCalculator calculator;
    private MathPyramidModel model;
    private Registration broadcasterRegistration;
    private Integer size;

    @Autowired
    public MathPyramidView(Environment env, MathPyramidModelFactory mathPyramidModelFactory,
                           MathPyramidLayout layout, EventBus uiEventBus, MathPyramidCalculator calculator) {
        this.env = env;
        this.mathPyramidModelFactory = mathPyramidModelFactory;
        this.layout = layout;
        this.uiEventBus = uiEventBus;
        this.calculator = calculator;
        addClassName("app-layout");
        createModel(true);
        refresh(this.model);
    }

    public void bind() {
        int size = model.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - row; column++) {
                bindPyramidBlock(row, column);
            }
        }
    }

    public void updatePyramidBlock(final int currentRow, final int currentColumn, IntegerField textField) {
        textField.removeClassNames("correct", "incorrect");
        Integer input = textField.getValue();
        if (model.isUserInputCorrect(currentRow, currentColumn)) {
            textField.addClassName("correct");
            textField.setReadOnly(true);
        } else if (input != null) {
            textField.addClassName("incorrect");
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        uiEventBus.register(this);
        UI ui = attachEvent.getUI();
        if (broadcasterRegistration == null) {
            broadcasterRegistration = Broadcaster.register(object -> ui.access(() -> {
                if(object instanceof  MathPyramidModel) {
                    refresh((MathPyramidModel) object);
                } else {
                    createNotification(((String) object)).open();
                }
            }));
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
        }
        broadcasterRegistration = null;
    }

    @Subscribe
    public void newGameEventReceived(NewGameEvent event) {
        createModel(false);
        refresh(this.model);

    }

    @Subscribe
    public void newMultiplayerGameEventReceived(NewMultiplayerGameEvent event) {
        createModel(true);
        broadcast(this.model);
    }

    @Subscribe
    public void harder(IncreaseDifficultyEvent event) {
        if (size < 10) {
            size++;
        } else {
            return;
        }
        createModel(true);
        refresh(this.model);
    }

    @Subscribe
    public void easier(DecreaseDifficultyEvent event) {
        if(size > 3) {
            size--;
        } else {
            return;
        }
        createModel(true);
        refresh(this.model);
    }

    private void refresh(MathPyramidModel model) {
        this.model = model;
        removeAll();
        layout.init(model.getSize());
        add(layout);
        bind();
    }

    private void createModel(boolean multiplayer) {
        int maxValue = env.getProperty("math-pyramid.max-value", Integer.class, DEFAULT_MAX_VALUE);
        if(size == null) {
            size = env.getProperty("math-pyramid.default-size", Integer.class, DEFAULT_SIZE);
        }
        model = mathPyramidModelFactory.createMathPyramid(size, maxValue);
        model.setMultiplayer(multiplayer);
    }

    private void bindPyramidBlock(int row, int column) {
        int fieldIndex = calculator.getIndex(row, column, model.getSize());
        IntegerField textField = layout.getPyramidBlocks().get(fieldIndex);
        if (model.isUserInput(row, column)) {
            addValueChangeListener(row, column, textField);
        } else {
            textField.setValue(model.getSolutionAt(row, column));
            textField.setReadOnly(true);
        }
    }

    private void addValueChangeListener(final int currentRow, final int currentColumn, IntegerField textField) {
        textField.addValueChangeListener(event -> {
            // store user input in model
            model.setUserInput(currentRow, currentColumn, textField.getValue());
            updatePyramidBlock(currentRow, currentColumn, textField);
            if (model.isSolved()) {
                if(model.getMultiplayer()) {
                    broadcast("Solved by " + UI.getCurrent().getSession().getAttribute("username"));
                } else{
                    createNotification("Solved! Congratulations!").open();
                }
            }
        });
    }

    private Notification createNotification(String message) {
        VerticalLayout layout = new VerticalLayout();
        Span content = new Span(message);
        layout.add(content);
        Button closeButton = new Button("Close");
        layout.add(closeButton);
        Notification notification = new Notification(layout);
        notification.setDuration(3000);
        notification.setPosition(Notification.Position.MIDDLE);
        closeButton.addClickListener(closeButtonClickedEvent -> notification.close());
        return notification;
    }
}