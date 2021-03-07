package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.component.HasOrderedComponents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MathPyramidLayoutTest {

    @ParameterizedTest
    @CsvSource({"3,6", "4,10", "5,15", "6,21"})
    public void testThatCorrectNumberOfPyramidBlocksAreCreated(int size, int blocks) {
        MathPyramidLayout layout = new MathPyramidLayout(size);
        assertThat(layout.getPyramidBlocks().size()).isEqualTo(blocks);
    }

    @ParameterizedTest
    @CsvSource({"3,3", "4,4", "5,5", "6,6"})
    public void testThatCorrectNumberOfRowsAreCreated(int size, int rows) {
        MathPyramidLayout layout = new MathPyramidLayout(size);
        assertThat(layout.getComponentCount()).isEqualTo(rows);
    }

    @Test
    public void testThatEachRowHasCorrectNumberOfBlocks() {
        MathPyramidLayout layout = new MathPyramidLayout(3);
        // top row
        assertThat(((HasOrderedComponents<?>) layout.getComponentAt(0)).getComponentCount()).isEqualTo(1);
        // middle row
        assertThat(((HasOrderedComponents<?>) layout.getComponentAt(1)).getComponentCount()).isEqualTo(2);
        // bottom row
        assertThat(((HasOrderedComponents<?>) layout.getComponentAt(2)).getComponentCount()).isEqualTo(3);
    }
}