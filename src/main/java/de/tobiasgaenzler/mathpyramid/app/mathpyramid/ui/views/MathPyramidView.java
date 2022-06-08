package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.UI;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;

import java.util.Optional;

public interface MathPyramidView {
    void refreshView(MathPyramidViewModel model);
    void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved);
}
