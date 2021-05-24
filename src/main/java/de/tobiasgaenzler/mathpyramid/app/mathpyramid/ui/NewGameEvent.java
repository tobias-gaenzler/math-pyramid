package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

public class NewGameEvent {
    private final Boolean multiPlayer;

    public NewGameEvent(Boolean multiPlayer) {
        this.multiPlayer = multiPlayer;
    }

    public Boolean getMultiPlayer() {
        return multiPlayer;
    }
}
