package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class MathPyramidModel {

    private final Integer size; // number or rows, higher number increases difficulty
    private final Map<Integer,Integer> startValues; // numbers which are visible from the start
    private final List<Integer> solution;
    private final Table<Integer, Integer, String> userInput = HashBasedTable.create();

    MathPyramidModel(Integer size, Map<Integer,Integer> startValues, List<Integer> solution) {
        this.size = size;
        this.startValues = startValues;
        this.solution = solution;
        // initialize user input
        for (int row = 0; row < getSize(); row++) {
            for (int column = 0; column < getSize() - row; column++) {
                if (isUserInput(row, column)) {
                    userInput.put(row, column, "");
                }
            }
        }
    }

    public boolean isSolved() {
        for (int row = 0; row < getSize(); row++) {
            for (int column = 0; column < getSize() - row; column++) {
                if (isUserInput(row, column) && !isUserInputCorrect(row, column)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUserInputCorrect(Integer row, Integer column) {
        checkDimensions(row, column);
        String inputValue = getUserInput(row, column);
        String solutionValue = getSolutionAt(row, column);
        return solutionValue.equals(inputValue);
    }

    public void setUserInput(Integer row, Integer column, String value) {
        if (!isUserInput(row, column)) {
            String message = MessageFormat.format("Can not enter values in read only block row {0}, column {1}.", row, column);
            throw new IllegalArgumentException(message);
        }
        userInput.put(row, column, trimInputValue(value));
    }

    public boolean isUserInput(Integer row, Integer column) {
        checkDimensions(row, column);
        return startValues.get(getIndex(row, column)) == null;
    }

    public int getSize() {
        return size;
    }

    public int getIndex(Integer rowId, Integer colId) {
        // starting in bottom row left, e.g. for pyramid of size 3:
        // 0 0 -> 0
        // 0 1 -> 1
        // 0 2 -> 2

        // 1 0 -> 3
        // 1 1 -> 4

        // 2 0 -> 5
        checkDimensions(rowId, colId);
        int index = 0;
        // increase index by (size - i) for each row
        for (int i = 0; i < rowId; i = i + 1) {
            index = index + size - i;
        }
        return index + colId;
    }

    public String getSolutionAt(Integer rowId, Integer colId) {
        return solution.get(getIndex(rowId, colId)).toString();
    }

    private String getUserInput(Integer rowId, Integer colId) {
        checkDimensions(rowId, colId);
        return userInput.get(rowId, colId);
    }

    private String trimInputValue(String input) {
        return Strings.nullToEmpty(input).trim();
    }

    private void checkDimensions(Integer rowId, Integer colId) {
        String message = "";
        if (rowId < 0 || rowId >= size) {
            message += MessageFormat.format("rowId {0} must be non-negative and smaller than the size of the pyramid {1}", rowId, size);
        }
        if (colId < 0 || colId >= size - rowId) {
            message += MessageFormat.format("colId {0} must be non-negative and smaller than the size of the pyramid minus rowId {1}, size {2}", colId, rowId, size);
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    Map<Integer,Integer> getStartValues() {
        return startValues;
    }

    List<Integer> getSolution() {
        return solution;
    }
}
