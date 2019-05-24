package newproject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumCommands {
	public static final int WAIT_TIMEOUT = 20;
	public static WebDriver driver = null;
	
	public static void click(String target)
	{
		driver.findElement(By.xpath(target)).click();
	}
	
	public static void wait(String target)
	{
		new WebDriverWait(driver,WAIT_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(target)));
	}
	
	public static void clearAndtype(String target,String text)
	{
		WebElement inputField = driver.findElement(By.xpath(target));
		inputField.clear();
		inputField.sendKeys(text);
	}
	
	public static void type(String target,String text)
	{
		WebElement inputField = driver.findElement(By.xpath(target));
		inputField.sendKeys(text);
	}

}
