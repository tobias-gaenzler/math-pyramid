package de.tobiasgaenzler.mathpyramid;

import io.github.bonigarcia.seljup.Arguments;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// run headless in CI environment.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class IntegrationTest {

    @LocalServerPort
    private int randomApplicationPort;

    @Test
    void testMathPyramidLoads(@Arguments("--headless") ChromeDriver driver) {
        driver.get("http://localhost:" + randomApplicationPort);
        waitForApplicationAvailable(driver);
        assertThat(driver.getTitle())
                .contains("Math-Pyramid");
    }

    @Test
    void testMathPyramidCanBeSolved(@Arguments("--headless") ChromeDriver driver) {
        driver.get("http://localhost:" + randomApplicationPort);
        waitForApplicationAvailable(driver);

        // find plain dom text fields
        List<WebElement> textFields = driver.findElements(By.cssSelector("vaadin-text-field"));
        // find shadow dom input fields (from top to bottom, left to right)
        List<WebElement> inputFields = textFields.stream().map(textField -> {
            WebElement shadowTextField = getShadowRootElement(textField, driver);
            return shadowTextField.findElement(By.cssSelector("input"));
        }).collect(Collectors.toList());

        fillFieldIfEmpty(inputFields.get(0),"4");
        fillFieldIfEmpty(inputFields.get(1),"2");
        fillFieldIfEmpty(inputFields.get(2),"2");
        fillFieldIfEmpty(inputFields.get(3),"1");
        fillFieldIfEmpty(inputFields.get(4),"1");
        fillFieldIfEmpty(inputFields.get(5),"1");
        // check if notification is present
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("vaadin-notification-card")));
    }

    private void fillFieldIfEmpty(WebElement field, String value) {
        if (field.getAttribute("value").isEmpty()) {
            field.sendKeys(value);
            field.sendKeys(Keys.TAB);
        }
    }

    private void waitForApplicationAvailable(ChromeDriver driver) {
        // wait for application to start: search for element with class "app-layout"
        WebDriverWait wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("app-layout")));
    }


    public WebElement getShadowRootElement(WebElement element, ChromeDriver driver) {
        return (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);
    }
}
