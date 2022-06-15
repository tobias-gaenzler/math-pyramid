package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.practice;

public interface PracticeViewListener {
    void setView(PracticeView practiceView);

    void startGame();

    void pyramidBlockChanged(int currentRow, int currentColumn, Integer value);
}
