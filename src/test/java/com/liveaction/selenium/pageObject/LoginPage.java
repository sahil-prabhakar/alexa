package com.liveaction.selenium.pageObject;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.liveaction.selenium.framework.BasePageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePageObject {

    @Inject @Named("app.loginUrl")
    private String homePageURL;

    @Inject @Named("app.username")
    private String username;

    @Inject @Named("app.password")
    private String password;
    


    // TODO username/password css changed - uncomment when all environments are same
//    @FindBy(css="#spUsername")
    @FindBy(css="input[name='username']")
    private WebElement userNameTxtBox;

    @FindBy(css="form[name='liveActionLogin'] > button")
    private WebElement loginWithLiveAction;
    
//    @FindBy(css="#spPassword")
    @FindBy(css="input[name='password']")
    private WebElement passwordTxtBox;

    @FindBy(css="input[value ='Login'")
    private WebElement loginBtn;

    @FindBy(css="#error_field")
    private WebElement invalidLoginText;

    public void navigateToLoginPage() {
        driver.get(homePageURL);
        waitFor(pageLoad());
     //   isloginWithLiveActionFieldPresent();
	}

	public void setLoginDetails() {
        setLoginDetails(username, password);
	}

	public void setLoginDetails(String userName, String password) {
		inputText(userNameTxtBox, userName);
		inputText(passwordTxtBox, password);
	}

	public void clickLoginWithLiveAction() {
		waitForElement(loginWithLiveAction);
		clickAndWait(loginWithLiveAction);
	}
	

	public void clickLogin() {
		clickAndWait(loginBtn);
	}

    public void login() {
    	driver.manage().window().maximize();
        navigateToLoginPage();
        clickLoginWithLiveAction();
        setLoginDetails();
		clickAndWait(loginBtn);
	}

    public String getInvalidLoginText(){
        waitForElement(invalidLoginText);
        return invalidLoginText.getText();
    }

    public boolean isloginWithLiveActionFieldPresent() {
        return isPresentWithWait(loginWithLiveAction);
    }
}
