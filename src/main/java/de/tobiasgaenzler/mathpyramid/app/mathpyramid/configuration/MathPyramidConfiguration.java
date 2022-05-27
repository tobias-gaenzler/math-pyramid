package de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mathpyramid")
public class MathPyramidConfiguration {
    public static final int DEFAULT_SIZE = 3;
    public static final int DEFAULT_MAX_VALUE = 100;

    private Integer maxValue = DEFAULT_MAX_VALUE;
    private Integer defaultSize = DEFAULT_SIZE;

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(Integer defaultSize) {
        this.defaultSize = defaultSize;
    }
}
