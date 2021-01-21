package supportlibraries;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public abstract class R extends ReusableLibrary {
	/**
	 * Constructor to initialize the business component library
	 * 
	 * @param scriptHelper The {@link ScriptHelper} object passed from the
	 *                     {@link DriverScript}
	 */
	public R(ScriptHelper scriptHelper) {
		super(scriptHelper);
	}

	private static int CatchCount = 0;
	private static boolean status = false;
	// static

	public static final int TEXTVERIFY_EXACT = 1;
	public static final int TEXTVERIFY_CONTAINS = 2;
	public static final int TEXTVERIFY_CLIPBOARD_EXACT = 3;
	public static final int TEXTVERIFY_CLIPBOARD_CONTAINS = 4;

	public static final int TODAY = 11;
	public static final int YESTERDAY = 12;
	public static final int TOMORROW = 13;
	public static final int WEEK_AGO = 14;
	public static final int WEEK_AFTER = 15;
	public static final int FIRST_DATE_OF_YEAR = 16;
	public static final int FIRST_DATE_OF_MONTH = 17;
	public static final int LAST_DATE_OF_YEAR = 18;
	public static final int TODAY_YEAR_AGO = 19;
	public static final int TODAY_YEAR_AFTER = 20;

	public static final int ELEMENT_HIGHLIGHT_GREEN = 21;
	public static final int ELEMENT_HIGHLIGHT_RED = 22;
	public static final int ELEMENT_BLACKOUT = 23;
	public static final int ELEMENT_HIDE = 24;
	public static final int HIGHLIGHT_DEFAULT = 25;

	public static JavascriptExecutor js = (JavascriptExecutor) driver;

	/**
	 * 
	 * @param e
	 * @param functionName
	 */

	public static void Catch(Exception e, String functionName) {
		System.out.println("\n( " + ++CatchCount + " ) Unexpected Error at {" + functionName + "}.");
		System.err.println(e.getMessage());
		System.out.print(
				"\n_____________________________________________________________________________________________________________________________\n\n\n");
	}

	/**
	 * 
	 * @param by
	 * @param timeOutInSeconds
	 */

	public static void waitUntilElementLocated(By by, long timeOutInSeconds) {
		try {
			status = true;
			(new WebDriverWait(driver, timeOutInSeconds)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */
	public static void Wait(By Xpath) {
		R.waitUntilElementLocated(Xpath, 30);
	}

	/**
	 * 
	 * @param Xpath
	 */
	public static void Wait(String Xpath) {
		R.waitUntilElementLocated(By.xpath(Xpath), 30);
	}

	/**
	 * 
	 * @param element
	 */

	public static void viewElementOnScreen(WebElement element) {
		try {
			Coordinates coordinate = ((Locatable) element).getCoordinates();
			coordinate.onPage();
			coordinate.inViewPort();
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */

	public static void hoverOver(String Xpath) {
		try {
			Wait(Xpath);
			WebElement element = driver.findElement(By.xpath(Xpath));
			viewElementOnScreen(element);

			if (status) {
				Actions act = new Actions(driver);
				act.moveToElement(element).perform();
				Thread.sleep(500);
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}
	
	/**
	 * 
	 * @param HighlightType
	 * @param Xpath
	 */

	public static void highlight(int HighlightType, String Xpath) {
		try {
			Wait(Xpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(Xpath));
				viewElementOnScreen(element);

				String script = null;
				switch (HighlightType) {

				case ELEMENT_HIGHLIGHT_GREEN:
					script = "arguments[0].setAttribute('style', 'background: yellow'; 'border: 1px solid green;');";
					break;
				case ELEMENT_HIGHLIGHT_RED:
					script = "arguments[0].setAttribute('style','border: 3px solid red;');";
					break;
				case ELEMENT_BLACKOUT:
					script = "arguments[0].setAttribute('style', 'background: black'; 'color: black;');";
					break;
				case ELEMENT_HIDE:
					script = "arguments[0].setAttribute('style', 'visibility: hidden;');";
					break;

				default:
					script = "arguments[0].setAttribute('style','border: 0px solid white;');";
					break;
				}

				js.executeAsyncScript(script, element);
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 * @param text
	 * @return
	 */

	public static boolean textVerifyTF(String Xpath, String text) {
		boolean retVal = false;
		try {
			Wait(Xpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(Xpath));
				viewElementOnScreen(element);
				String Obtainedtxt = element.getText().trim();
				if (!Obtainedtxt.equalsIgnoreCase(text)) {
					highlight(ELEMENT_HIGHLIGHT_RED, Xpath);
					System.out.println("Expected: - " + text + "\nFailure : - " + Obtainedtxt);
					highlight(HIGHLIGHT_DEFAULT, Xpath);
				} else {
					highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
					retVal = true;
					highlight(HIGHLIGHT_DEFAULT, Xpath);
				}
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
		return retVal;
	}

	/**
	 * 
	 * @param src
	 */
	public static void SwitchFrame(String src) {
		try {
			driver.switchTo().frame(driver.findElement(By.xpath("//iframe[contains(@src,'" + src + "')]")));
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @return
	 */

	public static String switchToNewWindow() {
		String parentHandler = null;
		try {
			parentHandler = driver.getWindowHandle();
			@SuppressWarnings("unused")
			WebDriverWait wait = new WebDriverWait(driver, 20);

			// boolean isChildWindowOpen =
			// wait.until(ExpectedConditions.numberOfWindowsToBe(2));
			// if (isChildWindowOpen) {
			Set<String> handles = driver.getWindowHandles();

			for (String handle : handles) {
				driver.switchTo().window(handle);
				if (!parentHandler.equals(handle)) {
					break;
				}
			}
			driver.manage().window().maximize();
			// }

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}

		return parentHandler;
	}

	/**
	 * 
	 * @param Xpath
	 * @param ExpectedAttribute
	 * @return
	 */

	public static String jsGetAttribute(String Xpath, String ExpectedAttribute) {
		String finalAttribute = null;
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(newXpath));
				viewElementOnScreen(element);
				JavascriptExecutor js = (JavascriptExecutor) driver;
				String[] html = ((String) js.executeScript("return argument[0].outerHTML;", element)).split("\"");

				for (int i = 0; i < html.length - 1; i++) {
					if (html[i].contains(ExpectedAttribute)) {
						finalAttribute = html[(i - 1)].toString();
						break;
					} else {
						finalAttribute = null;
					}
				}
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}

		if (finalAttribute == null) {
			System.out.println("No attribute named {" + ExpectedAttribute + "} is avilable for {" + Xpath + "}");
		}
		return finalAttribute;
	}

	public static void copyToClipboard(String Text) {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection strSelect = new StringSelection(Text);
			clipboard.setContents(strSelect, null);
			Thread.sleep(500);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */

	public static boolean isDisplayed(String Xpath) {
		boolean displayed = false;
		try {
			waitUntilElementLocated(By.xpath(Xpath), 15);

			if (status) {
				displayed = true;
				WebElement element = driver.findElement(By.xpath(Xpath));
				viewElementOnScreen(element);

			} else {
				displayed = false;
				System.out.println("Element {" + Xpath + "} is not displayed.");
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}

		return displayed;
	}

	/**
	 * 
	 * @param screen
	 * @param parrernName
	 */

	public static boolean sikuliIsDisplayed(Screen screen, String parrernName) {
		boolean displayed = false;
		try {
			screen.exists(parrernName, 1);
			displayed = true;
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
		return displayed;
	}

	/**
	 * 
	 * @param Xpath
	 * @return
	 */

	public static String getText(String Xpath, String arrtibuteName) {
		String text = null;
		try {
			text = driver.findElement(By.xpath(Xpath)).getText().trim();

			if (text.equals("")) {
				text = driver.findElement(By.xpath(Xpath)).getAttribute(arrtibuteName);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
		return text;
	}

	/**
	 * 
	 * @param Xpath
	 * @param keys
	 */

	public static void sendKeys(String Xpath, String keys) {
		try {
			System.out.println(driver);

			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);

			if (status) {
				WebElement element = driver.findElement(By.xpath(newXpath));
				viewElementOnScreen(element);
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
//				copyToClipboard(keys);
//				element.sendKeys(Keys.CONTROL, "v");
				element.sendKeys(keys);
				highlight(HIGHLIGHT_DEFAULT, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 * @param keys
	 * @param iteration
	 */
	public static void sendKeysSeries(String Xpath, String keys, int iteration) {
		try {
			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			for (int i = 1; i <= iteration; i++) {
				String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[1];
				sendKeys(finalXpath, keys);
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 * @param Text
	 */

	public static void sikuliSendKeys(Screen screen, String patternName, String Text) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			sikuliClick(screen, patternName);
			copyToClipboard(Text);
			screen.type("v", Key.CTRL);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 * @param Text
	 */

	public static void sikuliSendKeys(Screen screen, String patternName, String Text, int offsetX, int offsetY) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			Match m = screen.find(p);
			Location finalLocation = new Location(m.getX() + offsetX, m.getY() + offsetY);
			screen.click(finalLocation);
			copyToClipboard(Text);
			screen.type("v", Key.CTRL);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */
	public static void clear(String Xpath) {
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(newXpath));
				viewElementOnScreen(element);
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
				element.clear();
				highlight(HIGHLIGHT_DEFAULT, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 */

	public static void sikuliClear(Screen screen, String patternName) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			screen.click(patternName);
			screen.type("a", Key.CTRL);
			screen.type(Key.BACKSPACE);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */

	public static void click(String Xpath) {
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(newXpath));
				viewElementOnScreen(element);
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
				element.click();
				highlight(HIGHLIGHT_DEFAULT, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 * @param iteration
	 */
	public static void clickSeries(String Xpath, int iteration) {
		try {
			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			if (status) {
				for (int i = 1; i <= iteration; i++) {
					String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[2];
					click(finalXpath);
				}
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param ClickType
	 * @param Xpath
	 */

	public static void jsClick(String ClickType, String Xpath) {
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(newXpath));
				viewElementOnScreen(element);
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("argument[0].click();", element);
				element.click();
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 */

	public static void sikuliClick(Screen screen, String patternName) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			screen.click(patternName);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 * @param offsetX
	 * @param offsetY
	 */

	public static void sikuliClick(Screen screen, String patternName, int offsetX, int offsetY) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			Match m = screen.find(p);
			Location finalLocation = new Location(m.getX() + offsetX, m.getY() + offsetY);
			screen.click(finalLocation);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 */

	public static void sikuliRightClick(Screen screen, String patternName) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			screen.rightClick(patternName);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param screen
	 * @param patternName
	 * @param offsetX
	 * @param offsetY
	 */
	public static void sikuliRightClick(Screen screen, String patternName, int offsetX, int offsetY) {
		try {
			Screen.all();
			Pattern p = new Pattern(patternName);
			screen.wait(p, 60);
			Match m = screen.find(p);
			Location finalLocation = new Location(m.getX() + offsetX, m.getY() + offsetY);
			screen.rightClick(finalLocation);
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */

	public static void copy(String Xpath) {
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(Xpath));
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
				element.sendKeys(Keys.CONTROL, "a");
				element.sendKeys(Keys.CONTROL, "c");
				Thread.sleep(500);
				highlight(HIGHLIGHT_DEFAULT, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 */
	public static void paste(String Xpath) {
		try {
			String newXpath = Xpath.replaceAll("#", "");
			Wait(newXpath);
			if (status) {
				WebElement element = driver.findElement(By.xpath(Xpath));
				highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
				element.sendKeys(Keys.CONTROL, "v");
				highlight(HIGHLIGHT_DEFAULT, Xpath);
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 * @param iteration
	 */

	public static void pasteSeries(String Xpath, int iteration) {
		try {
			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			if (status) {
				for (int i = 1; i <= iteration; i++) {
					String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[2];
					paste(finalXpath);
				}
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}

	}

	/**
	 * 
	 * @param VerificationType use 1 for Exact, 2 for Contains, 3 for Clipboard, 4
	 *                         for Clipboard contains
	 * @param Xpath
	 * @param ExpectedText
	 */

	public static void textVerify(int VerificationType, String Xpath, String ExpectedText) {
		try {
			Wait(Xpath);
			if (status) {
				boolean contains = false;
				WebElement element = driver.findElement(By.xpath(Xpath));
				viewElementOnScreen(element);
				String retrievedText = null;

				switch (VerificationType) {
				// Clipboard Contains
				case TEXTVERIFY_CLIPBOARD_CONTAINS:
					element.click();
					element.click();
					element.sendKeys(Keys.CONTROL, "a");
					element.sendKeys(Keys.CONTROL, "c");
					Thread.sleep(500);
					retrievedText = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
					contains = true;
					break;

				// Clipboard
				case TEXTVERIFY_CLIPBOARD_EXACT:
					element.click();
					element.click();
					element.sendKeys(Keys.CONTROL, "a");
					element.sendKeys(Keys.CONTROL, "c");
					Thread.sleep(500);
					retrievedText = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
					break;

				// contains
				case TEXTVERIFY_CONTAINS:
					contains = true;
					break;

				// exact
				case TEXTVERIFY_EXACT:
					retrievedText = element.getText().trim();
					break;
				}

				if (contains) {
					if (!retrievedText.contains(ExpectedText)) {
						highlight(ELEMENT_HIGHLIGHT_RED, Xpath);
						System.out.println("Expected: - " + ExpectedText + "\nFailure : - " + retrievedText);
						highlight(HIGHLIGHT_DEFAULT, Xpath);
					} else {
						highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
						highlight(HIGHLIGHT_DEFAULT, Xpath);
					}
				} else {
					if (!retrievedText.equalsIgnoreCase(ExpectedText)) {
						highlight(ELEMENT_HIGHLIGHT_RED, Xpath);
						System.out.println("Expected: - " + ExpectedText + "\nFailure : - " + retrievedText);
						highlight(HIGHLIGHT_DEFAULT, Xpath);
					} else {
						highlight(ELEMENT_HIGHLIGHT_GREEN, Xpath);
						highlight(HIGHLIGHT_DEFAULT, Xpath);
					}
				}
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param VerificationType use 1 for Exact, 2 for Contains, 3 for Clipboard, 4
	 *                         for Clipboard contains
	 * @param Xpath
	 * @param ExpectedText
	 * @param iteration        total number of Series.
	 */

	public static void textVerifySeries(int VerificationType, String Xpath, String ExpectedText, int iteration) {
		try {

			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			for (int i = 1; i <= iteration; i++) {
				String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[2];
				textVerify(VerificationType, finalXpath, ExpectedText);
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param VerificationType use 1 for Exact, 2 for Contains, 3 for Clipboard, 4
	 *                         for Clipboard contains
	 * @param Xpath
	 * @param ExpectedText
	 * @param iteration
	 */

	public static void ListVerify(int VerificationType, String Xpath, String[] ExpectedText, int iteration) {
		try {
			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			for (int i = 1; i <= iteration; i++) {
				String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[2];
				textVerify(VerificationType, finalXpath, ExpectedText[(i - 1)]);
			}

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param Xpath
	 * @param iteration
	 */
	public static void printList(String Xpath, int iteration) {
		try {
			System.out.println();
			String forDataSheet = null;
			String XpathAry[] = Xpath.split("#");
			int counter = Integer.valueOf(XpathAry[1]);

			for (int i = 1; i <= iteration; i++) {
				String finalXpath = XpathAry[0] + ((i - 1) + counter) + XpathAry[2];
				Wait(finalXpath);

				if (status) {
					WebElement element = driver.findElement(By.xpath(finalXpath));
					viewElementOnScreen(element);
					String ObtainedText = element.getText().trim();
					System.out.println("(" + i + ") " + ObtainedText);

					if (i == 1) {
						forDataSheet = ObtainedText;
					} else if (i == iteration) {
						forDataSheet = forDataSheet + ";" + ObtainedText + ";";
						System.out.println("----------: For Datasheet :----------\n" + forDataSheet + "\n\n");
					} else {
						forDataSheet = forDataSheet + ";" + ObtainedText + ";";
					}
				}
			}
		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	/**
	 * 
	 * @param DateType
	 * @param Xpath
	 */

	public static void sendTodayDate(int DateType, String Xpath) {
		try {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			String day = null, month = null, year = null;

			switch (DateType) {
			case (TODAY):
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (FIRST_DATE_OF_YEAR):
				day = String.format("%02d", 1);
				month = String.format("%02d", 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (LAST_DATE_OF_YEAR):
				day = String.format("%02d", 12);
				month = String.format("%02d", 12);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (YESTERDAY):
				cal.add(Calendar.DATE, -1);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (TOMORROW):
				cal.add(Calendar.DATE, +1);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (TODAY_YEAR_AGO):
				cal.add(Calendar.DATE, -365);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (TODAY_YEAR_AFTER):
				cal.add(Calendar.DATE, +365);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (WEEK_AGO):
				cal.add(Calendar.DATE, -7);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			case (WEEK_AFTER):
				cal.add(Calendar.DATE, +7);
				day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
				month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
				year = Integer.toString(cal.get(Calendar.YEAR));
				break;
			}
			sendKeys(Xpath, day + "/" + month + "/" + year);

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
	}

	public static void errorPopUp(int timeOut, String ErrorMsg) {

		ErrorMsg = ErrorMsg.replace("|", "\n    ");
		UIManager.put("OptionPane.background", new ColorUIResource(255, 204, 204));
		UIManager.put("Panel.background", new ColorUIResource(255, 204, 204));
		JOptionPane pane = new JOptionPane(ErrorMsg, JOptionPane.INFORMATION_MESSAGE);
		final JDialog dialog = pane.createDialog(null, "Error");
		dialog.setModal(false);
		dialog.setLocation(0, 0);
		dialog.setVisible(true);

//		new Timer(timeOut, new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				dialog.setVisible(false);
//			}
//		}).start();
	}

	/**
	 * Read from file to an ArrayList
	 * 
	 * @param FileLocation
	 */

	public static ArrayList<String> readFromFile(String FileNameWithLocation) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(FileNameWithLocation);
			BufferedReader bufferredReader = new BufferedReader(fileReader);

			String line = null;

			while ((line = bufferredReader.readLine()) != null) {
				lines.add(line);
			}
			bufferredReader.close();

		} catch (Exception e) {
			Catch(e, new Object() {
			}.getClass().getEnclosingMethod().getName().toString());
		}
		return lines;
	}

	/**
	 * 
	 * @param FileNameWithLocation
	 */

	public static void deleteFile(String FileNameWithLocation) {
		try {
			Files.deleteIfExists(Paths.get(FileNameWithLocation));
		} catch (NoSuchFileException e) {
			System.out.println("No Such file/directory exists");
		} catch (DirectoryNotEmptyException e) {
			System.out.println("Directory is not empty");
		} catch (IOException e) {
			System.out.println("Invalid Permission");
		}
	}

}
