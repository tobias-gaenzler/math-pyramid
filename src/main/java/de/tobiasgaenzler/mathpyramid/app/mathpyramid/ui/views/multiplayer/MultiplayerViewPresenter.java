package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.multiplayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.NotificationService;
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
    private final NotificationService notificationService;
    private final UserService userService;
    private final Broadcaster broadcaster;
    private MathPyramidViewModel model;
    private MultiplayerView view;

    @Autowired
    public MultiplayerViewPresenter(MathPyramidConfiguration config,
                                    MathPyramidViewModelFactory factory,
                                    NotificationService notificationService,
                                    UserService userService,
                                    Broadcaster broadcaster) {
        this.config = config;
        this.factory = factory;
        this.notificationService = notificationService;
        this.userService = userService;
        this.broadcaster = broadcaster;
        createModel();
    }

    @Override
    public void startGame() {
        logger.info("New multiplayer game started by player {}, ", userService.getUserName());
        createModel();
        model.startMultiplayerGame();
        broadcaster.broadcast(model);
    }

    public void unregister(UI ui) {
        broadcaster.unregister(ui);
    }

    public void register(UI ui, MultiplayerView multiplayerView) {
        view = multiplayerView;
        unregister(ui);
        broadcaster.register(ui, this::broadcastListener);
    }

    private void broadcastListener(UI ui, Object message) {
        ui.access(() -> {
            logger.debug("Received broadcast message: {} for player: {}, UI: {}",
                    message,
                    userService.getUserName(),
                    ui
            );
            if (message instanceof MathPyramidViewModel) {
                model = new MathPyramidViewModel(((MathPyramidViewModel) message));
                view.refreshView(model);
            } else {
                if (model.getMultiplayerGame() && model.isMultiplayerGameInProgress()) {
                    notificationService.createNotification(((String) message)).open();
                }
            }
        });
    }

    private void createModel() {
        logger.info("Creating new multiplayer model for player: {}", userService.getUserName());
        model = factory.create(config.getDefaultSize(), config.getMaxValue());
        model.setMultiplayerGame(true);
    }

    public void gameFinished() {
        if (model.getMultiplayerGame()) {
            logger.info("Multiplayer game finished by player {}", userService.getUserName());
            broadcaster.broadcast("Solved by " + userService.getUserName());
        } else {
            logger.info("Single player game finished by player {}", userService.getUserName());
            notificationService.createNotification("Solved! Congratulations!").open();
        }
    }

    public void pyramidBlockChanged(int currentRow, int currentColumn, Integer inputValue) {
        logger.debug("Received input row {}, column {}: inputValue {}, player {}, model {}", currentRow, currentColumn, inputValue, userService.getUserName(), model);
        // store user input in model
        model.setUserInput(currentRow, currentColumn, inputValue);
        view.updatePyramidBlock(currentRow, currentColumn, model.isUserInputCorrect(currentRow, currentColumn));
        if (model.isSolved()) {
            gameFinished();
        }
    }
}
