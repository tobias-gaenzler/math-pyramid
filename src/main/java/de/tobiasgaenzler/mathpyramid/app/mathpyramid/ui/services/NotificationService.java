package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Service;

import static com.vaadin.flow.component.notification.Notification.Position.MIDDLE;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@Service
public class NotificationService {
    public Notification createNotification(String message) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(CENTER);
        Span content = new Span(message);
        layout.add(content);
        Button closeButton = new Button("Close");
        layout.add(closeButton);
        Notification notification = new Notification(layout);
        notification.setDuration(5_000);
        notification.addThemeVariants(LUMO_SUCCESS);
        notification.setPosition(MIDDLE);
        closeButton.addClickListener(closeButtonClickedEvent -> notification.close());
        return notification;
    }

}
