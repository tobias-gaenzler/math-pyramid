package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MathPyramidLayout extends VerticalLayout {

    private final List<TextField> pyramidBlocks = new ArrayList<>();
    private int size = 0;

    public MathPyramidLayout(int size) {
        super();
        setId("mathpyramid-layout");
        addClassNames("mathpyramid-layout");
        setSizeUndefined();
        setSize(size);

    }

    private void setSize(int size) {
        this.size = size;
        refresh();
    }

    public List<TextField> getPyramidBlocks() {
        return pyramidBlocks;
    }

    private void refresh() {
        pyramidBlocks.clear();
        removeAll();

        // create blocks from bottom left
        IntStream.iterate(size - 1, i -> i - 1)//
                .limit(size)//
                .forEach(i -> addComponentAsFirst(createRow(i)));
    }

    private HorizontalLayout createRow(Integer rowId) {
        HorizontalLayout row = new HorizontalLayout();
        IntStream.iterate(rowId + 1, colId -> colId - 1)//
                .limit(rowId + 1)//
                .forEach(colId -> row.addComponentAsFirst(createBlock()));
        row.addClassName("row");
        return row;
    }

    private TextField createBlock() {
        TextField textField = new TextField();
        pyramidBlocks.add(textField);
        return textField;
    }
}
