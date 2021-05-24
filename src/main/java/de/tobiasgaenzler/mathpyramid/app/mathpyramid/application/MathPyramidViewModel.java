package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.text.MessageFormat;

public class MathPyramidViewModel {

    private final MathPyramidModel mathPyramidModel;
    private final MathPyramidCalculator calculator;
    private final Table<Integer, Integer, Integer> userInput = HashBasedTable.create();
    private boolean multiplayerGame;
    private boolean multiplayerGameInProgress;

    public MathPyramidViewModel(MathPyramidModel mathPyramidModel,
                                MathPyramidCalculator calculator) {
        super();
        this.mathPyramidModel = mathPyramidModel;
        this.calculator = calculator;
        multiplayerGame = false;
        multiplayerGameInProgress = false;
        initializeUserInput();
    }

    public MathPyramidViewModel(MathPyramidViewModel other) {
        this(other.mathPyramidModel, other.calculator);
        multiplayerGame = other.multiplayerGame;
        multiplayerGameInProgress = other.multiplayerGameInProgress;
    }

    private void initializeUserInput() {
        // initialize user input
        for (int row = 0; row < getSize(); row++) {
            for (int column = 0; column < getSize() - row; column++) {
                if (isUserInput(row, column)) {
                    userInput.put(row, column, 0);
                }
            }
        }
    }


    public void setUserInput(Integer row, Integer column, Integer value) {
        if (!isUserInput(row, column)) {
            String message = MessageFormat.format("Can not enter values in read only block row {0}, column {1}.", row, column);
            throw new IllegalArgumentException(message);
        }
        if (value != null) {
            userInput.put(row, column, value);
        }
    }

    public boolean isUserInput(Integer row, Integer column) {
        calculator.checkDimensions(row, column, getSize());
        return mathPyramidModel.getStartValues().get(calculator.getIndex(row, column, getSize())) == null;
    }

    public int getSize() {
        return mathPyramidModel.getSize();
    }

    public Integer getSolutionAt(Integer rowId, Integer colId) {
        return mathPyramidModel.getSolution().get(calculator.getIndex(rowId, colId, getSize()));
    }

    public Integer getUserInput(Integer rowId, Integer colId) {
        calculator.checkDimensions(rowId, colId, getSize());
        return userInput.get(rowId, colId);
    }

    public int getIndex(int row, int column, int size) {
        return calculator.getIndex(row, column, size);
    }

    public boolean getMultiplayerGame() {
        return multiplayerGame;
    }

    public void setMultiplayerGame(boolean multiplayerGame) {
        this.multiplayerGame = multiplayerGame;
    }

    public boolean isMultiplayerGameInProgress() {
        return multiplayerGameInProgress;
    }

    public void endMultiplayerGame() {
        multiplayerGameInProgress = false;
    }

    public void startMultiplayerGame() {
        multiplayerGameInProgress = true;
    }

    public MathPyramidCalculator getCalculator() {
        return calculator;
    }
}
