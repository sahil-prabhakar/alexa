package com.liveaction.selenium.pageObject;

import com.liveaction.selenium.framework.BasePageObject;

public class AlexaPlanPage extends BasePageObject {

	public String getPageURL() {
		return driver.getCurrentUrl();
	}
}
