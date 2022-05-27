package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MathPyramidCalculatorTest {

    @Test
    public void testGetDifficultyReturns1ForDifficultPyramidOfSize3() {
        Set<Integer> startValues = Set.of(0, 2, 5);
        Integer difficulty = new MathPyramidCalculator().getDifficulty(startValues, 3);
        assertThat(difficulty).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"3,3,16,1", "4,61,118,31", "5,1179,1085,739", "6,25186,11617,17461"})
    public void testGetDifficultyForAllStartPositionsForPyramids(Integer size, Integer notSolvable, Integer easy, Integer hard) {
        Set<Set<Integer>> startPositions = getMathPyramidCombinations(size, size);
        List<Integer> difficulties = startPositions.stream()
                .map(startValues -> new MathPyramidCalculator().getDifficulty(startValues, size)).toList();
        int numberOfNotUniquelySolvable = (int) difficulties.stream().filter(Objects::isNull).count();
        assertThat(numberOfNotUniquelySolvable).isEqualTo(notSolvable); // not uniquely solvable
        Map<Integer, List<Integer>> groupedDifficulties = difficulties.stream().filter(Objects::nonNull).collect(groupingBy(d -> d));
        assertThat(groupedDifficulties.get(0).size()).isEqualTo(easy); // easy ones
        assertThat(groupedDifficulties.get(1).size()).isEqualTo(hard); // difficult ones
    }

    @Test
    public void testGetMathPyramidCombinationsForPyramidOfSize9() {
        Set<Set<Integer>> result = getMathPyramidCombinations(9, 9);
        assertThat(result.size()).isEqualTo(886_163_135);
    }

    @ParameterizedTest
    @CsvSource({"0,0,0", "0,1,1", "0,2,2", "1,0,3", "1,1,4", "2,0,5"})
    public void testGetIndexReturnsCorrectValues(int row, int col, int index) {
        assertThat(new MathPyramidCalculator().getIndex(row, col, 3)).isEqualTo(index);
    }

    @ParameterizedTest
    @CsvSource({"-10,0", "0,-1", "4,1", "1,5"})
    public void testGetIndexThrowsExceptionOnInvalidValues(int row, int col) {
        assertThatThrownBy(() -> new MathPyramidCalculator().getIndex(row, col, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // This method is not used in the application.
    // Instead, it is used in tests to document the distribution of not uniquely solvable, easy and difficult start positions
    Set<Set<Integer>> getMathPyramidCombinations(int pyramidHeight, int numberOfPrefilledBlocks) {
        int numberOfBlocks = new MathPyramidCalculator().getNumberOfBlocks(pyramidHeight);
        Set<Integer> positionValues = IntStream.range(0, numberOfBlocks).boxed().collect(Collectors.toSet());
        return Sets.combinations(positionValues, numberOfPrefilledBlocks);
    }

    @ParameterizedTest
    @CsvSource({"3,5", "4,100", "5,10000", "10," + Integer.MAX_VALUE})
    public void testMathPyramidCreatesPyramidWithValuesLowerOrEqualThanMaxValue(int size, int maxValue) {
        assertThat(new MathPyramidCalculator().createRandomSolution(size, maxValue).stream()
                .filter(value -> value > maxValue).findFirst().isEmpty())
                .isEqualTo(true);
    }

    @ParameterizedTest
    @CsvSource({"3,6", "4,10", "5,15", "6,21", "7,28", "8,36", "9,45", "10,55"})
    public void testCreatesPyramidWithCorrectSizing(int size, int numberOfBlocks) {
        MathPyramidCalculator mathPyramidCalculator = new MathPyramidCalculator();
        List<Integer> solution = mathPyramidCalculator.createRandomSolution(size, 10_000);
        Map<Integer, Integer> startValues = mathPyramidCalculator.getUniquelySolvableRandomStartValues(size, solution);
        // number of start values is equal to size
        assertThat(startValues.size()).isEqualTo(size);
        // number of blocks is equal to number of solution entries
        assertThat(solution.size()).isEqualTo(numberOfBlocks);
    }




}