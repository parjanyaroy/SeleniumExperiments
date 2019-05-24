package newproject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
public class SeleniumMain {

	public static WebDriver driver = null;
	public static Map<String,String> credMap = null;
	public static List<String> automationFiles = new ArrayList<String>();
	
	

    public static void main(String[] args) throws Exception {
    	
    	populateCredentials();
    	readTests();
    	for(String fileName : automationFiles){
    		List<SeleniumStep> stepList =readCSV(".\\automationFiles\\"+fileName);
    		initializeDriver(credMap.get("TEST_BROWSER"));
    		salesforceLogin();
    		for(SeleniumStep currentStep : stepList){
    			System.out.println(currentStep.getStepNumber()+". Step Details: "+currentStep.getComment());
    			if(currentStep.getCommand()!=null){
    				if(currentStep.getCommand().equalsIgnoreCase("clearandtype")) {
    					SeleniumCommands.clearAndtype(currentStep.getTarget(), currentStep.getValue());
    			    	
    				}
    				else if(currentStep.getCommand().equalsIgnoreCase("click")) {
    					SeleniumCommands.click(currentStep.getTarget());
    				}
    				else if(currentStep.getCommand().equalsIgnoreCase("wait")) {
    					SeleniumCommands.wait(currentStep.getTarget());
    				}
    				else if(currentStep.getCommand().equalsIgnoreCase("type")) {
    					SeleniumCommands.type(currentStep.getTarget(), currentStep.getValue());
    				}
    			}
    		}
    		Thread.sleep(3000);
    		driver.close();
    	}
    	System.out.println("----------- Program Ends -----------");
    }


	private static void populateCredentials() throws FileNotFoundException {
		File file =new File(".\\credentials.txt"); 
    	Scanner sc = new Scanner(file); 
    	while (sc.hasNextLine()) { 
    	String line = sc.nextLine();
    	if(!line.startsWith("##")){
    		String[] tokens = line.split("=");
    		if(null==credMap)
    			credMap = new HashMap<>();
    		credMap.put(tokens[0].trim(),tokens[1].trim());
    	}
    	}
	}
    
    
    public static void readTests()
    {
    	System.out.println("----------- Preparing List Of Automated Tests -----------");
    	File f = new File(".\\automationFiles\\");
        File[] files = f.listFiles();
        int count=1;
        if (files != null)
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory() && file.getName().endsWith(".csv")) {   
                 System.out.println(count+" : "+file.getName());
                 automationFiles.add(file.getName());
            }
        count++;
        }
        System.out.println("----------- Found:"+(count-1)+" test files -----------");
    }
    
    public static void initializeDriver(String browser)
    {
    	if(browser.equalsIgnoreCase("chrome")){
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--disable-notifications");
    	options.addArguments("--log-level=3");
    	options.addArguments("--silent");
    	System.setProperty("webdriver.chrome.driver",credMap.get("CHROMEDRIVER"));
		driver = new ChromeDriver(options);
    	}
    	else if(browser.equalsIgnoreCase("firefox")){
    	System.setProperty("webdriver.gecko.driver",credMap.get("GECKODRIVER"));
    	System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
    	FirefoxOptions options = new FirefoxOptions();
    	options.addArguments("--disable-notifications");
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        //driver = new FirefoxDriver(capabilities);
        driver = new FirefoxDriver(options);
        }
    	SeleniumCommands.driver=driver;
    }
    
    
    public static void salesforceLogin() throws InterruptedException
    {
    	driver.get(credMap.get("SALESFORCE_LOGIN_URL"));
        SeleniumCommands.clearAndtype("//*[@id=\"username\"]", credMap.get("SALESFORCE_LOGIN_ID"));
    	SeleniumCommands.clearAndtype("//*[@id=\"password\"]", credMap.get("SALESFORCE_LOGIN_PASSWORD"));
    	SeleniumCommands.click("//*[@id=\"Login\"]");
    }

	public static List<SeleniumStep> readCSV(String fileName) throws Exception
	{
		System.out.println("----------- Reading file: "+fileName+" -----------");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		Scanner scanner = null;
		int index = 0;
		List<SeleniumStep> stepList = new ArrayList<>();
		line = reader.readLine() ; // Call to skip the header line
		while ((line = reader.readLine()) != null) {
			SeleniumStep step = new SeleniumStep();
			scanner = new Scanner(line);
			scanner.useDelimiter(",");
			while (scanner.hasNext()) {
				String data = scanner.next();
				if (index == 0)
					step.setCommand(data);
				else if (index == 1)
					step.setTarget(data.replace("\"\"", "\""));
				else if (index == 2)
					step.setValue(data);
				else if (index == 3)
					step.setComment(data);
				else if (index == 4)
					step.setStepNumber(data);
				else
					throw new Exception("Invalid Data Supplied");
				index++;
			}
			index = 0;
			stepList.add(step);
		}
		reader.close();
		System.out.println("----------- Reading file Complete -----------");
		return stepList;
	}
	
	
	
    
    
}