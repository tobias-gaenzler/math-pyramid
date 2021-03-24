package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.google.common.base.Strings;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidModelFactory;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.Broadcaster;
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

    public static final int DEFAULT_SIZE = 3;
    public static final int DEFAULT_MAX_VALUE = 100;
    private final Environment env;
    private final MathPyramidModelFactory mathPyramidModelFactory;
    private final MathPyramidLayout layout;
    private MathPyramidModel model;
    private Registration broadcasterRegistration;
    private Registration buttonRegistration;

    @Autowired
    public MathPyramidView(Environment env, MathPyramidModelFactory mathPyramidModelFactory, MathPyramidLayout layout) {
        this.env = env;
        this.mathPyramidModelFactory = mathPyramidModelFactory;
        this.layout = layout;
        addClassName("app-layout");
        createModel();
        refresh(this.model);
    }

    public void bind() {
        int size = model.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - row; column++) {
                bindPyramidBlock(row, column);
            }
        }
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        if (broadcasterRegistration == null) {
            broadcasterRegistration = Broadcaster.register(model -> {
                ui.access(() -> {
                    layout.getStartButton().setEnabled(false);
                    refresh((MathPyramidModel) model);
                });
            });
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if(broadcasterRegistration != null) {
            broadcasterRegistration.remove();
        }
        broadcasterRegistration = null;
    }


    private void refresh(MathPyramidModel model) {
        this.model = model;
        removeAll();
        layout.init(model.getSize());
        if(buttonRegistration != null) {
            buttonRegistration.remove();
        }
        buttonRegistration = layout.getStartButton().addClickListener(event -> {
            createModel();
            layout.getStartButton().setEnabled(false);
            Broadcaster.broadcast(this.model);
        });
        add(layout);
        bind();
    }

    private void createModel() {
        int maxValue = env.getProperty("math-pyramid.max-value", Integer.class, DEFAULT_MAX_VALUE);
        int defaultSize = env.getProperty("math-pyramid.default-size", Integer.class, DEFAULT_SIZE);
        model = mathPyramidModelFactory.createMathPyramid(defaultSize, maxValue);
    }

    private void bindPyramidBlock(int row, int column) {
        int fieldIndex = model.getIndex(row, column);
        TextField textField = layout.getPyramidBlocks().get(fieldIndex);
        if (model.isUserInput(row, column)) {
            addValueChangeListener(row, column, textField);
        } else {
            textField.setValue(model.getSolutionAt(row, column));
            textField.setReadOnly(true);
        }
    }

    private void addValueChangeListener(final int currentRow, final int currentColumn, TextField textField) {
        textField.addValueChangeListener(event -> {
            // store user input in model
            model.setUserInput(currentRow, currentColumn, textField.getValue());
            updatePyramidBlock(currentRow, currentColumn, textField);
            if (model.isSolved()) {
                VerticalLayout layout = new VerticalLayout();
                Span content = new Span("Solved! Congratulations!");
                layout.add(content);
                Button closeButton = new Button("Close");
                layout.add(closeButton);
                Notification notification = new Notification(layout);
                notification.setDuration(3000);
                notification.setPosition(Notification.Position.MIDDLE);
                closeButton.addClickListener(closeButtonClickedEvent -> notification.close());
                notification.open();
            }
        });
    }
}