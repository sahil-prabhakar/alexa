package com.liveaction.selenium.pageObject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.liveaction.selenium.framework.BasePageObject;

public class AlexaHomePage extends BasePageObject {

	@FindBy(xpath = "//h5[contains(text(),'Total Sites Linking In')]//following-sibling::span")
	private WebElement totalSiteLinkingInLbl;
	
	@FindBy(xpath = "//table[@id='related_link_table']/tbody/tr/td/a")
	private List<WebElement> totalRelatedLnks;
	
	@FindBy(xpath ="//a[contains(text(),'Upgrade to view')]")
	private WebElement upgradeToViewLnk;
	
	
	public int getTotalSiteLinkingInCount() {
		return Integer.parseInt(totalSiteLinkingInLbl.getText().replace(",", ""));
	}
	
	public int getTotalRelatedLinks() {
		return totalRelatedLnks.size();
	}
	
	public float getPercentOfVisitors(String countryName){
		WebElement percentOfVisitors=findElement(By.xpath("//a[contains(text(),'"+countryName+"')]//parent::td//following-sibling::td[1]/span"));
		return Float.parseFloat(percentOfVisitors.getText().replace("%", ""));
	}
	
	public void navigateToPlanPage(){
		clickAndWait(upgradeToViewLnk);
	}
}
