package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Component
@UIScope
public class TimerService {
    private Instant gameTimer = null;

    public void startGame() {
        gameTimer = Instant.now();
    }
    public String getGameDuration() {
        long duration = Duration.between(gameTimer, Instant.now()).toMillis();
        return String.format("%d sec, %d ms",
                MILLISECONDS.toSeconds(duration),
                duration - SECONDS.toMillis(MILLISECONDS.toSeconds(duration))

        );
    }
}
