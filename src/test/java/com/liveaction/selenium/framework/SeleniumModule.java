package com.liveaction.selenium.framework;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Properties;

/**
 * @author hanson
 */
public class SeleniumModule implements Module {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumModule.class);

    @Provides
    @Singleton
    public WebDriver provideBaseDriver(@Named("host") String host,
                                 @Named("port") String port,
                                 @Named("browser") String browser,
                                 @Named("browser.version") String version,
                                 @Named("operatingSystem") String operatingSystem,
                                 @Named("framework.implicitTimeout") long limit,
                                 @Named("grid.enabled") String gridEnabled){

        WebDriverFactory webDriverFactory = new WebDriverFactory();
        logger.info("Creating WebDriver with the following configuration: "
                        + "\n host: " + host
                        + "\n port: " + port
                        + "\n browser: " + browser
                        + "\n browser version: " + version
                        + "\n operating system: " + operatingSystem
                        + "\n timeout limit: " + limit
                        + "\n run on grid: " + gridEnabled
        );
        return webDriverFactory.getDriver(browser, version, operatingSystem, host, port, limit);
    }

    @Override
    public void configure(Binder binder) {
        Properties testProps = loadProperties("./src/test/resources/properties/tests.properties");
        Properties appProps = loadProperties("./src/test/resources/properties/application.properties");
        Properties allProperties = mergeProperties(testProps, appProps, System.getProperties());
        determineGridSetup(allProperties);
        logger.debug("Binding properties: " + allProperties);
        Names.bindProperties(binder, allProperties);
    }

    /**
     * Handle the grid.enabled flag by overwriting host and port properties if they are available.
     *
     * @param properties The properties for the application
     */
    private void determineGridSetup(Properties properties) {
        if(Boolean.valueOf(properties.getProperty("grid.enabled"))){
            String gridHost = properties.getProperty("grid.host");
            String gridPort = properties.getProperty("grid.port");
            if(gridHost == null || gridHost.isEmpty() || gridPort == null || gridPort.isEmpty()){
                logger.error("Grid enabled but grid host or port not provided in properties.Using host and port: "
                        + properties.getProperty("host") + ":" + properties.getProperty("port"));
            } else {
                properties.put("host", gridHost);
                properties.put("port", gridPort);
            }
        }
    }

    /**
     * Load a properties file from the file system into a properties object
     *
     * @param filepath the path to the properties file relative to classpath origin
     * @return the loaded Properties
     */
    private Properties loadProperties(String filepath) {
        Properties properties = new Properties();
        try {
            FileInputStream in = new FileInputStream(new File(filepath));
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Properties file not loaded", e);
        }
        return properties;
    }

    /**
     * Merge several properties objects into one. Properties will be overridden sequentially,
     * meaning properties in the last properties object in the list will override any that come before it.
     *
     * @param propertiesObjects all of the Properties objects that are to be merged in order from least to highest precedence
     * @return the single merged Properties
     */
    private Properties mergeProperties(Properties... propertiesObjects) {
        Properties merged = new Properties();
        for(Properties properties : propertiesObjects){
            merged.putAll(properties);
        }
        return merged;
    }

}
