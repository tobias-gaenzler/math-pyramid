package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.UI;

public interface MathPyramidViewListener {
    void register(UI ui, MathPyramidView mathPyramidView);

    void startGame(Boolean multiPlayerGame);

    void unregister();

    void pyramidBlockChanged(int currentRow, int currentColumn, Integer value);
}
