package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramid;

import java.text.MessageFormat;

public class MathPyramidModel {

    private final MathPyramid mathPyramid;
    private final Table<Integer, Integer, String> userInput = HashBasedTable.create();

    public MathPyramidModel() {
        mathPyramid = new MathPyramid(3, 100);
        // initialize user input
        for (int row = 0; row < mathPyramid.getSize(); row++) {
            for (int column = 0; column < mathPyramid.getSize() - row; column++) {
                if (isUserInput(row, column)) {
                    userInput.put(row, column, "");
                }
            }
        }
    }

    public boolean isSolved() {
        for (int row = 0; row < mathPyramid.getSize(); row++) {
            for (int column = 0; column < mathPyramid.getSize() - row; column++) {
                if (isUserInput(row, column) && !isUserInputCorrect(row, column)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUserInputCorrect(int row, int column) {
        String inputValue = getUserInput(row, column);
        String solutionValue = getSolution(row, column);
        return solutionValue.equals(inputValue);
    }

    public int getSize() {
        return mathPyramid.getSize();
    }

    public void setUserInput(int row, int column, String value) {
        if (!isUserInput(row, column)) {
            String message = MessageFormat.format("Can not enter values in read only block row {0}, column {1}.", row, column);
            throw new IllegalArgumentException(message);
        }
        userInput.put(row, column, trimInputValue(value));
    }

    public int getIndex(Integer rowId, Integer colId) {
        return mathPyramid.getIndex(rowId, colId);
    }

    public boolean isUserInput(int row, int column) {
        return !mathPyramid.isPrefilledBlock(row, column);
    }

    public String getSolution(int row, int column) {
        return mathPyramid.getSolutionAt(row, column);
    }

    String getUserInput(int row, int column) {
        return userInput.get(row, column);
    }

    private String trimInputValue(String input) {
        return Strings.nullToEmpty(input).trim();
    }
}
