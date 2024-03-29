package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class MathPyramidLayout extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(MathPyramidLayout.class);
    public static final String MATHPYRAMID_LAYOUT_ID = "mathpyramid-layout";
    public static final String MATHPYRAMID_LAYOUT_CSS_CLASS = "mathpyramid-layout";
    private final List<IntegerField> pyramidBlocks = new ArrayList<>();

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
        logger.debug("Created layout for size {}", size);
    }

    public List<IntegerField> getPyramidBlocks() {
        return pyramidBlocks;
    }

    private HorizontalLayout createRowLayout(Integer rowId) {
        HorizontalLayout rowLayout = new HorizontalLayout();
        // add rowId + 1 fields (e.g. one field for row 0, two fields for row 1)
        for (int colId = 0; colId < rowId + 1; colId++) {
            rowLayout.addComponentAsFirst(createPyramidBlock());
        }
        rowLayout.addClassName("row");
        return rowLayout;
    }

    private IntegerField createPyramidBlock() {
        IntegerField pyramidBlock = new IntegerField();
        pyramidBlock.setValueChangeMode(ValueChangeMode.LAZY);
        pyramidBlock.setValueChangeTimeout(300);
        pyramidBlock.addClassName("pyramid-block");
        pyramidBlocks.add(pyramidBlock);
        return pyramidBlock;
    }
}
