package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MultiplayerVaadinViewModelFactoryTest {

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 11, Integer.MAX_VALUE})
    public void testIllegalSizesLessThanThreeOrGreaterThan10(int size) {
        MathPyramidViewModelFactory mathPyramidViewModelFactory = new MathPyramidViewModelFactory(new MathPyramidCalculator());
        assertThatThrownBy(() -> mathPyramidViewModelFactory.create(size, 10000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 3 and 10");
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 2, 4})
    public void testIllegalMaxValuesSmallerThan5(int maxValue) {
        MathPyramidViewModelFactory mathPyramidViewModelFactory = new MathPyramidViewModelFactory(new MathPyramidCalculator());
        assertThatThrownBy(() -> mathPyramidViewModelFactory.create(3, maxValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 or higher");
    }

    @Test
    public void testMathPyramidOfSizeThreeIsValid() {
        MathPyramidViewModelFactory mathPyramidViewModelFactory = new MathPyramidViewModelFactory(new MathPyramidCalculator());
        MathPyramidViewModel mathPyramid = mathPyramidViewModelFactory.create(3, 10_000);
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

    private int valueAt(MathPyramidViewModel mathPyramid, int row, int col) {
        return mathPyramid.getSolutionAt(row, col);
    }
}