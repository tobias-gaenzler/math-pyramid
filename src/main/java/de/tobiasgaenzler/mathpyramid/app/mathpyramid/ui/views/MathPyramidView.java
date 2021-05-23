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
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.MainLayout;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.NewGameEvent;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.NewMultiplayerGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster.broadcast;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class) // use this view as default view ("/"), set this as content in MainLayout
// Local styles for text fields (can style shadow dom, parts, ...)
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Math-Pyramid")
public class MathPyramidView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(MathPyramidView.class);
    private final MathPyramidModelFactory mathPyramidModelFactory;
    private final MathPyramidLayout layout;
    private final EventBus uiEventBus;
    private final MathPyramidCalculator calculator;
    private final MathPyramidConfiguration config;
    private MathPyramidModel model;
    private Registration broadcasterRegistration;
    private Integer size;
    // Use this variable to show finish notification only once
    // model can not be used because it is shared between all participants in multiplayer mode
    private Boolean multiplayerGameInProgress = false;

    @Autowired
    public MathPyramidView(MathPyramidConfiguration config, MathPyramidModelFactory mathPyramidModelFactory,
                           MathPyramidLayout layout, EventBus uiEventBus, MathPyramidCalculator calculator) {
        this.config = config;
        this.mathPyramidModelFactory = mathPyramidModelFactory;
        this.layout = layout;
        this.uiEventBus = uiEventBus;
        this.calculator = calculator;
        addClassName("app-layout");
        logger.debug("Initializing new view");
        createModel(false);
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
                logger.debug("Received broadcast message: {} for player: {}", object, getPlayerName());
                if (object instanceof MathPyramidModel) {
                    refresh((MathPyramidModel) object);
                } else {
                    // ensure that finish notification is displayed only once per game
                    if (model.getMultiplayerGame() && isMultiplayerGameInProgress()) {
                        createNotification(((String) object)).open();
                    }
                    if (isMultiplayerGameInProgress()) {
                        endMultiplayerGame();
                    }
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
        logger.info("New single player game started by player {}", getPlayerName());
        createModel(false);
        refresh(this.model);

    }

    private String getPlayerName() {
        return (String) UI.getCurrent().getSession().getAttribute("username");
    }

    @Subscribe
    public void newMultiplayerGameEventReceived(NewMultiplayerGameEvent event) {
        logger.info("New multiplayer game started by player {}", getPlayerName());
        createModel(true);
        broadcast(this.model);
    }

    private void refresh(MathPyramidModel model) {
        logger.info("Refreshing model of player: {}", getPlayerName());
        this.model = new MathPyramidModel(model);
        removeAll();
        layout.init(this.model.getSize());
        add(layout);
        bind();
        if (this.model.getMultiplayerGame()) {
            startMultiplayerGame();
        }
    }

    private void createModel(boolean multiplayer) {
        logger.info("Creating new model, multiplayer: {}, player: {}", multiplayer, getPlayerName());
        int maxValue = config.getMaxValue();
        if (size == null) {
            size = config.getDefaultSize();
        }
        model = mathPyramidModelFactory.createMathPyramid(size, maxValue);
        model.setMultiplayerGame(multiplayer);
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
            String username = getPlayerName();
            logger.debug("Received input row {}, column {}: value {}, player {}",
                    currentRow,
                    currentColumn,
                    textField.getValue(),
                    username);
            model.setUserInput(currentRow, currentColumn, textField.getValue());
            updatePyramidBlock(currentRow, currentColumn, textField);
            if (model.isSolved()) {
                if (model.getMultiplayerGame()) {
                    logger.info("Multiplayer game finished by player {}", username);
                    broadcast("Solved by " + username);
                } else {
                    logger.info("Single player game finished by player {}", username);
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
        notification.setDuration(0);
        notification.setPosition(Notification.Position.MIDDLE);
        closeButton.addClickListener(closeButtonClickedEvent -> notification.close());
        return notification;
    }

    private boolean isMultiplayerGameInProgress() {
        return multiplayerGameInProgress;
    }

    private void endMultiplayerGame() {
        multiplayerGameInProgress = false;
    }

    private void startMultiplayerGame() {
        multiplayerGameInProgress = true;
    }

}