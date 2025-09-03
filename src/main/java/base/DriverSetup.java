package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverSetup {
    private static WebDriver driver;

    public static WebDriver getDriver(String browser) {
        if (driver == null) {
            if (browser == null || browser.isEmpty()) {
                browser = ConfigLoader.getProperty("browser");
            }

            switch (browser.toLowerCase()) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    if (isHeadless()) {
                        ffOptions.addArguments("--headless");
                        ffOptions.addArguments("--no-sandbox");
                        ffOptions.addArguments("--disable-dev-shm-usage");
                    }
                    driver = new FirefoxDriver(ffOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (isHeadless()) {
                        edgeOptions.addArguments("--headless=new");
                        edgeOptions.addArguments("--no-sandbox");
                        edgeOptions.addArguments("--disable-dev-shm-usage");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (isHeadless()) {
                        chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--no-sandbox");
                        chromeOptions.addArguments("--disable-dev-shm-usage");
                        chromeOptions.addArguments("--disable-gpu");
                        chromeOptions.addArguments("--window-size=1920,1080");
                    }
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            if (!isHeadless()) {
                driver.manage().window().maximize();
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static boolean isHeadless() {
        // detect CI/Jenkins environment
        String ci = System.getenv("CI");
        return (ci != null && ci.equalsIgnoreCase("true"));
    }
}
