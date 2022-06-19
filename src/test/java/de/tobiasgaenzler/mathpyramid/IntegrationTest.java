package de.tobiasgaenzler.mathpyramid;

import io.github.bonigarcia.seljup.Arguments;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.Keys.ENTER;

// run headless in CI environment.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class IntegrationTest {

    @LocalServerPort
    private int randomApplicationPort;

    @Test
    void testMathPyramidLoads(@Arguments("--headless") ChromeDriver driver) {
        driver.get("http://localhost:" + randomApplicationPort + "/practice");
        waitForApplicationAvailable(driver);
        assertThat(driver.getTitle())
                .contains("Math Pyramid");
    }

    @Test
    void testMathPyramidCanBeSolved(@Arguments("--headless") ChromeDriver driver) {
        driver.get("http://localhost:" + randomApplicationPort + "/practice");
        waitForApplicationAvailable(driver);

        // find plain dom text fields
        List<WebElement> textFields = driver.findElements(By.cssSelector("vaadin-integer-field"));
        // find shadow dom input fields (from top to bottom, left to right)
        List<WebElement> inputFields = textFields.stream().map(textField -> {
            SearchContext shadowTextField = getShadowRootElement(textField, driver);
            return shadowTextField.findElement(By.cssSelector("input"));
        }).toList();

        fillFieldIfEmpty(inputFields.get(0), "4");
        fillFieldIfEmpty(inputFields.get(1), "2");
        fillFieldIfEmpty(inputFields.get(2), "2");
        fillFieldIfEmpty(inputFields.get(3), "1");
        fillFieldIfEmpty(inputFields.get(4), "1");
        fillFieldIfEmpty(inputFields.get(5), "1");
        // check if notification is present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("vaadin-notification-card")));
    }

    @Test
    void testNameCanBeChanged(@Arguments("--headless") ChromeDriver driver) {
        driver.get("http://localhost:" + randomApplicationPort + "/practice");
        waitForApplicationAvailable(driver);

        WebElement nameButton = driver.findElement(By.id("name-button"));
        nameButton.click();

        // check if name change form is present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("change-name-form")));

        // change name
        WebElement nameField = driver.findElement(By.className("user-name"));
        nameField.sendKeys("Tobias");
        nameField.sendKeys(ENTER);

        wait.until(ExpectedConditions.textToBePresentInElement(nameButton, "Tobias"));
    }


    private void fillFieldIfEmpty(WebElement field, String value) {
        if (field.getAttribute("value").isEmpty()) {
            field.sendKeys(value);
            field.sendKeys(Keys.TAB);
        }
    }

    private void waitForApplicationAvailable(ChromeDriver driver) {
        // wait for application to start: search for element with class "app-layout"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("app-layout")));
    }


    public SearchContext getShadowRootElement(WebElement element, ChromeDriver driver) {
        return (SearchContext) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);
    }
}
