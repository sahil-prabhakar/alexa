package com.liveaction.selenium.framework;

import java.io.File;
import java.io.FileOutputStream;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Guice;

import com.google.inject.Inject;

@Guice(modules = SeleniumModule.class)
public abstract class MainBase extends TestListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

	@Inject
	private WebDriver driver;
	
	@AfterClass(alwaysRun = true)
	public void afterTestClass() throws Exception {
		try {
			//driver.close();
		} catch (Exception e) {
			logger.error("Problem stopping driver.", e);
		}
	}

	public void reportLog(String message) {
		Reporter.log(message);
	}

	// capturing screenshot
	public void captureScreenshot(String fileName) {
		String path = getRootPath();
		try {
			FileOutputStream out = new FileOutputStream("screenshots//" + fileName + ".jpg");
			out.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
			out.close();
			String screen ="file://" +path+"/screenshots/" + fileName + ".jpg";
			Reporter.log("<a href= '" + screen + "' target='_blank' ><img src='"+screen+"' height=\"42\" width=\"42\">" + fileName + "</a>");
		} catch (Exception e) {

		}
	}
	
	
	protected String getRootPath(){
		String path = "";
		File file = new File("");
		String absoluteFilePath = file.getAbsolutePath();
		path = absoluteFilePath.replace("\\\\+", "//");
		return path;
		
	}


}
