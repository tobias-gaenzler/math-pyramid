package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MathPyramidModelFactory {

    public MathPyramidModel createMathPyramid(Integer size, Integer maxValue) {

        if (size < 3 || size > 10) {
            String message = MessageFormat.format("Illegal size {0} for pyramid. " +
                    "Size must be between 3 and 10", size);
            throw new IllegalArgumentException(message);
        }
        if (maxValue < 5) {
            String message = MessageFormat.format("Illegal maxValue {0} for pyramid. " +
                    "MaxValue must be 5 or higher.", maxValue);
            throw new IllegalArgumentException(message);
        }
        List<Integer> solution = createRandomSolution(size, maxValue);
        return new MathPyramidModel(size, getUniquelySolvableRandomStartValues(size, solution), solution);
    }


    private List<Integer> createRandomSolution(Integer size, Integer maxValue) {
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
    private Map<Integer, Integer> getUniquelySolvableRandomStartValues(Integer size, List<Integer> solution) {
        Map<Integer, Integer> startValues = getRandomStartValues(size, solution);
        while (!isUniquelySolvable(startValues, size)) {
            startValues = getRandomStartValues(size, solution);
        }
        return startValues;
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

    private boolean isUniquelySolvable(Map<Integer, Integer> startValues, Integer size) {
        int columns = getNumberOfBlocks(size);
        int rows = columns - size;
        SimpleMatrix A = new SimpleMatrix(rows, rows);
        SimpleMatrix F = createMatrix(size);
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

    int getNumberOfBlocks(Integer size) {
        return (size * size + size) / 2;
    }
}
