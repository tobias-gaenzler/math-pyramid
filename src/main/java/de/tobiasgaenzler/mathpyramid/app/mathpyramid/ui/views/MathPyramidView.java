package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.google.common.base.Strings;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class) // use this view as default view ("/"), set this as content in MainLayout
// Local styles for text fields (can style shadow dom, parts, ...)
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Math-Pyramid")
public class MathPyramidView extends VerticalLayout {

    private final Environment env;
    private final MathPyramidLayout layout;
    private MathPyramidModel model;

    public MathPyramidView(@Autowired Environment env, @Autowired MathPyramidLayout layout) {
        this.env = env;
        addClassName("app-layout");
        this.layout = layout;
        refresh();
    }

    private void refresh() {
        removeAll();
        int maxValue = env.getProperty("math-pyramid.max-value", Integer.class, 100);
        model = new MathPyramidModel(maxValue);
        layout.init(model.getSize());
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
                Notification notification = Notification.show("Solved! Congratulations!", 20000, Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> refresh());
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
}