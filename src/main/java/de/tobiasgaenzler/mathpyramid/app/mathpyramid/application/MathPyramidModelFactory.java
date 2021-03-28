package de.tobiasgaenzler.mathpyramid.app.mathpyramid.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class MathPyramidModelFactory {

    private final MathPyramidCalculator calculator;

    @Autowired
    public MathPyramidModelFactory(MathPyramidCalculator calculator) {
        this.calculator = calculator;
    }

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
        List<Integer> solution = calculator.createRandomSolution(size, maxValue);
        return new MathPyramidModel(size, calculator.getUniquelySolvableRandomStartValues(size, solution), solution, calculator);
    }


}
