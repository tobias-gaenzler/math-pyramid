package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MathPyramidModelFactoryTest {

    @ParameterizedTest
    @CsvSource({"3,6", "4,10", "5,15", "6,21", "7,28", "8,36", "9,45", "10,55"})
    public void testCreatesPyramidWithCorrectSizing(int size, int numberOfBlocks) {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());

        MathPyramidModel model = mathPyramidModelFactory.createMathPyramid(size, 10_000);
        assertThat(model.getSize()).isEqualTo(size);
        // number of start values is equal to size
        assertThat(model.getStartValues().size()).isEqualTo(size);
        assertThat(model.getSolution().size()).isEqualTo(numberOfBlocks);
        // number of blocks is equal to number of solution entries
        assertThat(model.getSolution().size()).isEqualTo(numberOfBlocks);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 11, Integer.MAX_VALUE})
    public void testIllegalSizesLessThanThreeOrGreaterThan10(int size) {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());
        assertThatThrownBy(() -> mathPyramidModelFactory.createMathPyramid(size, 10000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 3 and 10");
    }

    @ParameterizedTest
    @CsvSource({"3,5", "4,100", "5,10000", "10," + Integer.MAX_VALUE})
    public void testMathPyramidCreatesPyramidWithValuesLowerOrEqualThanMaxValue(int size, int maxValue) {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());
        MathPyramidModel mathPyramid = mathPyramidModelFactory.createMathPyramid(size, maxValue);
        assertThat(mathPyramid.getSolution().stream()
                .filter(value -> value > maxValue).findFirst().isEmpty())
                .isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 4})
    public void testIllegalMaxValuesSmallerThan5(int maxValue) {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());
        assertThatThrownBy(() -> mathPyramidModelFactory.createMathPyramid(3, maxValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 or higher");
    }

    @Test
    public void testMathPyramidOfSizeThreeIsValid() {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());
        MathPyramidModel mathPyramid = mathPyramidModelFactory.createMathPyramid(3, 10_000);
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

    private int valueAt(MathPyramidModel mathPyramid, int row, int col) {
        return Integer.parseInt(mathPyramid.getSolutionAt(row, col));
    }
}