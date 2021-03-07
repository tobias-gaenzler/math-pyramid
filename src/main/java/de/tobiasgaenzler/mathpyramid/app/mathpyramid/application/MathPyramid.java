package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Creates a 'MathPyramid' consisting of blocks which contain integer numbers where
 * the sum of two adjacent blocks equals the value in the block above these two blocks.
 * 'size': number of rows must be between three and 5 (size two is too simple, above size 5 is too expensive
 * 'maxValue': maximal integer value in one of the pyramid blocks
 */
public class MathPyramid {

    private final Integer size; // number or rows, higher number increases difficulty
    private final Integer maxValue; // maximum number in any block, higher numbers increase difficulty
    private final List<Integer> startValues; // numbers which are visible from the start
    private final List<Integer> solution;

    public MathPyramid(Integer size, Integer maxValue) {
        if (size < 3 || size > 5) {
            String message = MessageFormat.format("Illegal size {0} for pyramid. " +
                    "Size must be between 3 and 5", size);
            throw new IllegalArgumentException(message);
        }
        if (maxValue < 10) {
            String message = MessageFormat.format("Illegal maxValue {0} for pyramid. " +
                    "MaxValue must be 10 or higher.", maxValue);
            throw new IllegalArgumentException(message);
        }
        this.size = size;
        this.maxValue = maxValue;
        this.solution = createRandomSolution();
        this.startValues = getRandomStartValues();
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
        if (rowId < 0 || rowId >= size) {
            String message = MessageFormat.format("rowId {0} must be non-negative and smaller than the size of the pyramid {1}", rowId, size);
            throw new IllegalArgumentException(message);
        }
        if (colId < 0 || colId >= size - rowId) {
            String message = MessageFormat.format("colId {0} must be non-negative and smaller than the size of the pyramid minus rowId {1}, size {2}", colId, rowId, size);
            throw new IllegalArgumentException(message);
        }

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

    public boolean isPrefilledBlock(int row, int column) {
        if (row < size && column < size - row) {
            return startValues.get(getIndex(row, column)) != null;
        }
        return false;
    }

    private List<Integer> createRandomSolution() {
        // prevent too easy solutions: increase min value?
        int maxValueInLowestRow = Math.max(2, (int) (maxValue / Math.pow(2, size - 1)));
        // start values in bottom row of pyramid
        List<Integer> randomSolution = new Random()//
                .ints(size, 1, maxValueInLowestRow)//
                .boxed()//
                .collect(Collectors.toList());

        // solve pyramid from bottom to top
        int offset = 0;
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                randomSolution.add(randomSolution.get(offset + j) + randomSolution.get(offset + j + 1));
            }
            offset += (size - (i - 1));
        }
        return randomSolution;
    }

    /* Remove 'size' values from solution */
    private List<Integer> getRandomStartValues() {
        List<Integer> startValues = removeValuesRandomly();
        while (!isUniquelySolvable(startValues, size)) {
            startValues = removeValuesRandomly();
        }
        return startValues;
    }

    private List<Integer> removeValuesRandomly() {

        int numberOfBlocks = getNumberOfBlocks();
        ArrayList<Integer> startValues = new ArrayList<>(solution);
        new Random()//
                .ints(numberOfBlocks - size, 0, numberOfBlocks)//
                .forEach(n -> startValues.set(n, null));
        return startValues;
    }

    private boolean isUniquelySolvable(List<Integer> startValues, int size) {
        int columns = getNumberOfBlocks();
        int rows = columns - size;
        SimpleMatrix A = new SimpleMatrix(rows, rows);
        SimpleMatrix F = createMatrix();
        int column = 0;
        for (int i = 0; i < columns; i++) {
            if (startValues.get(i) == null) {
                for (int j = 0; j < rows; j++) {
                    A.set(j, column, F.get(j, i));
                }
                column++;
            }
        }
        SimpleMatrix b = new SimpleMatrix(rows, 1);

        try {
            A.solve(b);
            return true;
        } catch (SingularMatrixException e) {
            return false;
        }
    }

    private SimpleMatrix createMatrix() {

        int columns = getNumberOfBlocks();
        int rows = columns - size;
        SimpleMatrix A = new SimpleMatrix(rows, columns);

        // -1 on size-th upper diagonal
        IntStream.iterate(0, row -> row + 1)//
                .limit(rows)//
                .forEach(row -> A.set(row, row + size, -1.0d));
        // two ones on diagonal blocks
        int row = 0;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                A.set(row + j, row + j + i, 1.0d);
                A.set(row + j, row + j + i + 1, 1.0d);
            }
            row += size - i - 1;
        }

        return A;
    }

    int getNumberOfBlocks() {
        return (size * size + size) / 2;
    }

    List<Integer> getStartValues() {
        return startValues;
    }

    List<Integer> getSolution() {
        return solution;
    }
}
