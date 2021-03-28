package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class MathPyramidModel {

    private final Integer size; // number or rows, higher number increases difficulty
    private final Map<Integer, Integer> startValues; // numbers which are visible from the start
    private final List<Integer> solution;
    private final Table<Integer, Integer, String> userInput = HashBasedTable.create();
    private final MathPyramidCalculator calculator;
    private boolean multiplayer;

    MathPyramidModel(Integer size, Map<Integer, Integer> startValues, List<Integer> solution, MathPyramidCalculator calculator) {
        this.size = size;
        this.startValues = startValues;
        this.solution = solution;
        this.calculator = calculator;
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
        calculator.checkDimensions(row, column, size);
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
        calculator.checkDimensions(row, column, size);
        return startValues.get(calculator.getIndex(row, column, size)) == null;
    }

    public int getSize() {
        return size;
    }


    public String getSolutionAt(Integer rowId, Integer colId) {
        return solution.get(calculator.getIndex(rowId, colId, size)).toString();
    }

    private String getUserInput(Integer rowId, Integer colId) {
        calculator.checkDimensions(rowId, colId, size);
        return userInput.get(rowId, colId);
    }

    private String trimInputValue(String input) {
        return Strings.nullToEmpty(input).trim();
    }

    Map<Integer, Integer> getStartValues() {
        return startValues;
    }

    List<Integer> getSolution() {
        return solution;
    }

    public void setMultiplayer(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    public boolean getMultiplayer() {
        return multiplayer;
    }
}
