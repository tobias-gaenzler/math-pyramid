package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.application.MathPyramid;

public class MathPyramidModel {

    private final MathPyramid mathPyramid;
    private final Table<Integer, Integer, String> userInput = HashBasedTable.create();

    public MathPyramidModel() {
        this.mathPyramid = new MathPyramid(3, 100);
        // initialize user input
        for (int row = 0; row < mathPyramid.getSize(); row++) {
            for (int column = 0; column < mathPyramid.getSize(); column++) {
                if (isUserInput(row, column)) {
                    userInput.put(row, column, "");
                }
            }
        }
    }

    public int getSize() {
        return mathPyramid.getSize();
    }

    public int getIndex(Integer rowId, Integer colId) {
        return mathPyramid.getIndex(rowId, colId);
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

    public boolean isUserInput(int row, int column) {
        return !mathPyramid.isPrefilledBlock(row, column);
    }

    public String getSolution(int row, int column) {
        return mathPyramid.getSolutionAt(row, column);
    }

    public String getUserInput(int row, int column) {
        return userInput.get(row, column);
    }

    public boolean isUserInputCorrect(int row, int column) {
        String inputValue = cleanInputValue(getUserInput(row, column));
        String solutionValue = cleanInputValue(getSolution(row, column));
        return solutionValue.equalsIgnoreCase(inputValue);
    }

    private String cleanInputValue(String input) {
        return Strings.nullToEmpty(input).trim().replaceAll("^0*([0-9])", "$1");
    }

    public void setUserInput(int currentRow, int currentColumn, String value) {
        userInput.put(currentRow, currentColumn, cleanInputValue(value));
    }
}
