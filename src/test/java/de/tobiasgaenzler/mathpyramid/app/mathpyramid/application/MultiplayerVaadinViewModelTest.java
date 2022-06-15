package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultiplayerVaadinViewModelTest {

    @Test
    public void testThatOnlyWritableBlocksCanBeSet() {
        MathPyramidViewModel model = new MathPyramidViewModel(new MathPyramidModel(3,
                Map.of(0, 1, 1, 1, 2, 1),
                List.of(1, 1, 1, 2, 2, 4)),
                new MathPyramidCalculator()
        );
        // test all read only blocks (no user input allowed)
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3 - row; column++) {
                if (!model.isUserInput(row, column)) {
                    int finalReadOnlyRow = row;
                    int finalReadOnlyColumn = column;
                    assertThatThrownBy(() -> model.setUserInput(finalReadOnlyRow, finalReadOnlyColumn, 0))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }
        }
    }
}