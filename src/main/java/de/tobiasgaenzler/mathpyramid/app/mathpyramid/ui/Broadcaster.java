package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class Broadcaster implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(Broadcaster.class);
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final Map<UI, BroadcastListener> listeners = new ConcurrentHashMap<>();

    public void register(UI ui, BroadcastListener listener) {
        logger.debug("registering listener {} for UI {}", listener, ui);
        listeners.put(ui, listener);
    }

    public void unregister(UI ui) {
        listeners.remove(ui);
    }

    public void broadcast(Object message) {
        logger.debug("Broadcasted message to {} listeners {}", listeners.size(), message);
        listeners.forEach((ui, listener) -> executor.execute(() -> listener.receiveBroadcast(ui, message)));
    }

    public interface BroadcastListener {
        void receiveBroadcast(UI ui, Object message);
    }
}

