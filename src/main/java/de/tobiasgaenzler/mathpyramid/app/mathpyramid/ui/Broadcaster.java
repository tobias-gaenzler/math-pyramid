package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {

    private static final Logger logger = LoggerFactory.getLogger(Broadcaster.class);
    static final Executor executor = Executors.newSingleThreadExecutor();

    static final LinkedList<Consumer<Object>> listeners = new LinkedList<>();

    public static synchronized Registration register(
            Consumer<Object> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(Object message) {
        logger.debug("Broadcasted message: {}", message);
        for (Consumer<Object> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }
}

