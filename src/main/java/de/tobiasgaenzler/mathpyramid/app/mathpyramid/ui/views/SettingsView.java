package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Math Pyramid - Settings")
@Component
public class SettingsView extends VerticalLayout {
    private final Logger logger = LoggerFactory.getLogger(SettingsView.class);

    @Autowired
    public SettingsView(MathPyramidConfiguration config) {
        setAlignItems(CENTER);
        addClassName("settings");
        VerticalLayout settingsForm = new VerticalLayout();
        IntegerField sizeField = new IntegerField("Size:");
        sizeField.setValue(config.getDefaultSize());
        sizeField.setAutofocus(true);
        settingsForm.add(sizeField);
        IntegerField maxValueField = new IntegerField("Max value:");
        maxValueField.setValue(config.getMaxValue());
        settingsForm.add(maxValueField);
        Button saveButton = new Button("Save");
        saveButton.addClickShortcut(Key.ENTER);

        saveButton.addClickListener(e -> {
                    logger.info("Changing config to size: {},max value: {}", sizeField.getValue(), maxValueField.getValue());
                    config.setDefaultSize(sizeField.getValue());
                    config.setMaxValue(maxValueField.getValue());
                }
        );
        settingsForm.add(saveButton);
        add(settingsForm);
    }
}
