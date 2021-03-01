package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.base.Strings;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramid;

import java.util.HashMap;
import java.util.Map;

@Route
@Theme(value = Lumo.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MathPyramidView extends VerticalLayout {

    private final MathPyramid mathPyramid;
    private final MathPyramidLayout layout;
    private final MathPyramidModel model;
    private Map<TextField, Registration> valueChangeListeners = new HashMap<>();

    public MathPyramidView() {
        addClassName("app-layout");
        this.mathPyramid = new MathPyramid(3, 100);
        model = new MathPyramidModel(mathPyramid);
        layout = new MathPyramidLayout(model.getSize());
        add(layout);
        bind();
    }

    public void bind() {
        int size = model.getSize();
        for (int row = 0; row < size; row++) {
            final int currentRow = row;
            for (int column = 0; column < size - row; column++) {
                final int currentColumn = column;
                TextField textField = layout.getPyramidBlocks().get(model.getIndex(row, column));
                if (!model.isUserInput(row, column)) {
                    textField.setValue(model.getSolution(row, column));
                    textField.setReadOnly(true);
                } else {
                    addValueChangeListener(currentRow, currentColumn, textField);
                }
            }
        }

    }

    private void addValueChangeListener(final int currentRow, final int currentColumn, TextField textField) {
        if (valueChangeListeners.get(textField) == null) {
            Registration registration = textField.addValueChangeListener(event -> {
                // store user input in model
                model.setUserInput(currentRow, currentColumn, textField.getValue());
                if (model.isSolved()) {
                    showSuccessNotification();
                }
                updateField(currentRow, currentColumn, textField);

            });
            valueChangeListeners.put(textField, registration);
        }
    }

    public void updateField(final int currentRow, final int currentColumn, TextField textField) {
        textField.removeClassNames("correct", "incorrect");
        String input = Strings.nullToEmpty(textField.getValue()).trim();
        if (model.isUserInputCorrect(currentRow, currentColumn)) {
            textField.addClassName("correct");
            textField.setReadOnly(true);
        } else if (!Strings.isNullOrEmpty(input)) {
            textField.addClassName("incorrect");
        }
    }

    private void showSuccessNotification() {
        Notification.show("Pyramide gelöst! Herzlichen Glückwunsch!", 2000, Notification.Position.MIDDLE);
    }
}