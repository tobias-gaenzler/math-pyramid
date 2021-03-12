package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MathPyramidTest {

    @ParameterizedTest
    @CsvSource({"3,6", "4,10", "5,15"})
    public void testMathPyramidCreatesPyramidWithCorrectSizing(int size, int numberOfBlocks) {
        MathPyramid mathPyramid = new MathPyramid(size, 10000);
        assertThat(mathPyramid.getSize()).isEqualTo(size);
        assertThat(mathPyramid.getNumberOfBlocks()).isEqualTo(numberOfBlocks);
        assertThat(mathPyramid.getStartValues().size()).isEqualTo(numberOfBlocks);
        assertThat(mathPyramid.getSolution().size()).isEqualTo(numberOfBlocks);
        // three start values
        assertThat(mathPyramid.getStartValues().stream().filter(Objects::nonNull).count())
                .isEqualTo(size);
        // six solution values
        assertThat(mathPyramid.getSolution().size()).isEqualTo(numberOfBlocks);
    }


    @ParameterizedTest
    @CsvSource({"3,5", "4,100", "5,10000", "5," + Integer.MAX_VALUE})
    public void testMathPyramidCreatesPyramidWithValuesLowerOrEqualThanMaxValue(int size, int maxValue) {
        MathPyramid mathPyramid = new MathPyramid(size, maxValue);
        assertThat(mathPyramid.getSolution().stream()
                .filter(value -> value > maxValue).findFirst().isEmpty())
                .isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 6, Integer.MAX_VALUE})
    public void testIllegalSizesLessThanThreeOrGreaterThan5(int size) {
        assertThatThrownBy(() -> new MathPyramid(size, 10000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 3 and 5");
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 4})
    public void testIllegalMaxValuesSmallerThan5(int maxValue) {
        assertThatThrownBy(() -> new MathPyramid(3, maxValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 or higher");
    }

    @Test
    public void testMathPyramidOfSizeThreeIsValid() {
        MathPyramid mathPyramid = new MathPyramid(3, 10000);
        assertThat(valueAt(mathPyramid, 0, 0) +
                valueAt(mathPyramid, 0, 1))
                .isEqualTo(valueAt(mathPyramid, 1, 0));
        assertThat(valueAt(mathPyramid, 0, 1) +
                valueAt(mathPyramid, 0, 2))
                .isEqualTo(valueAt(mathPyramid, 1, 1));
        assertThat(valueAt(mathPyramid, 1, 0) +
                valueAt(mathPyramid, 1, 1))
                .isEqualTo(valueAt(mathPyramid, 2, 0));
    }

    private int valueAt(MathPyramid mathPyramid, int row, int col) {
        return Integer.parseInt(mathPyramid.getSolutionAt(row, col));
    }

    @ParameterizedTest
    @CsvSource({"0,0,0", "0,1,1", "0,2,2", "1,0,3", "1,1,4", "2,0,5"})
    public void testGetIndexReturnsCorrectValues(int row, int col, int index) {
        MathPyramid mathPyramid = new MathPyramid(3, 10000);
        assertThat(mathPyramid.getIndex(row, col)).isEqualTo(index);
    }

    @ParameterizedTest
    @CsvSource({"-10,0", "0,-1", "4,1", "1,5"})
    public void testGetIndexThrowsExceptionOnInvalidValues(int row, int col) {
        MathPyramid mathPyramid = new MathPyramid(3, 10000);
        assertThatThrownBy(() -> mathPyramid.getIndex(row, col))
                .isInstanceOf(IllegalArgumentException.class);
    }
}