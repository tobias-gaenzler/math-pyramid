package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.multiplayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.TimerService;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class MultiplayerViewPresenter implements MultiplayerViewListener {
    private final MathPyramidConfiguration config;
    private final Logger logger = LoggerFactory.getLogger(MultiplayerViewPresenter.class);
    private final MathPyramidViewModelFactory factory;
    private final UserService userService;
    private final Broadcaster broadcaster;
    private MathPyramidViewModel model;
    private MultiplayerView view;
    private final TimerService timerService;

    @Autowired
    public MultiplayerViewPresenter(MathPyramidConfiguration config,
                                    MathPyramidViewModelFactory factory,
                                    UserService userService,
                                    Broadcaster broadcaster,
                                    TimerService timerService) {
        this.config = config;
        this.factory = factory;
        this.userService = userService;
        this.broadcaster = broadcaster;
        this.timerService = timerService;
        createModel();
    }

    public void register(UI ui, MultiplayerView multiplayerView) {
        view = multiplayerView;
        unregister(ui);
        logger.info("Registering UI {}, ", ui.getUIId());
        broadcaster.register(ui, this::broadcastListener);
    }

    public void unregister(UI ui) {
        logger.info("Unregistering UI {}, ", ui.getUIId());
        broadcaster.unregister(ui);
    }

    @Override
    public void startGame() {
        logger.info("New multiplayer game started by player {}, ", userService.getUserName());
        createModel();
        model.startMultiplayerGame();
        broadcaster.broadcast(model);
    }

    public void gameFinished() {
        logger.info("Multiplayer game finished by player {}", userService.getUserName());
        broadcaster.broadcast("Solved by " + userService.getUserName() + " in " + timerService.getGameDuration());
    }

    public void pyramidBlockChanged(int currentRow, int currentColumn, Integer inputValue) {
        logger.info("Received input for block row {}, column {}: inputValue {}, player {}, model {}",
                currentRow,
                currentColumn,
                inputValue,
                userService.getUserName(),
                model);
        // store user input in model
        model.setUserInput(currentRow, currentColumn, inputValue);
        view.updatePyramidBlock(currentRow, currentColumn, model.isUserInputCorrect(currentRow, currentColumn));
        if (model.isSolved()) {
            gameFinished();
        }
    }

    private void broadcastListener(UI ui, Object message) {
        ui.access(() -> {
            logger.info("Received broadcast message: {} for player: {}, UI: {}",
                    message,
                    userService.getUserName(),
                    ui
            );
            if (message instanceof MathPyramidViewModel) {
                logger.info("Received new model for player {}, starting game", userService.getUserName());
                model = new MathPyramidViewModel(((MathPyramidViewModel) message));
                view.refreshView(model);
                timerService.startGame();
            } else {
                if (model.isMultiplayerGameInProgress()) {
                    view.addSolvedMessage((String) message);
                    logger.info("Received message, that game is solved (user: {})", userService.getUserName());
                } else {
                    logger.info("Ignoring message as no game is in progress (user: {})", userService.getUserName());
                }
            }
        });
    }

    private void createModel() {
        logger.info("Creating new multiplayer model for player: {}", userService.getUserName());
        model = factory.create(config.getDefaultSize(), config.getMaxValue());
    }
}
