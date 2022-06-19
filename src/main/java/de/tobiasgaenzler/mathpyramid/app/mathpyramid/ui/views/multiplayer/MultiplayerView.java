package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.multiplayer;

import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;

public interface MultiplayerView {
    void refreshView(MathPyramidViewModel model);
    void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved);
    void addSolvedMessage(String message);
}
