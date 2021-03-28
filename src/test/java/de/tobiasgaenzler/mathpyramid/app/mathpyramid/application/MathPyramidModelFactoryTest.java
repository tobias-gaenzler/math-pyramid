package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MathPyramidModelFactoryTest {

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 11, Integer.MAX_VALUE})
    public void testIllegalSizesLessThanThreeOrGreaterThan10(int size) {
        MathPyramidModelFactory mathPyramidModelFactory = new MathPyramidModelFactory(new MathPyramidCalculator());
        assertThatThrownBy(() -> mathPyramidModelFactory.createMathPyramid(size, 10000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 3 and 10");
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
        assertThat(mathPyramid.getSize()).isEqualTo(3);
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
        return mathPyramid.getSolutionAt(row, col);
    }
}