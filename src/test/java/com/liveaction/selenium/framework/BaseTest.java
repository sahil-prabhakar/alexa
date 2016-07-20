package com.liveaction.selenium.framework;

import com.google.inject.Inject;
import com.liveaction.selenium.pageObject.LoginPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest extends MainBase {
	 
	@Inject private LoginPage loginPage;

	private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
	
	@BeforeClass(alwaysRun = true)
	public void beforeTestClass() throws Exception {
		try {
			loginPage.login();
			
		} catch (Exception e) {
			logger.error("Problem stopping driver.", e);
		}
	}

		
}
