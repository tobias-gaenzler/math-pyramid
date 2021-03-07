package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MathPyramidModelTest {

    @Test
    public void testIsSolvedReturnsFalseForInitialModel() {
        MathPyramidModel model = new MathPyramidModel();
        assertThat(model.isSolved()).isEqualTo(false);
    }

    @ParameterizedTest
    @CsvSource({"0,0", "0,1", "0,2", "1,0", "1,1", "2,0"})
    public void testIsUserInputCorrectReturnsFalseForInitialModel(int row, int column) {
        MathPyramidModel model = new MathPyramidModel();
        assertThat(model.isUserInputCorrect(row, column)).isEqualTo(false);
    }

    @ParameterizedTest
    @CsvSource({"-1,0", "0,-1", "3,2", "1,3"})
    public void testIsUserInputCorrectThrowsExceptionForOutOfRangeValues(int row, int column) {
        MathPyramidModel model = new MathPyramidModel();
        assertThatThrownBy(() -> model.isUserInputCorrect(row, column))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testIsSolvedReturnsTrueWhenPyramidIsSolved() {
        MathPyramidModel model = new MathPyramidModel();
        // set solution into user input fields
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3 - row; column++) {
                if (model.isUserInput(row, column)) {
                    model.setUserInput(row, column, model.getSolution(row, column));
                }
            }
        }
        assertThat(model.isSolved()).isEqualTo(true);
    }

    @Test
    public void testThatOnlyWritableBlocksCanBeSet() {
        MathPyramidModel model = new MathPyramidModel();
        // test all read only blocks (no user input allowed)
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3 - row; column++) {
                if (!model.isUserInput(row, column)) {
                    int finalReadOnlyRow = row;
                    int finalReadOnlyColumn = column;
                    assertThatThrownBy(() -> model.setUserInput(finalReadOnlyRow, finalReadOnlyColumn, ""))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }
        }
    }
}