package app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

    public static WebDriver createDriver() {
        
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-cookie-encryption",
                "--disable-site-isolation-trials",
                "--disable-background-networking",
                "--disable-default-apps",
                "--disable-third-party-cookies"
        );

        return new ChromeDriver(options);
    }
}
