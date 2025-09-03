package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class MoviePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators as By objects instead of @FindBy
    private final By recommendedMovies = By.xpath("//div[contains(@class,'sc-133848s-3')]//a[@class='sc-133848s-11 sc-lnhrs7-5 ctsexn bHVBt']");
    private final By movieName = By.xpath("//h1[@class='sc-qswwm9-6 ea-drWB']");
    private final By moviePoster = By.xpath("//section[@class='sc-bsek5f-0 jNOshi']");
    private final By movieDetailed = By.xpath("//h4[contains(@class,'sc-o4g232-2') and contains(text(),'About the movie')]");
    private final By bookingOption = By.xpath("//div[@class='sc-qswwm9-8 fNtHgG']//button//span[text()='Book tickets']");
    private final By moviesTab = By.xpath("//a[text()='Movies']");
    private final By exploreUpcomingMoviesImgLink = By.xpath("//a//img[@alt='Coming Soon']");
    private final By inCinemasNearYouImgLink = By.xpath("//img[@alt='Now Showing']");

    // Constructor
    public MoviePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Helper method to safely find element with retry logic
    private WebElement findElementSafely(By locator, int maxRetries) {
        Exception lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (TimeoutException | NoSuchElementException e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(1000); // Wait 1 second before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        System.out.println("Failed to find element after " + maxRetries + " retries: " + locator);
        if (lastException != null) {
            lastException.printStackTrace();
        }
        return null;
    }

    // Helper method to check if element is displayed safely
    private boolean isElementDisplayedSafely(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return element.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("Element not found or not displayed: " + locator);
            return false;
        }
    }

    // Select first movie from recommended list
    public void selectFirstRecommendedMovie() {
        try {
            List<WebElement> movies = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(recommendedMovies));
            if (!movies.isEmpty()) {
                wait.until(ExpectedConditions.elementToBeClickable(movies.get(0))).click();
            } else {
                throw new RuntimeException("No recommended movies found!");
            }
        } catch (TimeoutException e) {
            throw new RuntimeException("Failed to find recommended movies within timeout period", e);
        }
    }

    // Get movie name
    public boolean isMovieNameDisplayed() {
        return isElementDisplayedSafely(movieName);
    }

    // Check if poster is visible
    public boolean isPosterDisplayed() {
        return isElementDisplayedSafely(moviePoster);
    }

    // Check if detailed is visible with multiple fallback strategies
    public boolean isDetailedPageDisplayed() {
        // Try different variations of the About section
        By[] aboutLocators = {
            By.xpath("//h4[normalize-space()='About the movie']"),
            By.xpath("//h4[contains(text(),'About the movie')]"),
            By.xpath("//h4[contains(text(),'About')]"),
            By.xpath("//h4[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'about')]"),
            movieDetailed // Original locator
        };

        for (By locator : aboutLocators) {
            if (isElementDisplayedSafely(locator)) {
                System.out.println("Found 'About' section with locator: " + locator);
                return true;
            }
        }

        // Debug information
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        
        try {
            List<WebElement> allH4s = driver.findElements(By.tagName("h4"));
            System.out.println("Available h4 elements count: " + allH4s.size());
            for (int i = 0; i < Math.min(allH4s.size(), 5); i++) { // Print first 5 h4s only
                WebElement h4 = allH4s.get(i);
                System.out.println("H4[" + i + "] text: '" + h4.getText() + "' | Class: '" + h4.getAttribute("class") + "'");
            }
        } catch (Exception e) {
            System.out.println("Error getting debug info: " + e.getMessage());
        }
        
        return false;
    }

    // Alternative validation method - check for movie details page by multiple indicators
    public boolean isMovieDetailsPageLoaded() {
        try {
            // Wait for page to stabilize
            Thread.sleep(2000);
            
            boolean hasMovieName = isElementDisplayedSafely(By.xpath("//h1[contains(@class,'sc-qswwm9')]"));
            boolean hasMovieInfo = isElementDisplayedSafely(By.xpath("//*[contains(text(),'About') or contains(text(),'movie') or contains(text(),'Cast') or contains(text(),'Director')]"));
            boolean hasBookingOption = isElementDisplayedSafely(By.xpath("//*[contains(text(),'Book') or contains(text(),'tickets')]"));
            
            System.out.println("Movie Details Page Check:");
            System.out.println("Has Movie Name: " + hasMovieName);
            System.out.println("Has Movie Info: " + hasMovieInfo);
            System.out.println("Has Booking Option: " + hasBookingOption);
            
            // Return true if at least 2 out of 3 elements are present
            return (hasMovieName && hasMovieInfo) || (hasMovieName && hasBookingOption) || (hasMovieInfo && hasBookingOption);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Check if booking option is available
    public boolean isBookingOptionAvailable() {
        return isElementDisplayedSafely(bookingOption);
    }

    // Navigate to Movies tab
    public void clickMoviesTab() {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(moviesTab));
            element.click();
            // Wait for navigation to complete
            Thread.sleep(1000);
        } catch (TimeoutException e) {
            throw new RuntimeException("Failed to click Movies tab", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Navigate to Explore Upcoming Movies
    public void clickExploreUpcomingMovies() {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(exploreUpcomingMoviesImgLink));
            element.click();
            // Wait for navigation to complete
            Thread.sleep(2000);
        } catch (TimeoutException e) {
            throw new RuntimeException("Failed to click Explore Upcoming Movies", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Validate "In Cinemas Near You" link - FIXED for Jenkins
    public boolean isInCinemasNearYouLinkDisplayed() {
        // Multiple strategies to find the element
        By[] inCinemasLocators = {
            By.xpath("//img[@alt='Now Showing']"),
            By.xpath("//a//img[@alt='Now Showing']"),
            By.xpath("//img[contains(@alt,'Now Showing')]"),
            By.xpath("//*[contains(text(),'In Cinemas Near You')]"),
            By.xpath("//a[contains(text(),'In Cinemas Near You')]"),
            By.xpath("//*[contains(text(),'Now Showing')]")
        };

        System.out.println("Searching for 'In Cinemas Near You' link...");
        
        for (By locator : inCinemasLocators) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                if (element.isDisplayed()) {
                    System.out.println("Found 'In Cinemas Near You' with locator: " + locator);
                    return true;
                }
            } catch (TimeoutException e) {
                System.out.println("Locator failed: " + locator);
                continue;
            }
        }

        // Debug: Print available elements
        try {
            List<WebElement> allImages = driver.findElements(By.tagName("img"));
            System.out.println("Available images on page:");
            for (int i = 0; i < Math.min(allImages.size(), 10); i++) {
                WebElement img = allImages.get(i);
                String alt = img.getAttribute("alt");
                String src = img.getAttribute("src");
                System.out.println("IMG[" + i + "] alt: '" + alt + "' src: '" + src + "'");
            }
            
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            System.out.println("\nAvailable links containing relevant text:");
            for (WebElement link : allLinks) {
                String text = link.getText();
                if (text.toLowerCase().contains("cinema") || text.toLowerCase().contains("now") || text.toLowerCase().contains("showing")) {
                    System.out.println("LINK text: '" + text + "' href: '" + link.getAttribute("href") + "'");
                }
            }
        } catch (Exception e) {
            System.out.println("Error during debugging: " + e.getMessage());
        }

        return false;
    }
}
