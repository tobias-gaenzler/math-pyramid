package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.practice;

import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.NotificationService;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class PracticeViewPresenter implements PracticeViewListener {
    private final MathPyramidConfiguration config;
    private final Logger logger = LoggerFactory.getLogger(PracticeViewPresenter.class);
    private final MathPyramidViewModelFactory factory;
    private final NotificationService notificationService;
    private final UserService userService;
    private MathPyramidViewModel model;
    private PracticeView view;

    @Autowired
    public PracticeViewPresenter(MathPyramidConfiguration config, MathPyramidViewModelFactory factory, NotificationService notificationService, UserService userService) {
        this.config = config;
        this.factory = factory;
        this.notificationService = notificationService;
        this.userService = userService;
        createModel();
    }

    @Override
    public void startGame() {
        logger.info("New practice started by player {}", userService.getUserName());
        createModel();
        view.refreshView(this.model);
    }

    @Override
    public void setView(PracticeView practiceView) {
        view = practiceView;
    }

    @Override
    public void pyramidBlockChanged(int currentRow, int currentColumn, Integer inputValue) {
        logger.debug("Received input row {}, column {}: inputValue {}, player {}, model {}", currentRow, currentColumn, inputValue, userService.getUserName(), model);
        // store user input in model
        model.setUserInput(currentRow, currentColumn, inputValue);
        view.updatePyramidBlock(currentRow, currentColumn, model.isUserInputCorrect(currentRow, currentColumn));
        if (model.isSolved()) {
            gameFinished();
        }
    }

    private void createModel() {
        logger.info("Creating new model, player: {}", userService.getUserName());
        model = factory.create(config.getDefaultSize(), config.getMaxValue());
        model.setMultiplayerGame(false);
    }

    public void gameFinished() {
        logger.info("Practice finished by player {}", userService.getUserName());
        notificationService.createNotification("Solved! Congratulations!").open();
    }
}
