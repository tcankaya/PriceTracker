package app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PriceTracker {
    private static final String FILE = "prices.json";
    private ObjectMapper mapper = new ObjectMapper();

    // Load products from JSON
    public List<Product> loadProducts() throws IOException {
        File file = new File(FILE);
        if (!file.exists()) {
            System.out.println("⚠️ prices.json not found, returning empty list.");
            return new ArrayList<>();
        }
        return mapper.readValue(file, new TypeReference<List<Product>>() {});
    }

    // Save products back to JSON
    public void saveProducts(List<Product> products) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE), products);
    }

    // Handle cookie popup
    public void handleCookies(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Common patterns for cookie/consent buttons
            String[] possibleIds = {
                    "onetrust-accept-btn-handler",
                    "footer_tc_privacy_button_3",
                    "banner-accept-btn"
            };

            String[] possibleClasses = {
                    "cookie-accept",
                    "accept-cookies",
                    "consent-btn"
            };

            String[] possibleTexts = {
                    "TÜMÜNÜ KABUL ET",
                    "Accept All",
                    "Accept Cookies",
                    "Accept All Cookies",
                    "Tüm Çerezleri Kabul Et",
                    "TÜM ÇEREZLERİ KABUL ET",
                    "ACCEPT ALL COOKIES",
                    "Accept All Cookies"
            };

            boolean clicked = false;

            // Try by IDs first
            for (String id : possibleIds) {
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
                for (String cls : possibleClasses) {
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
                for (String text : possibleTexts) {
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

            if (!clicked) {
                System.out.println("ℹ️ No cookie popup found.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Cookie handling failed: " + e.getMessage());
        }
    }

    // Fetch price from product URL
    public double fetchPrice(String url, boolean quitAfter) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-cookie-encryption"); // prevents cookie storage encryption
        options.addArguments("--disable-site-isolation-trials"); // optional
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-third-party-cookies");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);

            // Handle cookies if present (might fail if the targeted cookie button isn't specified in handleCookies method)
            handleCookies(driver);

            // Wait for span element containing "TL" (might fail here if the targeted website using a different selector)
            List<WebElement> elementsWithTL = driver.findElements(By.xpath("//span[contains(text(),'TL')]"));

            if (elementsWithTL.isEmpty()) {
                System.err.println("❌ Could not find any price element on page!");
                return -1;
            }

            // Take the first element with TL as the price
            WebElement priceElement = elementsWithTL.get(0);
            String priceText = priceElement.getText();

            priceText = priceText.replaceAll("[^\\d,\\.]", ""); // remove everything except digits, dot, comma

            if (priceText.matches("\\d{1,3}(\\.\\d{3})*,\\d{2}")) {
                priceText = priceText.replace(".", "").replace(",", ".");
            } else if (priceText.matches("\\d{1,3}(,\\d{3})*\\.\\d{2}")) {
                priceText = priceText.replace(",", "");
            } else if (priceText.contains(",")) {
                priceText = priceText.replace(",", ".");
            }

            return Double.parseDouble(priceText);

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch price from " + url + ": " + e.getMessage());
            return -1;
        } finally {
            if (quitAfter) driver.quit();
        }
    }
}