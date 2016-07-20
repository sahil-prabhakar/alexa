package com.liveaction.selenium.pageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.liveaction.selenium.framework.BasePageObject;

public class ConfigurationPage extends BasePageObject {

	// driver.findElement(By.xpath("//a[contains(.,'Configure')]"));
	@FindBy(xpath = "//a[contains(.,'LDAP Management')]")
	private WebElement ldapManagement;
	@FindBy(xpath = "//a[contains(.,'Site Management')]")
	private WebElement siteManagement;

	@FindBy(xpath = "//button[@class='btn btn-success']")
	private WebElement Import;
	@FindBy(xpath = "//button(@class='buttons')]")
	private WebElement edit;

	public void clicksiteManagement() {
		clickAndWait(siteManagement);

	}

	public void clickldapManagement() {
		clickAndWait(ldapManagement);
	}

}
