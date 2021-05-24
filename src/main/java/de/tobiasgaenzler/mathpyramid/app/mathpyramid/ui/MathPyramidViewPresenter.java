package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.MathPyramidView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster.broadcast;

@Component
@UIScope
public class MathPyramidViewPresenter implements MathPyramidViewListener {
    private final MathPyramidConfiguration config;
    private final Logger logger = LoggerFactory.getLogger(MathPyramidViewPresenter.class);
    private final MathPyramidViewModelFactory factory;
    private final NotificationService notificationService;
    private final UserService userService;
    private MathPyramidViewModel model;
    private Registration broadcasterRegistration;
    private MathPyramidView view;

    @Autowired
    public MathPyramidViewPresenter(MathPyramidConfiguration config,
                                    MathPyramidViewModelFactory factory,
                                    EventBus uiEventBus,
                                    NotificationService notificationService,
                                    UserService userService) {
        this.config = config;
        this.factory = factory;
        this.notificationService = notificationService;
        this.userService = userService;
        createModel(false);
        uiEventBus.register(this);
    }

    @Subscribe
    public void newGameEventReceived(NewGameEvent event) {
        startGame(event.getMultiPlayer());
    }

    @Override
    public void startGame(Boolean multiPlayerGame) {
        logger.info("New game started by player {}, multiplayer: {}", userService.getUserName(), multiPlayerGame);
        createModel(multiPlayerGame);
        if (multiPlayerGame) {
            model.startMultiplayerGame();
            broadcast(model);
        } else {
            view.refreshView(this.model);
        }
    }

    public void unregister() {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
        }
        broadcasterRegistration = null;
    }

    public void register(UI ui, MathPyramidView mathPyramidView) {
        view = mathPyramidView;
        if (broadcasterRegistration == null) {
            broadcasterRegistration = Broadcaster.register(object -> broadcastListener(ui, object));
        }
    }

    private void broadcastListener(UI ui, Object object) {
        ui.access(() -> {
            logger.debug("Received broadcast message: {} for player: {}", object, userService.getUserName());
            if (object instanceof MathPyramidViewModel) {
                model = new MathPyramidViewModel(((MathPyramidViewModel) object));
                view.refreshView(model);
            } else {
                // ensure that finish notification is displayed only once per game
                if (model.getMultiplayerGame() && model.isMultiplayerGameInProgress()) {
                    notificationService.createNotification(((String) object)).open();
                }
                if (model.isMultiplayerGameInProgress()) {
                    model.endMultiplayerGame();
                }
            }
        });
    }

    private void createModel(boolean multiplayer) {
        logger.info("Creating new model, multiplayer: {}, player: {}", multiplayer, userService.getUserName());
        model = factory.create(config.getDefaultSize(), config.getMaxValue());
        model.setMultiplayerGame(multiplayer);
    }

    public void gameFinished() {
        if (model.getMultiplayerGame()) {
            logger.info("Multiplayer game finished by player {}", userService.getUserName());
            broadcast("Solved by " + userService.getUserName());
        } else {
            logger.info("Single player game finished by player {}", userService.getUserName());
            notificationService.createNotification("Solved! Congratulations!").open();
        }
    }

    public void pyramidBlockChanged(int currentRow, int currentColumn, Integer inputValue) {
        logger.debug("Received input row {}, column {}: inputValue {}, player {}",
                currentRow,
                currentColumn,
                inputValue,
                userService.getUserName());
        // store user input in model
        model.setUserInput(currentRow, currentColumn, inputValue);
        view.updatePyramidBlock(currentRow, currentColumn, isUserInputCorrect(currentRow, currentColumn));
        if (isSolved()) {
            gameFinished();
        }
    }

    private boolean isSolved() {
        for (int row = 0; row < model.getSize(); row++) {
            for (int column = 0; column < model.getSize() - row; column++) {
                if (model.isUserInput(row, column) && !isUserInputCorrect(row, column)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isUserInputCorrect(Integer row, Integer column) {
        model.getCalculator().checkDimensions(row, column, model.getSize());
        Integer inputValue = model.getUserInput(row, column);
        Integer solutionValue = model.getSolutionAt(row, column);
        return solutionValue.equals(inputValue);
    }


}
