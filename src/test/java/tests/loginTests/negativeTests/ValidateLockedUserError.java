package tests.loginTests.negativeTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidateLockedUserError {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Use WebDriverManager to setup ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Initialize WebDriver
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Navigate to the login page
        driver.get("https://www.saucedemo.com/");
    }

    @Test
    public void testLoginWithStandardUser() {
        login("standard_user", "secret_sauce");

        // Validate URL
        String expectedUrl = "https://www.saucedemo.com/inventory.html";
        Assert.assertEquals(driver.getCurrentUrl(), expectedUrl);

        // Validate title
        String expectedTitle = "Swag Labs";
        Assert.assertEquals(driver.getTitle(), expectedTitle);
    }

    @Test
    public void testLoginWithOtherUsers() {
        String[] users = {"problem_user", "performance_glitch_user", "error_user", "visual_user"};

        for (String user : users) {
            login(user, "secret_sauce");

            // Validate URL
            String expectedUrl = "https://www.saucedemo.com/inventory.html";
            Assert.assertEquals(driver.getCurrentUrl(), expectedUrl);

            // Validate title
            String expectedTitle = "Swag Labs";
            Assert.assertEquals(driver.getTitle(), expectedTitle);

            // Logout after each user
            logout();
        }
    }

    @Test
    public void testNegativeLoginScenarios() {
        // Login with the user locked_out_user
        login("locked_out_user", "secret_sauce");

        // Validate error message
        String expectedErrorMessage = "Epic sadface: Sorry, this user has been locked out.";
        validateErrorMessage(expectedErrorMessage);

        // Logout after the test
        logout();

        // Execute login scenarios with different combinations
        String[] usernames = {"standard_user", "incorrect_user", "", "correct_user", "", ""};
        String[] passwords = {"incorrect_password", "secret_sauce", "correct_password", "", "", ""};

        for (int i = 0; i < usernames.length; i++) {
            login(usernames[i], passwords[i]);

            // Validate error message for each scenario
            switch (i) {
                case 0:
                    expectedErrorMessage = "Epic sadface: Username and password do not match any user in this service";
                    break;
                case 1:
                    expectedErrorMessage = "Epic sadface: Username and password do not match any user in this service";
                    break;
                case 2:
                    expectedErrorMessage = "Epic sadface: Password is required";
                    break;
                case 3:
                    expectedErrorMessage = "Epic sadface: Password is required";
                    break;
                case 4:
                    expectedErrorMessage = "Epic sadface: Username is required";
                    break;
                case 5:
                    expectedErrorMessage = "Epic sadface: Username is required";
                    break;
            }

            validateErrorMessage(expectedErrorMessage);

            // Logout after each scenario
            logout();
        }
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser after the test
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    private void logout() {
        WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
        WebElement logoutButton = driver.findElement(By.id("logout_sidebar_link"));

        menuButton.click();
        logoutButton.click();
    }

    private void validateErrorMessage(String expectedErrorMessage) {
        WebElement errorElement = driver.findElement(By.cssSelector("[data-test='error']"));
        String actualErrorMessage = errorElement.getText();

        Assert.assertEquals(actualErrorMessage, expectedErrorMessage);
    }
}
