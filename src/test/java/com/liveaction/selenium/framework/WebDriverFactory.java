package com.liveaction.selenium.framework;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.fail;

/**
 * @author hanson
 */
public class WebDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    private Map<String,Browser> browserMap = getBrowserMap();

    public WebDriver getDriver(String browserName, String browserVersion, String operatingSystem, String host, String port, long timeoutLimit){
    	System.setProperty("org.uncommons.reportng.escape-output", "false");
        Browser browser = browserMap.get(browserName.toLowerCase());
        if(browser == null){
            fail("Unable to match the following browser property to a browser: " + browserName);
        }
        DesiredCapabilities capabilities = browser.getDesiredCapabilities();

        String webDriverDir = System.getProperty("user.dir") + "/src/test/resources/webdriver/";

        WebDriver driver = null;

        boolean runLocally = host.toLowerCase().contains("localhost");

        if (host.toLowerCase().contains("saucelabs")) {
            capabilities.setCapability("version", browserVersion);
            capabilities.setCapability("platform", operatingSystem);
            logger.info("Preparing to run on SauceLabs");
            return createRemoteWebDriver(host, port, capabilities);
        }

        String downloadFilepath = System.getProperty("user.dir") + "/src/test/resources/download";
        File file;
        switch(browser){
            case Firefox:
                FirefoxProfile firefoxProfile = new FirefoxProfile();
                firefoxProfile.setAcceptUntrustedCertificates(true);
                capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
                driver = runLocally ? new FirefoxDriver(capabilities) : null;
                break;
            case InternetExplorer:
                file = new File(webDriverDir + "IEDriverServer.exe");
                System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
                driver = runLocally ? new InternetExplorerDriver(capabilities) : null;
                break;
            case Safari:
                capabilities.setCapability("acceptSslCerts", true);
                driver = runLocally ? new SafariDriver(capabilities) : null;
                break;
            case GoogleChrome:            	
            	
            	HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            	chromePrefs.put("profile.default_content_settings.popups", 0);
            	chromePrefs.put("download.default_directory", downloadFilepath);
            	
                file = new File(webDriverDir + "chromedriver");
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("prefs", chromePrefs);
                options.addArguments("--disable-extensions");
                capabilities.setCapability("chrome.switches", Collections.singletonList("--start-maximized"));
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                driver = runLocally ? new ChromeDriver(capabilities) : null;
                break;
        }

        if(driver == null){
            driver = createRemoteWebDriver(host, port, capabilities);
        }

        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(timeoutLimit, TimeUnit.SECONDS);
            driver.manage().window().maximize();
        }

        return driver;
    }

    private enum Browser {
        Firefox(DesiredCapabilities.firefox()),
        InternetExplorer(DesiredCapabilities.internetExplorer()),
        GoogleChrome(DesiredCapabilities.chrome()),
        Safari(DesiredCapabilities.safari());

        private DesiredCapabilities desiredCapabilities;

        Browser(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
        }
        public DesiredCapabilities getDesiredCapabilities() {
            return desiredCapabilities;
        }
    }

    /**
     * Generate map containing possible property values specifying which browser to use.
     * Note that all key values are lowercase for comparison.
     *
     * @return Map containing String to browser mappings
     */
    private Map<String,Browser> getBrowserMap() {
        Map<String,Browser> browserMap = new HashMap<>();

        browserMap.put("ff", Browser.Firefox);
        browserMap.put("firefox", Browser.Firefox);
        browserMap.put("mozillafirefox", Browser.Firefox);

        browserMap.put("ie", Browser.InternetExplorer);
        browserMap.put("internetexplorer", Browser.InternetExplorer);
        browserMap.put("iexplorer", Browser.InternetExplorer);

        browserMap.put("gc", Browser.GoogleChrome);
        browserMap.put("googlechrome", Browser.GoogleChrome);
        browserMap.put("chrome", Browser.GoogleChrome);

        browserMap.put("safari", Browser.Safari);

        return browserMap;
    }

    private RemoteWebDriver createRemoteWebDriver(String host, String port, Capabilities capabilities){
        try {
            URL remoteUrl = new URL("http://" + host + ":" + port + "/wd/hub");
            return new RemoteWebDriver(remoteUrl, capabilities);
        } catch (MalformedURLException e) {
            fail("Malformed URL based on host and port set in properties:" + host + ":" + port, e);
        }
        return null;
    }

}
