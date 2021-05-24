package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.component.UI;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.MathPyramidView;

public interface MathPyramidViewListener {
    void register(UI ui, MathPyramidView mathPyramidView);
    void startGame(Boolean multiPlayerGame);
    void unregister();
    void pyramidBlockChanged(int currentRow, int currentColumn, Integer value);
}
