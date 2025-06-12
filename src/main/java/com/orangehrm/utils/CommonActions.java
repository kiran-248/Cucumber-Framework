package com.orangehrm.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonActions {

    private WebDriver driver;

    // Constructor to initialize WebDriver
    public CommonActions(WebDriver driver) {
        this.driver = driver;
    }

    // Method to send keys to the first matching element using findElements
    public void sendKeysToElement(By locator, String text) {
        // Directly using findElements without waiting explicitly
        List<WebElement> elements = driver.findElements(locator); // Find elements without waiting
        if (elements.size() > 0) {
            elements.get(0).sendKeys(text);  // Send keys to the first matching element
        } else {
            System.out.println("Element not found");
        }
    }

    // Method to click on the first matching element
    public  void clickElement(By locator) {
        List<WebElement> elements = driver.findElements(locator);  // Find elements without waiting
        if (elements.size() > 0) {
            elements.get(0).click();  // Click on the first matching element
        } else {
            System.out.println("Element not found "+locator);
        }
    }

    // Method to retrieve text from the first matching element
    public String getElementText(By locator) {
        List<WebElement> elements = driver.findElements(locator);  // Find elements without waiting
        if (elements.size() > 0) {
            return elements.get(0).getText();  // Retrieve text from the first matching element
        } else {
            System.out.println("Element not found");
            return null;
        }
    }

    // Method to retrieve multiple elements using findElements
    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);  // Return list of elements matching the locator
    }
    // Method to perform moveToElement (hover) action using By locator


    public void moveToElement(By locator) {
        // Find the element using the By locator
        WebElement element = driver.findElement(locator);

        // Create an Actions instance to perform the moveToElement action
        Actions actions = new Actions(driver);

        // Perform the hover action over the element
        actions.moveToElement(element).perform();
    }
    public static String fetchedText = "";

    public void clickElementFromListByText(By locator, String textToFind) {
        List<WebElement> elements = driver.findElements(locator); // Find elements using locator

        for (WebElement element : elements) {
            if (element.getText().trim().equalsIgnoreCase(textToFind)) {
                System.out.println(element.getText());
                element.click();
                System.out.println("Clicked on element with text: " + textToFind);
                fetchedText = element.getText();
                return;  // Stop after clicking the matching element
            }
        }
    }
    public void selectDropdownByText(WebDriver driver, By locator, String visibleText) {
        WebElement dropdownElement = driver.findElement(locator); // Use locator directly
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(visibleText);
    }

    public static void selectDropdownByValue(WebDriver driver, String locator, String value) {
        Select dropdown = new Select(driver.findElement(By.xpath(locator)));
        dropdown.selectByValue(value);
    }
    public static void selectDropdownByIndex(WebDriver driver, String locator, int index) {
        Select dropdown = new Select(driver.findElement(By.xpath(locator)));
        dropdown.selectByIndex(index);
    }

    public static List<String> getAllDropdownOptions(WebDriver driver, String locator) {
        Select dropdown = new Select(driver.findElement(By.xpath(locator)));
        List<String> optionsText = new ArrayList<>();
        for (WebElement option : dropdown.getOptions()) {
            optionsText.add(option.getText());
        }
        return optionsText;
    }

    public static void deselectAllOptions(WebDriver driver, String locator) {
        Select dropdown = new Select(driver.findElement(By.xpath(locator)));
        dropdown.deselectAll();
    }

    public static boolean isMultiSelectDropdown(WebDriver driver, String locator) {
        Select dropdown = new Select(driver.findElement(By.xpath(locator)));
        return dropdown.isMultiple();
    }

    public static void selectRadioButton(WebDriver driver, String locator) {
        WebElement radioButton = driver.findElement(By.xpath(locator));
        if (!radioButton.isSelected()) {
            radioButton.click();
        }
    }
    public void rightClick(By locator) {
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }
    public static void selectRadioButtonByValue(WebDriver driver, By groupLocator, String value) {
        List<WebElement> radioButtons = driver.findElements(groupLocator);
        for (WebElement radio : radioButtons) {
            if (radio.getAttribute("value").equalsIgnoreCase(value)) {
                radio.click();
                break;
            }
        }
    }

    public static boolean isRadioButtonSelected(WebDriver driver, By locator) {
        return driver.findElement(locator).isSelected();
    }

    public void verifyColumnSorting(By locator, boolean ascending) {
        List<String> actualValues = new ArrayList<>();

        try {
            // Find all elements using the given locator
            List<WebElement> elements = driver.findElements(locator);

            if (elements.isEmpty()) {
                System.out.println("⚠ No elements found for locator: " + locator);
                return; // Exit gracefully if no elements are found
            }

            for (WebElement element : elements) {
                String text = element.getText().trim();

                // Ensure it's not empty before adding to the list
                if (!text.isEmpty()) {
                    actualValues.add(text);
                }
            }

            // Print extracted values
            System.out.println("Actual Column Values: " + actualValues);

            // Create a sorted copy
            List<String> expectedValues = new ArrayList<>(actualValues);

            if (ascending) {
                Collections.sort(expectedValues); // Ascending order
            } else {
                Collections.sort(expectedValues, Collections.reverseOrder()); // Descending order
            }

            // Print sorted values
            System.out.println("Expected Sorted Values: " + expectedValues);


            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(actualValues, expectedValues,
                    "Column values are NOT sorted in " + (ascending ? "ascending" : "descending") + " order!");

        } catch (StaleElementReferenceException e) {
            System.err.println("⚠ StaleElementReferenceException: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("⚠ Exception occurred: " + e.getMessage());
        }



    }


    private List<String> defaultData = new ArrayList<>();
    private List<String> currentData = new ArrayList<>();

    public void captureDefaultData(By columnDataLocator) {
        defaultData.clear();
        List<WebElement> elements = driver.findElements(columnDataLocator);

        for (WebElement element : elements) {
            defaultData.add(element.getText().trim());
            System.out.println("Default Data " + element.getText());
        }
    }

    // Capture current data after clearing sort
    public void captureCurrentData(By columnDataLocator) {
        currentData.clear();
        List<WebElement> elements = driver.findElements(columnDataLocator);

        for (WebElement element : elements) {
            currentData.add(element.getText().trim());
            System.out.println("Current Data " + element.getText());
        }
    }

    // Getters to return captured data
    public List<String> getDefaultData() {
        return defaultData;
    }

    public List<String> getCurrentData() {
        return currentData;
    }

    public void verifyColumnSortingUsingJS(By locator, boolean ascending) {
        List<String> actualValues = new ArrayList<>();

        try {
            // Find all elements using the given locator
            List<WebElement> elements = driver.findElements(locator);

            if (elements.isEmpty()) {
                System.out.println("⚠ No elements found for locator: " + locator);
                return; // Exit gracefully if no elements are found
            }

            for (WebElement element : elements) {
//                String text = element.getText();
                JavascriptExecutor js = (JavascriptExecutor) driver; // JavaScript Executor
                String text = (String) js.executeScript("return arguments[0].textContent;", element);
                // Ensure it's not empty before adding to the list
                if (!text.isEmpty()) {
                    actualValues.add(text);
                }
            }

            // Print extracted values
            System.out.println("Actual Column Values: " + actualValues);

            // Create a sorted copy
            List<String> expectedValues = new ArrayList<>(actualValues);

            if (ascending) {
                Collections.sort(expectedValues); // Ascending order
            } else {
                Collections.sort(expectedValues, Collections.reverseOrder()); // Descending order
            }

            // Print sorted values
            System.out.println("Expected Sorted Values: " + expectedValues);

            // Assert that actual matches expected
            Assert.assertEquals(actualValues, expectedValues,
                    "Column values are NOT sorted in " + (ascending ? "ascending" : "descending") + " order!");

        } catch (StaleElementReferenceException e) {
            System.err.println("⚠ StaleElementReferenceException: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("⚠ Exception occurred: " + e.getMessage());
        }
    }

    public void clickElementUsingJS(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Scroll element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500); // Allow time for scrolling to settle

            // Wait until element is clickable
            element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click(); // Normal click

        } catch (Exception e) {
            System.out.println("Clicking element via JavaScript as fallback: " + locator);
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String getTextUsingJS(WebDriver driver, By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(locator);
        return (String) js.executeScript("return arguments[0].innerText;", element);
    }

    public void sendKeysUsingJS(WebDriver driver, By locator, String text) {
        WebElement element = driver.findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("arguments[0].scrollIntoView(true);", element);
        // Clear the input field
        js.executeScript("arguments[0].value = '';", element);

        // Enter the new value
        js.executeScript("arguments[0].value = arguments[1];", element, text);
        // Trigger change and blur events to ensure UI registers the update
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
        js.executeScript("arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));", element);
    }

    public static void clickRandomlyInsideElement(WebDriver driver, By locator) {
        WebElement element = driver.findElement(locator);

        // Ensure the element is visible and in viewport
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();

        // Ensure width and height are positive
        if (width <= 0 || height <= 0) {
            throw new IllegalStateException("Element size is invalid: " + width + "x" + height);
        }

        // Generate random coordinates safely inside the element
        Random random = new Random();
        int randomX = random.nextInt(Math.max(1, width - 10)) + 5;
        int randomY = random.nextInt(Math.max(1, height - 10)) + 5;

        // Click at the generated position
        Actions actions = new Actions(driver);
        actions.moveToElement(element, randomX - (width / 2), randomY - (height / 2)).click().perform();
    }

    public void doubleClickElement(By locator) {
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
    }

    public void clearAndSendKeys(By locator, String text) {
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getElementAttributeValue(By locator) {
        List<WebElement> elements = driver.findElements(locator);  // Find elements without waiting
        if (elements.size() > 0) {
            return elements.get(0).getAttribute("value");  // Retrieve text from the first matching element
        } else {
            System.out.println("Element not found");
            return null;
        }
    }

    public void clickEnter(By locator) {
        WebElement element = driver.findElement(locator);
        element.sendKeys(Keys.ENTER);
    }

    public String generateRandomTime() {
        Random random = new Random();

        // Generate random HH between -999 and 999
        int hours = random.nextInt(1999) - 999; // (-999 to 999)

        // Generate random MM between 0 and 59
        int minutes = random.nextInt(60); // (0 to 59)

        // Format HH:MM (ensuring MM is always two digits)
        return String.format("%d:%02d", hours, minutes);
    }

    public void clickElementWithRetry(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        int attempts = 0;
        while (attempts < 3) {  // Retry up to 3 times
            try {
                // Wait until the element is visible
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                element = driver.findElement(locator);  // Locate again to avoid StaleElementReferenceException
                element.click();
                return;  // Exit loop if click is successful
            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException caught, retrying... Attempt: " + (attempts + 1));
            }catch (ElementClickInterceptedException e) {
                System.out.println("ElementClickInterceptedException caught, retrying... Attempt: " + (attempts + 1));
                WaitUtils.waitForSecondsUsingThread(2);
            } catch (TimeoutException e) {
                System.out.println("TimeoutException caught, retrying... Attempt: " + (attempts + 1));
            } catch (NoSuchElementException e) {
                System.out.println("NoSuchElementException caught, retrying... Attempt: " + (attempts + 1));
            }
            attempts++;
        }
        throw new RuntimeException("Failed to click the element after multiple attempts");
    }

    public void selectDateFromCalendar(String dateToSelect) {
        //date format should be "yyyy/MM/dd"
        WaitUtils.waitForSeconds(3);
        By date = By.xpath("(//div[@class='dx-popup-content']//td[contains(@data-value,'"+dateToSelect+"')])[last()]");
        clickElementJS(date);

    }

    // Function to click an element using JavaScript Executor
    public void clickElementJS(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
            System.out.println("Clicked on element using JavaScript: " + locator.toString());
        } catch (Exception e) {
            System.out.println("Failed to click element using JavaScript: " + locator.toString());
            e.printStackTrace();
        }
    }

    public String getCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    // Function to get future date
    public static String getFutureDateForTheDate(String dateTimeValue, int years, int months, int days) {
        // List of possible date formats
        List<String> formats = Arrays.asList("MM/dd/yyyy", "yyyy/MM/dd", "yyyy-MM-dd");

        LocalDate date = null;
        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                date = LocalDate.parse(dateTimeValue, formatter);
                break; // Exit loop once successfully parsed
            } catch (DateTimeParseException ignored) {
                // Try next format
            }
        }

        if (date == null) {
            throw new IllegalArgumentException("Unsupported date format: " + dateTimeValue);
        }

        // Add specified years, months, and days
        LocalDate futureDate = date.plusYears(years).plusMonths(months).plusDays(days);

        // Format output to "MM/dd/yyyy"
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return futureDate.format(outputFormatter);
    }

    // Function to convert an unknown date format to the expected format
    public String convertToRequiredFormat(String inputDate, String expectedFormat) {
        // List of possible input date formats
        List<String> formats = Arrays.asList(
                "MM/dd/yyyy HH:mm",   // 04/12/2025 10:30
                "yyyy/MM/dd HH:mm",   // 2025/04/12 10:30
                "yyyy-MM-dd HH:mm",   // 2025-04-12 10:30
                "MM/dd/yyyy",         // 04/12/2025
                "yyyy/MM/dd",         // 2025/04/12
                "yyyy-MM-dd"          // 2025-04-12
        );

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

                if (format.contains("HH:mm")) {
                    // Parse as LocalDateTime when time is present
                    LocalDateTime dateTime = LocalDateTime.parse(inputDate, formatter);
                    return dateTime.format(DateTimeFormatter.ofPattern(expectedFormat));
                } else {
                    // Parse as LocalDate when only the date is present
                    LocalDate date = LocalDate.parse(inputDate, formatter);
                    return date.format(DateTimeFormatter.ofPattern(expectedFormat));
                }
            } catch (DateTimeParseException ignored) {
                // Try the next format
            }
        }

        throw new IllegalArgumentException("Unsupported date format: " + inputDate);
    }

    public void selectTimeFromCalendar(String hoursToModify, String minutesToModify) {
        By hours = By.xpath("(//input[@aria-label='hours'])[last()]");
        By minutes = By.xpath("(//input[@aria-label='minutes'])[last()]");
        WaitUtils.waitForElementToBeVisible(driver,hours,10);
        clearAndSendKeys(hours, hoursToModify);
        clearAndSendKeys(minutes, minutesToModify);
    }

    // Function to generate a random number with the specified number of digits
    public static int generateRandomNumber(int digits) {
        if (digits <= 0) {
            throw new IllegalArgumentException("Number of digits must be greater than 0.");
        }

        // Define the range for the random number (10^(digits-1) to (10^digits)-1)
        int min = (int) Math.pow(10, digits - 1);  // Smallest number with given digits
        int max = (int) Math.pow(10, digits) - 1;  // Largest number with given digits

        // Generate random number within the range
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public boolean verifyElementIsDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();  // Returns true if visible
        } catch (Exception e) {
            System.out.println("Element not found or not visible: " + locator);
            return false;  // Returns false if element is not found or not visible
        }
    }

    public static List<String> getElementTexts(WebDriver driver, By locator) {
        List<String> values = new ArrayList<>();
        List<WebElement> elements = driver.findElements(locator); // Find elements using locator

        for (WebElement element : elements) {
            values.add(element.getAttribute("Value").trim());
            System.out.println("Values " + element);// Get text and remove extra spaces
        }
        return values;
    }


    public static String getElementTextusingJS(WebDriver driver, By locator) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(locator);  // Find the element using the locator

        // Execute JavaScript to get text content of the element
        return (String) jsExecutor.executeScript(
                "return arguments[0].textContent || arguments[0].innerText;",
                element
        );

    }


    public void clickElementByTextUsingJS(By locator, String textToClick) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for all elements to be present
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        for (WebElement element : elements) {
            try {
                if (element.getText().trim().equalsIgnoreCase(textToClick)) {
                    jsExecutor.executeScript("arguments[0].click();", element);
                    System.out.println("Clicked on element with text: " + textToClick);
                    return; // Exit after clicking the matched element
                }
            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException encountered. Retrying...");
                elements = driver.findElements(locator); // Re-locate elements
            }
        }

        System.out.println("Element with text '" + textToClick + "' not found!");
    }


    public void verifyListElementsContainText(By locator, String expectedText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();
            System.out.println("Element Text: " + actualText);  // Print each element text

            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }
            if (!actualText.contains(expectedText)) {
                System.out.println("Text mismatch found: " + actualText);
                throw new AssertionError("Element text does not contain expected text: " + expectedText);
            }
        }

        System.out.println("All elements contain the expected text: " + expectedText);
    }

    public void verifyListElementsDoNotContainText(By locator, String unexpectedText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();
            System.out.println("Element Text: " + actualText);  // Print each element text

            // Skip empty texts
            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }

            if (actualText.contains(unexpectedText)) {
                System.out.println("Unexpected text found: " + actualText);
                throw new AssertionError("Element text contains unexpected text: " + unexpectedText);
            }
        }

        System.out.println("None of the non-empty elements contain the unexpected text: " + unexpectedText);
    }

    public void verifyListElementsStartWithText(By locator, String expectedStartText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();
            System.out.println("Element Text: " + actualText);  // Print each element text

            // Skip empty texts
            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }

            if (!actualText.startsWith(expectedStartText)) {
                System.out.println("Text does not start with expected value: " + actualText);
                throw new AssertionError("Element text does not start with expected text: " + expectedStartText);
            }
        }

        System.out.println("All non-empty elements start with the expected text: " + expectedStartText);
    }

    public void verifyListElementsEndWithText(By locator, String expectedEndText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();

            // Print each element text
            System.out.println("Element Text: " + actualText);

            // Skip empty texts
            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }

            if (!actualText.endsWith(expectedEndText)) {
                System.out.println("Text does not end with expected value: " + actualText);
                throw new AssertionError("Element text does not end with expected text: " + expectedEndText);
            }
        }

        System.out.println("All non-empty elements end with the expected text: " + expectedEndText);
    }

    public void verifyListElementsEqualToText(By locator, String expectedText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();
            System.out.println("Element Text: " + actualText);

            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }

            if (!actualText.equals(expectedText)) {
                System.out.println("Text does not exactly match: " + actualText);
                throw new AssertionError("Element text does not equal expected text: " + expectedText);
            }
        }

        System.out.println("All non-empty elements exactly match the expected text: " + expectedText);
    }

    public void verifyListElementsNotEqualToText(By locator, String unexpectedText) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualText = element.getText().trim();
            System.out.println("Element Text: " + actualText);

            if (actualText.isEmpty()) {
                System.out.println("Skipping empty text...");
                continue;
            }

            if (actualText.equals(unexpectedText)) {
                System.out.println("Unexpected exact match found: " + actualText);
                throw new AssertionError("Element text should not equal: " + unexpectedText);
            }
        }

        System.out.println("None of the non-empty elements exactly match the unexpected text: " + unexpectedText);
    }

    public void verifyListDatesLessThanExpected(By locator, String expectedDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate expectedDate = LocalDate.parse(expectedDateStr, formatter);

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String fullDateStr = element.getText().trim(); // e.g. "04/07/2025 22:25 GMT+5:30"
            System.out.println("Full Date Text: " + fullDateStr);

            if (fullDateStr.isEmpty()) {
                System.out.println("Skipping empty date...");
                continue;
            }

            String actualDateStr = fullDateStr.split(" ")[0]; // Extract "04/07/2025"
            LocalDate actualDate = LocalDate.parse(actualDateStr, formatter);

            if (!actualDate.isBefore(expectedDate)) {
                throw new AssertionError("❌ Date " + actualDate + " is not before expected date: " + expectedDate);
            }
        }

        System.out.println("✅ All valid dates are before: " + expectedDateStr);
    }

    public void verifyListDatesGreaterThanExpected(By locator, String expectedDateStr) {
        // Use only MM/dd/yyyy to match UI format like "05/15/2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate expectedDate;
        try {
            expectedDate = LocalDate.parse(expectedDateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("❌ Invalid expected date format: " + expectedDateStr);
        }

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String fullDateStr = element.getText().trim(); // e.g. "05/15/2025 12:38 GMT+5:30"
            System.out.println("Full Date Text: " + fullDateStr);

            if (fullDateStr.isEmpty()) {
                System.out.println("Skipping empty date...");
                continue;
            }

            String actualDateStr = fullDateStr.split(" ")[0]; // Extract "MM/dd/yyyy"
            LocalDate actualDate;
            try {
                actualDate = LocalDate.parse(actualDateStr, formatter);
                System.out.println("Parsed actual date: " + actualDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("❌ Could not parse actual date: " + actualDateStr);
            }

            if (!actualDate.isAfter(expectedDate)) {
                throw new AssertionError("❌ Date " + actualDate + " is not after expected date: " + expectedDate);
            }
        }

        System.out.println("✅ All valid dates are after: " + expectedDateStr);
    }


    public void verifyListDatesLessThanOrEqualToExpected(By locator, String expectedDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate expectedDate = LocalDate.parse(expectedDateStr, formatter);

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String fullDateStr = element.getText().trim(); // e.g. "04/07/2025 22:25 GMT+5:30"
            System.out.println("Full Date Text: " + fullDateStr);

            if (fullDateStr.isEmpty()) {
                System.out.println("Skipping empty date...");
                continue;
            }

            String actualDateStr = fullDateStr.split(" ")[0];
            LocalDate actualDate = LocalDate.parse(actualDateStr, formatter);

            if (actualDate.isAfter(expectedDate)) {
                throw new AssertionError("❌ Date " + actualDate + " is after expected date: " + expectedDate);
            }
        }

        System.out.println("✅ All valid dates are less than or equal to: " + expectedDateStr);
    }
    public void verifyListDatesGreaterThanOrEqualToExpected(By locator, String expectedDateStr) {
        // Use MM/dd/yyyy format to match date strings like "05/15/2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate expectedDate;

        try {
            expectedDate = LocalDate.parse(expectedDateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("❌ Invalid expected date format: " + expectedDateStr);
        }

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String fullDateStr = element.getText().trim(); // e.g. "05/15/2025 12:38 GMT+5:30"
            System.out.println("Full Date Text: " + fullDateStr);

            if (fullDateStr.isEmpty()) {
                System.out.println("Skipping empty date...");
                continue;
            }

            String actualDateStr = fullDateStr.split(" ")[0]; // Extract date portion
            LocalDate actualDate;
            try {
                actualDate = LocalDate.parse(actualDateStr, formatter);
                System.out.println("Parsed actual date: " + actualDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("❌ Could not parse actual date: " + actualDateStr);
            }

            if (actualDate.isBefore(expectedDate)) {
                throw new AssertionError("❌ Date " + actualDate + " is before expected date: " + expectedDate);
            }
        }

        System.out.println("✅ All valid dates are greater than or equal to: " + expectedDateStr);
    }


    public void verifyStringListValuesLessThanExpected(By locator, String expectedValue) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualValue = element.getText().trim();
            System.out.println("Actual Value: " + actualValue);

            if (actualValue.isEmpty()) {
                System.out.println("Skipping empty value...");
                continue;
            }

            if (actualValue.compareTo(expectedValue) >= 0) {
                throw new AssertionError("❌ \"" + actualValue + "\" is not less than \"" + expectedValue + "\"");
            }
        }

        System.out.println("✅ All string values are less than: " + expectedValue);
    }

    public void verifyStringListValuesGreaterThanExpected(By locator, String expectedValue) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualValue = element.getText().trim();
            System.out.println("Actual Value: " + actualValue);

            if (actualValue.isEmpty()) {
                System.out.println("Skipping empty value...");
                continue;
            }

            if (actualValue.compareTo(expectedValue) <= 0) {
                throw new AssertionError("❌ \"" + actualValue + "\" is not greater than \"" + expectedValue + "\"");
            }
        }

        System.out.println("✅ All string values are greater than: " + expectedValue);
    }

    public void verifyStringListValuesLessThanOrEqual(By locator, String expectedValue) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualValue = element.getText().trim();
            System.out.println("Actual Value: " + actualValue);

            if (actualValue.isEmpty()) {
                System.out.println("Skipping empty value...");
                continue;
            }

            if (actualValue.compareTo(expectedValue) > 0) {
                throw new AssertionError("❌ \"" + actualValue + "\" is not less than or equal to \"" + expectedValue + "\"");
            }
        }

        System.out.println("✅ All string values are less than or equal to: " + expectedValue);
    }

    public void verifyStringListValuesGreaterThanOrEqual(By locator, String expectedValue) {
        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            String actualValue = element.getText().trim();
            System.out.println("Actual Value: " + actualValue);

            if (actualValue.isEmpty()) {
                System.out.println("Skipping empty value...");
                continue;
            }

            if (actualValue.compareTo(expectedValue) < 0) {
                throw new AssertionError("❌ \"" + actualValue + "\" is not greater than or equal to \"" + expectedValue + "\"");
            }
        }

        System.out.println("✅ All string values are greater than or equal to: " + expectedValue);
    }

    public void assertDatesBetweenRange(String startDateStr, String endDateStr, By dateElementsLocator) {
        // Flexible formatter to handle single-digit months and days
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

        // Parse the start and end dates
        LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
        LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);

        // Get all date elements from the table
        List<WebElement> dateElements = driver.findElements(dateElementsLocator);

        for (WebElement element : dateElements) {
            String fullText = element.getText().trim(); // Example: "04/07/2025 06:05 GMT+5:30"
            if (fullText.isEmpty()) continue;

            String[] parts = fullText.split(" ");
            String dateOnly = parts[0].trim(); // Get only "04/07/2025"

            try {
                LocalDate currentDate = LocalDate.parse(dateOnly, dateFormatter);

                if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
                    throw new AssertionError("❌ Date " + currentDate + " is outside the range: " + startDate + " to " + endDate);
                }
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Failed to parse date from: " + fullText);
                e.printStackTrace();
            }
        }

        System.out.println("✅ All dates are within the specified range.");
    }

    public void assertOnlyValuesInRangeVisible(String startStr, String endStr, By elementsLocator) {
        int start = Integer.parseInt(startStr);
        int end = Integer.parseInt(endStr);

        List<WebElement> elements = driver.findElements(elementsLocator);

        for (WebElement element : elements) {
            String valueStr = element.getText().trim(); // e.g., "1320", "1499"
            if (valueStr.isEmpty()) continue;

            try {
                int value = Integer.parseInt(valueStr);

                if (value < start || value > end) {
                    throw new AssertionError("❌ Invalid value displayed: " + value + " is OUTSIDE the allowed range (" + start + " to " + end + ")");
                }

            } catch (NumberFormatException e) {
                throw new AssertionError("⚠️ Found non-numeric value: '" + valueStr + "'");
            }
        }

        System.out.println("✅ All visible values are within the allowed range (" + start + " to " + end + ").");
    }

    public void assertRecordsLessThanOrEqualToExpected(By locator, int expectedMaxCount) {

        int actualCount = driver.findElements(locator).size();
        System.out.println("Actual size " + actualCount);
        List<WebElement> list = driver.findElements(locator);


        if (actualCount <= expectedMaxCount) {
            System.out.println("✅ Passed: Actual record count (" + actualCount + ") is within the limit (" + expectedMaxCount + ")");
        } else
        {
            throw new AssertionError("❌ Failed: Expected <= " + expectedMaxCount + " records, but found " + actualCount);
        }
    }

    public void clickNextUntilLastPage(WebDriver driver, By nextButtonLocator, By currentPageLocator, By uiPageLocator) {
        while (isNextButtonEnabled(driver, nextButtonLocator)) {
            // Get current page from internal state or system
            String currentPageText = driver.findElement(currentPageLocator).getText().trim();
            System.out.println("Current Page Text " + currentPageText);

            // Get displayed page number from UI
            String uiPageText = driver.findElement(uiPageLocator).getText().trim();
            String uiPageNumber = extractPageNumber(uiPageText);
            System.out.println("UI Page Text " + uiPageNumber);
            Assert.assertEquals(currentPageText, uiPageNumber, "Mismatch between current page and UI displayed page!");

            clickNext(driver, nextButtonLocator);

            try {
                Thread.sleep(2000); // replace with WebDriverWait for better practice
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractPageNumber(String text) {
        // Defensive check and simple splitting
        if (text != null && text.contains("Page")) {
            String[] parts = text.split(" ");
            if (parts.length >= 2) {
                return parts[1]; // Index 1 is the page number
            }
        }
        throw new IllegalArgumentException("Could not extract page number from text: " + text);
    }
    public void clickNext(WebDriver driver, By nextButtonLocator) {
        WebElement nextBtn = driver.findElement(nextButtonLocator);
        nextBtn.click();
    }


    public boolean isNextButtonEnabled(WebDriver driver, By nextButtonLocator) {
        try {
            WebElement nextBtn = driver.findElement(nextButtonLocator);
            String classAttr = nextBtn.getAttribute("class");

            // If it contains "dx-button-disable", it's visually disabled
            return classAttr == null || !classAttr.contains("dx-button-disable");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickElementUsingActions(By locator) {

        WebElement element = driver.findElement(locator);

        Actions actions = new Actions(driver);

        actions.moveToElement(element).click().perform();

    }

    public String getElementTextByXPath(String xpath) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        String script = "var element = document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +

                "return element ? element.textContent.trim() : null;";

        return (String) jsExecutor.executeScript(script, xpath);

    }

    public void waitForElementToBeInvisible(By locator, int timeout) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));

    }

    public List<String> getTextFromAllElementsUsingJS(By locator) {

        List<String> textList = new ArrayList<>();

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {

            JavascriptExecutor js = (JavascriptExecutor) driver; // JavaScript Executor

            String text = (String) js.executeScript("return arguments[0].textContent;", element);

            textList.add(text.trim());

        }

        return textList;

    }

    public String getElementAttribute(By locator,String attribute) {

        List<WebElement> elements = driver.findElements(locator);  // Find elements without waiting

        if (elements.size() > 0) {

            return elements.get(0).getAttribute(attribute);  // Retrieve text from the first matching element

        } else {

            System.out.println("Element not found");

            return null;

        }

    }

    public String getMonthNumber(String monthName) {

        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase(); // Normalize input

        return String.valueOf(months.indexOf(monthName) + 1);  // Convert to 1-based index

    }

    // Function to convert month abbreviation to full month name

    public String getFullMonthName(String monthAbbreviation) {

        switch (monthAbbreviation.toUpperCase()) {

            case "JAN": return "January";

            case "FEB": return "February";

            case "MAR": return "March";

            case "APR": return "April";

            case "MAY": return "May";

            case "JUN": return "June";

            case "JUL": return "July";

            case "AUG": return "August";

            case "SEP": return "September";

            case "OCT": return "October";

            case "NOV": return "November";

            case "DEC": return "December";

            default: throw new IllegalArgumentException("Invalid month abbreviation: " + monthAbbreviation);

        }

    }

    public void clickEnterUsingRobot() {

        Robot robot = null;

        try {

            robot = new Robot();

        } catch (AWTException e) {

            throw new RuntimeException(e);

        }

        robot.keyPress(KeyEvent.VK_ENTER);

    }

    // Method to check if the file exists and matches the format

    public boolean verifyDownloadedFile(String downloadDirectory, String expectedFileName) {

        // Define the download directory

        File dir = new File(downloadDirectory);

        // Get the list of files in the directory

        File[] files = dir.listFiles();

        if (files != null) {

            // Loop through files in the directory to find one with the correct format

            for (File file : files) {

                if (file.getName().equals(expectedFileName)) {

                    System.out.println("File found: " + file.getName());

                    return true; // File exists with the correct name and format

                }

            }

        }

        System.out.println("File not found with the expected format: " + expectedFileName);

        return false; // File not found

    }

    public void clickElementWithRetryUsingElementByIndex(List<WebElement> elements, int index, int timeout) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        int attempts = 0;

        while (attempts < 3) {  // Retry up to 3 times

            try {

                // Re-locate the element by the index each time

                WebElement element = elements.get(index);

                wait.until(ExpectedConditions.visibilityOf(element));  // Wait until visible

                element.click();  // Attempt to click the element

                return;  // Exit loop if click is successful

            } catch (StaleElementReferenceException e) {

                System.out.println("StaleElementReferenceException caught, retrying... Attempt: " + (attempts + 1));

                WaitUtils.waitForSecondsUsingThread(10);

            }

            attempts++;

        }

        throw new RuntimeException("Failed to click the element after multiple attempts");

    }

    public static String generateRandomStringWithOutNumeric(int length) {

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            sb.append(charSequence.charAt(RANDOM.nextInt(charSequence.length())));

        }

        return sb.toString();

    }

    public List<String> getTextFromAllElements(By locator) {

        List<String> textList = new ArrayList<>();

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {

            textList.add(element.getText().trim());

        }

        return textList;

    }

    public String getCurrentYear() {

        // Get the current date

        LocalDate currentDate = LocalDate.now();

        // Return the current year

        return String.valueOf(currentDate.getYear());

    }

    public String fetchTextFromElement(By locator) {

        WebElement element = driver.findElement(locator);
        String text = element.getText();
        System.out.println("Fetched text: " + text);
        return text;

    }



    private static final List<String> months = Stream.of(

                    "January", "February", "March", "April", "May", "June",

                    "July", "August", "September", "October", "November", "December")

            .collect(Collectors.toList());

    private static final String charSequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";



    public boolean isElementDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

}
