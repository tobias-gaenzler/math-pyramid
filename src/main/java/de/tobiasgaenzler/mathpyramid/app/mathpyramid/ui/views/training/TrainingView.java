package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.training;

import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;

public interface TrainingView {
    void refreshView(MathPyramidViewModel model);
    void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved);
}
