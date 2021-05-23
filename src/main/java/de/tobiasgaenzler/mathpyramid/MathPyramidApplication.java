package de.tobiasgaenzler.mathpyramid;

import de.tobiasgaenzler.mathpyramid.app.mathpyramid.configuration.MathPyramidConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MathPyramidConfiguration.class)
public class MathPyramidApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathPyramidApplication.class, args);
	}

}
