package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import java.util.List;
import java.util.Map;

public class MathPyramidModel {
    private final Integer size; // number or rows, higher number increases difficulty
    private final Map<Integer, Integer> startValues; // numbers which are visible from the start
    private final List<Integer> solution;

    MathPyramidModel(Integer size,
                     Map<Integer, Integer> startValues,
                     List<Integer> solution) {
        this.size = size;
        this.startValues = startValues;
        this.solution = solution;
    }

    public Integer getSize() {
        return size;
    }

    public Map<Integer, Integer> getStartValues() {
        return startValues;
    }

    public List<Integer> getSolution() {
        return solution;
    }
}
