package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramidViewModel;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.MainLayout;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.MathPyramidViewListener;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.MathPyramidViewPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
@Route(value = "", layout = MainLayout.class) // use this view as default view ("/"), set this as content in MainLayout
// Local styles for text fields (can style shadow dom, parts, ...)
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Math-Pyramid")
public class MathPyramidVaadinView extends VerticalLayout implements MathPyramidView {

    private final Logger logger = LoggerFactory.getLogger(MathPyramidVaadinView.class);
    private final MathPyramidLayout layout;
    private final MathPyramidViewListener presenter;
    private MathPyramidViewModel model;

    @Autowired
    public MathPyramidVaadinView(MathPyramidLayout layout, MathPyramidViewPresenter presenter) {
        this.layout = layout;
        this.presenter = presenter;
        addClassName("app-layout");
        logger.debug("Initializing new MathPyramidView");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        presenter.register(attachEvent.getUI(), this);
        // start new game on first load only
        if (model == null) {
            presenter.startGame(false);
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        presenter.unregister();
    }

    @Override
    public void refreshView(MathPyramidViewModel model) {
        logger.info("Refreshing view with model" + model);
        this.model = model;
        removeAll();
        layout.init(this.model.getSize());
        add(layout);
        bind();
    }

    private void bind() {
        int size = model.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - row; column++) {
                bindPyramidBlock(row, column);
            }
        }
    }

    private void bindPyramidBlock(int row, int column) {
        IntegerField textField = getPyramidBlock(row, column);
        if (model.isUserInput(row, column)) {
            addValueChangeListener(row, column, textField);
        } else {
            textField.setValue(model.getSolutionAt(row, column));
            textField.setReadOnly(true);
        }
    }

    private void addValueChangeListener(int currentRow, int currentColumn, IntegerField textField) {
        textField.addValueChangeListener(event ->
                presenter.pyramidBlockChanged(currentRow, currentColumn, textField.getValue()));
    }

    @Override
    public void updatePyramidBlock(int currentRow, int currentColumn, boolean blockSolved) {
        IntegerField textField = getPyramidBlock(currentRow, currentColumn);
        textField.removeClassNames("correct", "incorrect");
        Integer input = textField.getValue();
        if (blockSolved) {
            textField.addClassName("correct");
            textField.setReadOnly(true);
        } else if (input != null) {
            textField.addClassName("incorrect");
        }
    }

    private IntegerField getPyramidBlock(int currentRow, int currentColumn) {
        int fieldIndex = model.getIndex(currentRow, currentColumn, model.getSize());
        return layout.getPyramidBlocks().get(fieldIndex);
    }
}