package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;

public interface MathPyramidView {
    void refreshView(MathPyramidViewModel model);
    void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved);
}
