package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.base.Strings;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("") // map this view to "/"
@Theme(value = Lumo.class)
// Global styles apply only to elements which are not located in shadow dom
@CssImport("./styles/shared-styles.css")
// Local styles for text fields (can style parts, ...)
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MathPyramidView extends VerticalLayout {

    private final MathPyramidLayout layout;
    private final MathPyramidModel model;

    public MathPyramidView() {
        addClassName("app-layout");
        model = new MathPyramidModel();
        layout = new MathPyramidLayout(model.getSize());
        add(layout);
        bind();
    }

    public void bind() {
        int size = model.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - row; column++) {
                createPyramidBlock(row, column);
            }
        }
    }

    private void createPyramidBlock(int row, int column) {
        int fieldIndex = model.getIndex(row, column);
        TextField textField = layout.getPyramidBlocks().get(fieldIndex);
        if (model.isUserInput(row, column)) {
            addValueChangeListener(row, column, textField);
        } else {
            textField.setValue(model.getSolution(row, column));
            textField.setReadOnly(true);
        }
    }

    private void addValueChangeListener(final int currentRow, final int currentColumn, TextField textField) {
        textField.addValueChangeListener(event -> {
            // store user input in model
            model.setUserInput(currentRow, currentColumn, textField.getValue());
            updatePyramidBlock(currentRow, currentColumn, textField);
            if (model.isSolved()) {
                showSuccessNotification();
            }

        });
    }

    public void updatePyramidBlock(final int currentRow, final int currentColumn, TextField textField) {
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
        Notification.show("Solved! Congratulations!", 2000, Notification.Position.MIDDLE);
    }
}