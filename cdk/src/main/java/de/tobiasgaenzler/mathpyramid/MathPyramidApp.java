package de.tobiasgaenzler.mathpyramid;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class MathPyramidApp {
    public static void main(final String[] args) {
        App app = new App();

        new MathPyramidStack(app, "math-pyramid-ec2", StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build());

        app.synth();
    }
}

