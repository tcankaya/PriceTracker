package app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String FILE = "prices.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    // --------------------
    // JSON handling
    // --------------------
    public static List<Product> loadProducts() throws IOException {
        File file = new File(FILE);
        if (!file.exists()) {
            System.out.println("⚠️ prices.json not found, returning empty list.");
            return new ArrayList<>();
        }
        return mapper.readValue(file, new TypeReference<List<Product>>() {});
    }

    public static void saveProducts(List<Product> products) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE), products);
    }

    // --------------------
    // Cookie handling
    // --------------------
    public static void handleCookies(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            boolean clicked = false;

            // Try by IDs
            for (String id : Config.getCookieIds()) {
                try {
                    WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
                    btn.click();
                    System.out.println("✅ Accepted cookies via ID: " + id);
                    clicked = true;
                    break;
                } catch (Exception ignored) {}
            }

            // Try by classes
            if (!clicked) {
                for (String cls : Config.getCookieClasses()) {
                    try {
                        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("button." + cls + ", a." + cls)
                        ));
                        btn.click();
                        System.out.println("✅ Accepted cookies via class: " + cls);
                        clicked = true;
                        break;
                    } catch (Exception ignored) {}
                }
            }

            // Try by button text
            if (!clicked) {
                for (String text : Config.getCookieTexts()) {
                    try {
                        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[contains(text(),'" + text + "')] | //a[contains(text(),'" + text + "')]")
                        ));
                        btn.click();
                        System.out.println("✅ Accepted cookies via text: " + text);
                        clicked = true;
                        break;
                    } catch (Exception ignored) {}
                }
            }

            if (!clicked) System.out.println("ℹ️ No cookie popup found.");

        } catch (Exception e) {
            System.out.println("⚠️ Cookie handling failed: " + e.getMessage());
        }
    }

    // Normalize price string to double
    public static double normalizePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) return -1;

        priceText = priceText.replaceAll("[^\\d,\\.]", ""); // keep only digits, dot, comma

        if (priceText.matches("\\d{1,3}(\\.\\d{3})*,\\d{2}")) { // 1.234,56
            priceText = priceText.replace(".", "").replace(",", ".");
        } else if (priceText.matches("\\d{1,3}(,\\d{3})*\\.\\d{2}")) { // 1,234.56
            priceText = priceText.replace(",", "");
        } else if (priceText.contains(",")) {
            priceText = priceText.replace(",", ".");
        }

        try {
            return Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Failed to parse price: " + priceText);
            return -1;
        }
    }

    // Fetching Prices
    public static double fetchPriceFromPage(WebDriver driver) {
        for (String selector : Config.getPriceSelectors()) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(selector));
                if (!elements.isEmpty()) {
                    String priceText = elements.get(0).getText();
                    return normalizePrice(priceText);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Selector failed: " + selector + " | " + e.getMessage());
            }
        }
        System.err.println("❌ Could not find any price element on page!");
        return -1;
    }
}