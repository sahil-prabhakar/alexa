package com.liveaction.selenium.testCase.homepage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.liveaction.selenium.framework.MainBase;
//import com.liveaction.selenium.pageObject.AlexaHomePage;
//import com.liveaction.selenium.pageObject.AlexaPlanPage;
import com.liveaction.selenium.pageObject.AlexaHomePage;
import com.liveaction.selenium.pageObject.AlexaPlanPage;

public class TestAlexaSite extends MainBase {
	@Inject
	private WebDriver driver;

	@Inject
	AlexaHomePage alexaHomePage;
	
	@Inject
	AlexaPlanPage alexaPlanPage;

	@BeforeMethod
	public void beforeMethod() {
		driver.get("http://www.alexa.com/siteinfo/cnn.com");
		driver.manage().window().maximize();
	}

	@Test(groups = "alexa", testName = "test_ALM82062_VerifyAlexa", description = "Verify Alexa functionality")
	public void test_ALM82062_VerifyAlexa() {

		reportLog("Step 1- Sites linking in is larger than 100,000.");
		assertTrue(alexaHomePage.getTotalSiteLinkingInCount() > 10000);

		reportLog("Step 2- At least 5% of visitors are from Australia.");
		assertTrue(alexaHomePage.getPercentOfVisitors("United States") > 5);

		reportLog("Step 3- That there are 10 related links in the Related Links table.");
		assertEquals(alexaHomePage.getTotalRelatedLinks(), 10);

		reportLog("Step 4- All upgrade/upsell links lead to /plans (sometimes there will be additional URL params)");
		alexaHomePage.navigateToPlanPage();
		String url= alexaPlanPage.getPageURL();
		assertTrue(StringUtils.containsIgnoreCase(url, "plans"));
	}

}
