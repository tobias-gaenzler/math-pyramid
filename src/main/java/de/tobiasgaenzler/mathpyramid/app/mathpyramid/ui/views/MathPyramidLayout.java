package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class MathPyramidLayout extends VerticalLayout {

    public static final String MATHPYRAMID_LAYOUT_ID = "mathpyramid-layout";
    public static final String MATHPYRAMID_LAYOUT_CSS_CLASS = "mathpyramid-layout";
    private final List<TextField> pyramidBlocks = new ArrayList<>();
    private Button sendButton = new Button("Start");

    public MathPyramidLayout() {
        super();
        // styling
        setId(MATHPYRAMID_LAYOUT_ID);
        addClassNames(MATHPYRAMID_LAYOUT_CSS_CLASS);
        setSizeUndefined(); // center
    }

    public void init(int size) {
        pyramidBlocks.clear();
        removeAll();

        // create blocks row by row, from bottom to top, left to right
        for (int rowId = size - 1; rowId >= 0; rowId = rowId - 1) {
            addComponentAsFirst(createRowLayout(rowId));
        }
        add(sendButton);
    }

    public List<TextField> getPyramidBlocks() {
        return pyramidBlocks;
    }

    public Button getStartButton() {
        return sendButton;
    }

    private HorizontalLayout createRowLayout(Integer rowId) {
        HorizontalLayout rowLayout = new HorizontalLayout();
        // add rowId + 1 fields (e.g. one field for row 0, two fields for row 1)
        for (int colId = 0; colId < rowId + 1; colId++) {
            rowLayout.addComponentAsFirst(createTextField());
        }
        rowLayout.addClassName("row");
        return rowLayout;
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        pyramidBlocks.add(textField);
        return textField;
    }
}
