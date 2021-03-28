package de.tobiasgaenzler.mathpyramid.app.mathpyramid;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @UIScope
    @Bean(name = "uiEventBus")
    public EventBus eventBus() {
        return new EventBus();
    }
}