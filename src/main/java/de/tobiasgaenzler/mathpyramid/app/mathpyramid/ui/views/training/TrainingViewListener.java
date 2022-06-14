package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.training;

public interface TrainingViewListener {
    void setView(TrainingView trainingView);

    void startGame();

    void pyramidBlockChanged(int currentRow, int currentColumn, Integer value);
}
