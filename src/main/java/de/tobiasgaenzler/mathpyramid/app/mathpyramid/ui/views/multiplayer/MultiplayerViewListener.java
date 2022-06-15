package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views.multiplayer;

import com.vaadin.flow.component.UI;

public interface MultiplayerViewListener {
    void register(UI ui, MultiplayerView multiplayerView);

    void startGame();

    void unregister(UI ui);

    void pyramidBlockChanged(int currentRow, int currentColumn, Integer value);
}
