package com.liveaction.selenium.framework;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public abstract class BasePageObject {
    private static final Logger logger = LoggerFactory.getLogger(BasePageObject.class);

    protected WebDriver driver;
   
	protected String title;
	protected static final int DEFAULT_WAIT_4_ELEMENT = 20;
	protected static final int DEFAULT_WAIT_4_PAGE = 30;
	protected static WebDriverWait ajaxWait;
    @Inject @Named("framework.implicitTimeout")
    protected long timeout;

    /**
     * This setter is important as it will initialize the elements for each PageObject that extends
     * this class, allowing the Selenium FindBy annotations to work.
     * It takes the place of constructor initialization to keep the PageObjects cleaner.
     *
     * @param driver the webDriver instance
     */
    @Inject
    public void setWebDriver(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
       // driver.manage().window().maximize();
    }

    public void clickAndWait(WebElement element) {
        logger.info("clickAndWait");
        element.click();
        waitFor(pageLoad());
    }
    
    public String returnTitle() {
		return title;
	}
    public void click(WebElement element) {
        logger.info("click");
        element.click();
    }
    
    public void scrollDown()
    
    {
 	   JavascriptExecutor jse = (JavascriptExecutor)driver;
 	   jse.executeScript("window.scrollBy(0,250)", "");
 	   
    }
    
    private void setImplicitWait(int timeInSec) {
        logger.info("setImplicitWait, timeInSec={}", timeInSec);
        driver.manage().timeouts().implicitlyWait(timeInSec, TimeUnit.SECONDS);
    }

    private void resetImplicitWait() {
        logger.info("resetImplicitWait");
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    public void waitFor(ExpectedCondition<Boolean> expectedCondition){
        setImplicitWait(0);
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(expectedCondition);
        resetImplicitWait();
    }

    public ExpectedCondition<Boolean> pageLoad(){
        return jsExpectedCondition("document.readyState === 'complete'");
    }

    public ExpectedCondition<Boolean> jsExpectedCondition(final String jsCondition) {
        return driver -> {
            Object ret = ((JavascriptExecutor) driver).executeScript("return " + jsCondition);
            return ret.equals(true);
        };
    }

    public void inputText(WebElement element, String text) {
        logger.info("inputText, text={}", text);
        element.clear();
        element.sendKeys(text);
    }

    public void waitForElement(WebElement element) {
        logger.info("waitForElement");
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElement(String locator) {
        logger.info("waitForElement");
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ByLocator(locator)));
    }
    
    public void clickPotentiallyStaleElement(WebElement element) {
        logger.info("clickPotentiallyStaleElement");

        new WebDriverWait(driver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver driver) -> {
                    element.click();
                    return true;
                });
    }
    
    

    public boolean isPresentWithWait(WebElement element) {
        logger.info("isPresent with wait");

        new WebDriverWait(driver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver driver) -> {
                    element.isDisplayed();
                    return true;
                });

        boolean present = element.isDisplayed();
        logger.debug("isPresent: {}", present);
        return present;
    }

 // Handle locator type
 	public By ByLocator(String locator) {
 		By result = null;
 		if (locator.startsWith("//")) {
 			result = By.xpath(locator);
 		} else if (locator.startsWith("css=")) {
 			result = By.cssSelector(locator.replace("css=", ""));
 		} else if (locator.startsWith("#")) {
 			result = By.id(locator.replace("#", ""));
 		} else if (locator.startsWith("name=")) {
 			result = By.name(locator.replace("name=", ""));
 		} else if (locator.startsWith("link=")) {
 			result = By.linkText(locator.replace("link=", ""));
 		} else {
 			result = By.className(locator);
 		}
 		return result;
 	}
 	
	public static String generateRandomString(int lettersNum) {
		String finalString = "";

		int numberOfLetters = 25;
		long randomNumber;
		for (int i = 0; i < lettersNum; i++) {
			char letter = 97;
			randomNumber = Math.round(Math.random() * numberOfLetters);
			letter += randomNumber;
			finalString += String.valueOf(letter);
		}
		return finalString;
	}
	public static String getValueFromPropertiesFile(String value,
			String fileName) {
		Properties prop = new Properties();
		String selectedItem = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			prop.load(fileInputStream);
			selectedItem = prop.getProperty(value);
			fileInputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return selectedItem;
	}

	public static String getValueFromTestDataFile(String value) {
		Properties prop = new Properties();
		String selectedItem = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(
					"test_data.properties");
			prop.load(fileInputStream);
			selectedItem = prop.getProperty(value);
			fileInputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return selectedItem;
	}
	public void waitForCopyRightToBeVisible() {
		String text = "Copyright ©";
		String currentUrl = driver.getCurrentUrl();
		if (!currentUrl.contains("graph_share")) {
			if (currentUrl.contains("wpm.biz")
					&& !currentUrl.contains("https://admin")) {
				//waitForTextPresentInElement(By.cssSelector("span.copyright"),
				waitForTextPresentInElement(By.xpath(".//*[@id='staticFooter']/div/span[1]"),	
						
						text, DEFAULT_WAIT_4_ELEMENT);
			} else {
				waitForElementPresent(
						By.xpath("//p[contains(text(),'Copyright')]"),
						DEFAULT_WAIT_4_ELEMENT);
			}
		}
	}
	
	public boolean verifyURL(String text)
	{
		boolean value=false;
		String currentUrl=driver.getCurrentUrl();
		if (currentUrl.contains(text))
			return true;
		else
			return value;
			
	}

	public WebDriver goToPage(final By button) {
		waitForElementPresent(button, DEFAULT_WAIT_4_ELEMENT);
		findElement(button).click();
		//waitForCopyRightToBeVisible();
		return driver;
	}

	public WebDriver goToPage(WebElement webElement) {
		waitForPageLoaded(driver);
		waitForElementPresent(webElement, DEFAULT_WAIT_4_ELEMENT);
		webElement.click();
		waitForCopyRightToBeVisible();
		return driver;
	}



	public WebDriver getDriver() {
		return driver;
	}

	public WebElement findElement(By by) {
		if (driver instanceof ChromeDriver
				|| driver instanceof InternetExplorerDriver) {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		WebElement foundElement = null;
		for (int milis = 0; milis < 3000; milis = milis + 200) {
			try {
				foundElement = driver.findElement(by);
				return foundElement;
			} catch (Exception e) {
				//Utils.hardWaitMilliSeconds(200);
			}
		}
		return null;
	}

	public void assertByPageTitle() {
		try {
			if (driver instanceof ChromeDriver
					|| driver instanceof InternetExplorerDriver
					|| driver instanceof FirefoxDriver) {
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertTrue(returnTitle().equals(driver.getTitle()));
		System.out.println(returnTitle());
	}
	public List<String> findAllLinksOnPage() {
		List<String> links = new ArrayList<>();
		List<WebElement> linkElements = driver.findElements(By.tagName("a"));
		for (WebElement each : linkElements) {
			String link = each.getAttribute("href");
			if (link == null || link.contains("mailto")
					|| link.contains("javascript")) {
				continue;
			}
			links.add(link);
		}
		return links;
	}
	
	public boolean isResponseForLinkTwoHundredOrThreeOTwo(String link) {
		int code = 0;
		Reporter.log("Link: " + link);
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			code = connection.getResponseCode();
			Reporter.log("Code: " + code);
		} catch (Exception e) {
			Reporter.log(e.toString());
			return false;
		}
		if (link.contains("pager") || code == 403) {
			return true;
		}
		return code == 200 || code == 302;
	}

	

	public void setWaitTime(WebDriver driver, int waitTime) {
		driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.SECONDS);
	}

	public void setWaitTimeToZero(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}

	public void customizableCondition(WebDriver driver, int waitTime,
			final Boolean condition) {
		// setWaitTimeToZero(driver);
		new WebDriverWait(driver, waitTime)
				.until(new ExpectedCondition<Boolean>() {

					public Boolean apply(WebDriver driver) {
						return condition;
					}
				});
		// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
	}
	
	public WebElement waitForElementClickable(WebElement webElement, int timeOutInSeconds) {
		WebElement element;
		try {

			// setWaitTimeToZero(driver);
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions.elementToBeClickable(webElement));

			// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public WebElement waitForElementPresent(final By by, int timeOutInSeconds) {
		WebElement element;
		try {

			// setWaitTimeToZero(driver);
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions
					.presenceOfElementLocated(by));

			// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public WebElement waitForElementPresent(WebElement webElement,
			int timeOutInSeconds) {
		WebElement element;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions.visibilityOf(webElement));
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean waitForTextPresentInElement(WebElement webElement,
			String text, int timeOutInSeconds) {
		boolean notVisible;
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		notVisible = wait.until(ExpectedConditions.textToBePresentInElement(
				webElement, text));

		return notVisible;
	}

	public boolean waitForTextPresentInElement(By by, String text,
			int timeOutInSeconds) {
		boolean notVisible;
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		notVisible = wait.until(ExpectedConditions
				.textToBePresentInElementLocated(by, text));

		return notVisible;
	}

	public Boolean isElementPresent(String locator) {
		Boolean result = false;
		try {
			getDriver().findElement(ByLocator(locator));
			result = true;
		} catch (Exception ex) {
		}
		return result;
	}
	
	public void WaitForElementNotPresent(String locator, int timeout) {
		for (int i = 0; i < timeout; i++) {
			if (!isElementPresent(locator)) {
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	
	public int findNumberOfSpecificElementsInContainer(By container, By element) {
		WebElement mainDiv = driver.findElement(container);
		List<WebElement> divs = mainDiv.findElements(element);
		return divs.size();
	}

	public WebDriver hoverOverElementAndClick(WebDriver driver,
		WebElement toBeHovered, WebElement toBeClicked) {
		Actions builder = new Actions(driver);
		builder.moveToElement(toBeHovered).build().perform();
		waitForElementPresent(toBeClicked, DEFAULT_WAIT_4_ELEMENT);
		toBeClicked.click();
		waitForPageLoaded(driver);
		return driver;
	}
	
	// Select value from drop down
	public void selectDropDown(WebElement element, String targetValue) {
		
		new Select(element)
				.selectByVisibleText(targetValue);

	}

	public void waitForElementToBecomeVisible(By by, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}
	
	public void waitForElementToBecomeInvisible(By by){
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	public void waitForAjaxRequestsToComplete() {
		(new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE))
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						JavascriptExecutor js = (JavascriptExecutor) d;
						return (Boolean) js
								.executeScript("return jQuery.active == 0");
					}
				});
	}
	
	public void waitForAjaxBusyToComplete(){
		try{ 
			WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//*[@id='ajaxBusy']")));
		} catch (Exception e){
			e.printStackTrace();
		}
	}	

	public void myClick(WebElement element) {
		new WebDriverWait(driver, DEFAULT_WAIT_4_ELEMENT)
				.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForPageLoaded(WebDriver driver) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, 20);
		wait.until(expectation);
		waitForCopyRightToBeVisible();
	}

	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean isTextPresentOnPage(String text) {
		return driver.findElement(By.tagName("body")).getText().contains(text);
	}

	public boolean isFileAvailableForDownload(WebElement webElement)
			throws Exception {
		int code = 0;
		String downloadUrl = webElement.getAttribute("href");
		URL url = new URL(downloadUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		code = connection.getResponseCode();
		Reporter.log("The response code for download is " + code);
		return code == 200;
	}
  
	
	
	public void javascriptButtonClick(WebDriver driver, WebElement webElement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", webElement);
	}

	public void waitForLoading(){
		String locator = "//div[contains(text()= 'Loading.')]";
		WaitForElementNotPresent(locator, 60);
	}
	
	
	public void clickOn(String locator) {
		this.WaitForElementNotPresent(locator, 30);
		Assert.assertTrue(isElementPresent(locator), "Element Locator :"
				+ locator + " Not found");
		WebElement el = getDriver().findElement(ByLocator(locator));
		el.click();
	}
	public void takeRemoteWebDriverScreenShot(String fileName) {
		File screenshot = ((TakesScreenshot) new Augmenter().augment(driver))
				.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshot, new File(fileName));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void waitForTextNotToBeVisible(String text, int timeoutInSeconds) {
		int startWait = 0;
		while (isTextPresentOnPage(text)) {
			//Utils.hardWaitSeconds(1);
			startWait++;
			if (startWait == timeoutInSeconds) {
				throw new TimeoutException();
			}
		}
	}
	  public void waitForWebElementPresent(WebElement element) {
		  WebDriverWait ajaxWait = new WebDriverWait(driver, 30);
	        ajaxWait.until(ExpectedConditions.visibilityOf(element));
	    }



	public void logBrowserType(String browser) {
		Reporter.log(" ================= Executed on " + browser
				+ " =======================");
	}
 	
}
