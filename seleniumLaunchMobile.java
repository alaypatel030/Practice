import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class seleniumLaunchMobile {

	public static void main(String[] args) {
		WebDriver webDriver = null;

		try {
		
			Map<String, String> mobileEmulation = new HashMap<>();
			mobileEmulation.put("deviceName", "iPhone X");

			ChromeOptions options = new ChromeOptions();
			System.setProperty("webdriver.chrome.driver",
					"C:\\Users\\alayp\\Downloads\\chromedriver_win32 (2)\\chromedriver.exe");
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("profile.default_content_settings.popups", 0);
			options.setExperimentalOption("prefs", prefs);
			options.addArguments("start-maximized");
			options.addArguments("disable-popup-blocking");
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			options.setExperimentalOption("mobileEmulation", mobileEmulation);
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			webDriver = new ChromeDriver(options);
			Thread.sleep(2500);

			webDriver.get("https://www.bcbst.com");
			Thread.sleep(2500);
			webDriver.findElement(By.xpath("//*[@id=\"bcbst-header\"]/div[1]/div[1]/div/a")).click();
			Thread.sleep(2500);
			webDriver.findElement(By.xpath("//*[@id=\"top-our-plans\"]")).click();
			Thread.sleep(2500);
			webDriver.findElement(By.xpath("//*[@id=\"bcbst-header\"]/div[2]/ul[1]/li[1]/ul/li[6]/a")).click();
			Thread.sleep(2500);
			webDriver.quit();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
