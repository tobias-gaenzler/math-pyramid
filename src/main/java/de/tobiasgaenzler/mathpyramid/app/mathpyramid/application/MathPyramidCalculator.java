package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MathPyramidCalculator {

    public int getIndex(Integer rowId, Integer colId, Integer size) {
        // starting in bottom row left, e.g. for pyramid of size 3:
        // 0 0 -> 0
        // 0 1 -> 1
        // 0 2 -> 2

        // 1 0 -> 3
        // 1 1 -> 4

        // 2 0 -> 5
        checkDimensions(rowId, colId, size);
        int index = 0;
        // increase index by (size - i) for each row
        for (int i = 0; i < rowId; i = i + 1) {
            index = index + size - i;
        }
        return index + colId;
    }

    public void checkDimensions(Integer rowId, Integer colId, Integer size) {
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

    List<Integer> createRandomSolution(Integer size, Integer maxValue) {
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

    /* Get 'size' values from solution */

    Map<Integer, Integer> getUniquelySolvableRandomStartValues(Integer size, List<Integer> solution) {
        Map<Integer, Integer> startValues = getRandomStartValues(size, solution);
        long tries = 1;
        while (getDifficulty(startValues.keySet(), size) == null || getDifficulty(startValues.keySet(), size) == 1) {
            startValues = getRandomStartValues(size, solution);
            tries++;
        }
        System.out.println("Needed " + tries +   " iterations to find suitable start values.");
        return startValues;
    }

    Integer getDifficulty(Set<Integer> startPositions, int size) {
        if (!isUniquelySolvable(startPositions, size)) {
            return null; // no unique solution
        }
        // try to solve pyramid
        HashSet<Integer> calculatablePositions = new HashSet<>(startPositions);
        for (int i = 0; i < size; i++) {
            addCurrentCalculatablePositions(calculatablePositions, size);
        }
        return calculatablePositions.size() < getNumberOfBlocks(size) ? 1 : 0;
    }

    int getNumberOfBlocks(Integer pyramidHeight) {
        return (pyramidHeight * pyramidHeight + pyramidHeight) / 2;
    }

    private void addCurrentCalculatablePositions(HashSet<Integer> calculatablePositions, int size) {

        for (int row = 0; row < size - 1; row++) {
            for (int column = 0; column < size - row; column++) {
                if (column + 1 < size - row) {
                    // plus
                    Integer first = getIndex(row, column, size);
                    Integer second = getIndex(row, column + 1, size);
                    if (calculatablePositions.contains(first) && calculatablePositions.contains(second)) {
                        calculatablePositions.add(getIndex(row + 1, column, size));
                    }
                    // minus to the right
                    Integer firstMinus = getIndex(row, column, size);
                    Integer secondMinus = getIndex(row + 1, column, size);
                    if (calculatablePositions.contains(firstMinus) && calculatablePositions.contains(secondMinus)) {
                        calculatablePositions.add(getIndex(row, column + 1, size));
                    }
                }
                // minus to the left
                if (column > 0) {
                    Integer firstMinus = getIndex(row, column, size);
                    Integer secondMinus = getIndex(row + 1, column - 1, size);
                    if (calculatablePositions.contains(firstMinus) && calculatablePositions.contains(secondMinus)) {
                        calculatablePositions.add(getIndex(row, column - 1, size));
                    }
                }

            }
        }
    }

    private boolean isUniquelySolvable(Set<Integer> startValues, Integer size) {
        int columns = getNumberOfBlocks(size);
        int rows = columns - size;
        SimpleMatrix A = new SimpleMatrix(rows, rows);
        SimpleMatrix F = createMatrix(size);
        int column = 0;
        for (int i = 0; i < columns; i++) {
            if (!startValues.contains(i)) {
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

    private SimpleMatrix createMatrix(Integer size) {

        int columns = getNumberOfBlocks(size);
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

    private Map<Integer, Integer> getRandomStartValues(Integer size, List<Integer> solution) {
        int numberOfBlocks = getNumberOfBlocks(size);
        Map<Integer, Integer> randomStartValues = new HashMap<>();
        List<Integer> randomIndices = getRandomIndices(numberOfBlocks, size);
        randomIndices.forEach(randomIndex -> randomStartValues.put(randomIndex, solution.get(randomIndex)));
        return randomStartValues;
    }

    private List<Integer> getRandomIndices(Integer maxValue, Integer numberOfIndices) {
        List<Integer> givenList = IntStream.range(0, maxValue).boxed().collect(Collectors.toList());
        Collections.shuffle(givenList);
        return givenList.subList(0, numberOfIndices);
    }
}
