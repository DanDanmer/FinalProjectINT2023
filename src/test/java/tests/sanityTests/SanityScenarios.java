package tests.sanityTests;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class SanityScenarios {

    private static WebDriver driver;

    public static void main(String[] args) {
        setUp();
        executeSanityTest();
        tearDown();
    }

    private static void setUp() {
        // Use WebDriverManager to set up Chromedriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    private static void executeSanityTest() {
        login("standard_user", "secret_sauce");

        // Validate the URL of the products page
        assertCurrentUrl("https://www.saucedemo.com/inventory.html");

        // Validate the title of the page
        assertPageTitle("Swag Labs");

        // Add 2 products to the cart
        addProductToCart("Sauce Labs Backpack");
        addProductToCart("Sauce Labs Bolt T-Shirt");

        // Validate the cart icon has the correct number of items
        assertCartItemCount(2);

        // Navigate to the cart
        navigateToCart();

        // Validate the URL and page title of the cart
        assertCurrentUrl("https://www.saucedemo.com/cart.html");
        assertPageTitle("Your Cart"); // Updated expected title

        // Validate the correct number of items in the cart
        assertCartItemCount(2);

        // Click the Checkout button
        clickCheckoutButton();

        // Validate the URL and page title of the checkout step one
        assertCurrentUrl("https://www.saucedemo.com/checkout-step-one.html");
        assertPageTitle("Checkout: Your Information");

        // Fill the checkout form
        fillCheckoutForm("John", "Doe", "12345");

        // Click the Continue button
        clickContinueButton();

        // Validate the URL and page title of the checkout step two
        assertCurrentUrl("https://www.saucedemo.com/checkout-step-two.html");
        assertPageTitle("Checkout: Overview");

        // Click the Finish button
        clickFinishButton();

        // Validate the URL and page title of the checkout complete page
        assertCurrentUrl("https://www.saucedemo.com/checkout-complete.html");
        assertPageTitle("Checkout: Complete!");

        // Validate the text of each presented message
        assertTextPresent("Your order has been dispatched", By.xpath("//div[@class='complete-text']"));
    }

    private static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void login(String username, String password) {
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys(username);
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
    }

    private static void addProductToCart(String productName) {
        By productLocator = By.xpath("//div[contains(text(), '" + productName + "')]/ancestor::div[@class='inventory_item']//button");

        System.out.println("Trying to locate element with XPath: " + productLocator);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(productLocator));
            System.out.println("Element found. Clicking...");
            addToCartButton.click();
        } catch (TimeoutException e) {
            System.out.println("Timeout Exception: Element not found within the specified time.");
            e.printStackTrace();
        }
    }

    private static void assertCurrentUrl(String expectedUrl) {
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals(actualUrl, expectedUrl, "URL validation failed. Expected: " + expectedUrl + ", Actual: " + actualUrl);
    }

    private static void assertPageTitle(String expectedTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased wait time

        try {
            wait.until(ExpectedConditions.titleIs(expectedTitle)); // Use titleIs for exact title match
        } catch (TimeoutException e) {
            System.out.println("Timeout Exception: Title not found within the specified time.");
            System.out.println("Actual page source: " + driver.getPageSource()); // Print page source on failure
            e.printStackTrace();
        }

        String actualTitle = driver.getTitle();
        Assert.assertTrue(actualTitle.contains(expectedTitle), "Title validation failed. Expected: " + expectedTitle + ", Actual: " + actualTitle);
    }

    private static void assertCartItemCount(int expectedItemCount) {
        WebElement cartItemCount = driver.findElement(By.cssSelector(".shopping_cart_badge"));
        int actualItemCount = Integer.parseInt(cartItemCount.getText());
        Assert.assertEquals(actualItemCount, expectedItemCount, "Cart item count validation failed.");
    }

    private static void navigateToCart() {
        driver.findElement(By.cssSelector(".shopping_cart_link")).click();
    }

    private static void clickCheckoutButton() {
        driver.findElement(By.cssSelector(".checkout_button")).click();
    }

    private static void fillCheckoutForm(String firstName, String lastName, String zipCode) {
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(zipCode);
    }

    private static void clickContinueButton() {
        driver.findElement(By.cssSelector(".cart_button")).click();
    }

    private static void clickFinishButton() {
        driver.findElement(By.cssSelector(".cart_button")).click();
    }

    private static void assertTextPresent(String expectedText, By locator) {
        WebElement element = driver.findElement(locator);
        String actualText = element.getText();
        Assert.assertTrue(actualText.contains(expectedText), "Text validation failed. Expected: " + expectedText + ", Actual: " + actualText);
    }
}
