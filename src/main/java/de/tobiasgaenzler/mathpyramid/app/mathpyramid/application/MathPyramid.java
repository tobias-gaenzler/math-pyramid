package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.apache.commons.compress.utils.Lists;
import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MathPyramid {

	private final Integer size;
	private final Integer maxValue;
	private List<Integer> startValues = new ArrayList<>();
	private List<Integer> solution =  new ArrayList<>();

	public MathPyramid(Integer size, Integer maxValue) {
		this.size = size;
		this.maxValue = maxValue;
		create();
	}

	public List<Integer> getStartValues() {
		return startValues;
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
		if(rowId >= size) {
			String message = MessageFormat.format("rowId {0} must be smaller than the size of the pyramid {1}", rowId, size);
			throw new IllegalArgumentException(message);
		}
		if(colId >= size - rowId) {
			String message = MessageFormat.format("colId {0} must be smaller than the size of the pyramid minus rowId {1}, size {2}", colId, rowId, size);
			throw new IllegalArgumentException(message);
		}

		AtomicInteger index = new AtomicInteger(0);
		IntStream.iterate(0, i -> i + 1)//
				.limit(rowId)// increase index by (size - i) for each row
				.forEach(i -> index.set(index.get() + size - i));
		return index.get() + colId;
	}

	public String getSolutionAt(Integer rowId, Integer colId) {
		return solution.get(getIndex(rowId, colId)).toString();
	}

	private void create() {
		this.solution = createRandomSolution();
		this.startValues = createStartValuesFromSolution();
	}

	/* Remove 'size' values from solution */
	private List<Integer> createStartValuesFromSolution() {
		List<Integer> startValues = removeValuesRandomly();
		while (!isUniquelySolvable(startValues, size)) {
			startValues = removeValuesRandomly();
		}
		return startValues;
	}

	private boolean isUniquelySolvable(List<Integer> startValues, int size) {
		int columns = getNumberOfBlocks(size);
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

	private List<Integer> removeValuesRandomly() {
		int numberOfBlocks = getNumberOfBlocks(size);
		ArrayList<Integer> startValues = new ArrayList<>(solution);
		new Random()//
				.ints(numberOfBlocks - size, 0, numberOfBlocks)//
				.forEach(n -> startValues.set(n, null));
		return startValues;
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

	private int getNumberOfBlocks(int size) {
		return (size * size + size) / 2;
	}

	/**
	 * 
	 * @return the solution values of input fields, i.e. the values which need to be entered into input fields to solve
	 *         the pyramid
	 */
	public List<Integer> getSolutionValues() {
		List<Integer> solutions = Lists.newArrayList();
		for (int i = 0; i < solution.size(); i++) {
			if (startValues.get(i) == null) {
				solutions.add(solution.get(i));
			}
		}
		return solutions;
	}

	public boolean isStartBlock(int row, int column) {
		if (row < size && column < size - row) {
			return startValues.get(getIndex(row, column)) != null;
		}
		return false;
	}

}
