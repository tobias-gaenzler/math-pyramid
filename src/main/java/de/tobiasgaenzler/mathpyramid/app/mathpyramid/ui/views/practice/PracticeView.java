package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.practice;

import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;

public interface PracticeView {
    void refreshView(MathPyramidViewModel model);
    void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved);
}
