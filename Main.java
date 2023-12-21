import java.io.File;
import java.io.FileNotFoundException;
import org.openqa.selenium.ElementClickInterceptedException;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.*;


import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Main {
	static Account[] accounts;
	static Account currentAccount;

	@SuppressWarnings("deprecation")
	public static void doBingSearch(List<String> searches,int searchCount,boolean mobile,String email,String password) throws InterruptedException {
		Random rand=new Random();
		for (int i=0;i<searchCount;i++) {
			boolean complete=false;
			while (complete==false) {
				try {
					driver.get("https://www.bing.com");
					Thread.sleep(500);
					try {  
						driver.findElement(By.id("bnp_btn_accept")).click();
					} catch (Exception e) {}
					WebElement elem=findElement("//textarea",50);
					System.out.println(searches.size());
					int index=rand.nextInt(searches.size());
				
					elem.clear();
					elem.sendKeys(searches.get(index));
					searches.remove(index);
					elem.sendKeys(Keys.RETURN);
					try {
						    
						driver.findElement(By.id("bnp_btn_accept")).click();
						Thread.sleep(2000);
					} catch (Exception e) {}
					
					try {  
						driver.findElement(By.id("B3C116_1_btn")).click();
						Thread.sleep(2000);
					} catch (Exception e) {}
					
					Thread.sleep(2400);
					if (mobile==false) {
						currentAccount.totalScore=Integer.parseInt(findElement("//*[contains(@id,'id_rc')]",20).getText());
						currentAccount.desktopSearches++;
					}
					else {
						currentAccount.mobileSearches++;
					}
					complete=true;
				}
				catch (Exception e) {
					//e.printStackTrace(console);
					e.printStackTrace();
					System.out.println("bing search, rebooting for "+email);
					login(email,password,mobile);
					
				}
			} 
		}
		driver.quit();
	}
	public static void login(String email, String password,boolean mobile) {
		boolean complete=false;
		while (complete==false) {
			try {
				try {
					driver.quit();
				} catch (Exception e) {}
				ChromeOptions options= new ChromeOptions();
				options.addArguments("--remote-allow-origins=*");
				if (mobile==true) {
					options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/111.0.5563.101 Mobile/15E148 Safari/604.1");
				}
				driver=new ChromeDriver(options);
				driver.manage().window().setPosition(new Point(-1000,-1000));
				driver.get("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&id=264960&wreply=https%3a%2f%2fwww.bing.com%2fsecure%2fPassport.aspx%3fedge_suppress_profile_switch%3d1%26requrl%3dhttps%253a%252f%252fwww.bing.com%252f%253ftoWww%253d1%2526redig%253d14E1DCBC328044D29D1DB806216207EF%2526wlexpsignin%253d1%26sig%3d3239A1828463674338ECB35D85536662&wp=MBI_SSL&lc=2057&pcexp=false&CSRFToken=25fad841-1cf6-4cec-ae51-1b24fb0be3ad&aadredir=1");
				findElement("//input[contains(@id,'i0116')]",20).sendKeys(email);
				findElement("//*[contains(@id,'idSIButton9')]",20).click();
				Thread.sleep(1000);
				findElement("//input[contains(@id,'i0118')]",20).sendKeys(password);
				findElement("//*[contains(@id,'idSIButton9')]",20).click();
				Thread.sleep(1000);
				findElement("//*[contains(@id,'bnp_btn_accept')]",20).click();
				complete=true;
			} catch (Exception e) {//e.printStackTrace(console);
			e.printStackTrace();}
		}
	}
	public static void doBingSearches(String email,String password,ArrayList<String> searches) throws InterruptedException {
		
		
		login(email,password,true);
		doBingSearch(searches,40,true,email,password);
		login(email,password,false);
		doBingSearch(searches,40,false,email,password);
		driver.quit();
	}
	public static List<String> getGoogleSearches() {
		boolean complete=false;
		WebDriver newDriver = null;
		List<String> searchTerms=null;
		while (complete==false) {
			try {
					searchTerms=null;
					ChromeOptions options=new ChromeOptions();
					options.addArguments("--remote-allow-origins=*");
					options.addArguments("--headless");
					newDriver=new ChromeDriver(options);		
					newDriver.get("https://trends.google.com/trends/trendingsearches/daily?geo=GB&hl=en-US");
					Thread.sleep(500);
					newDriver.findElement(By.className("cookieBarConsentButton")).click();
					for (int i=0;i<5;i++) {
						newDriver.findElement(By.xpath("//*[contains(@class,'feed-load-more-button')]")).click();
						Thread.sleep(500);
					}
					
					searchTerms = newDriver.findElements(By.xpath("//span[contains(@ng-repeat,'titlePart in titleArray')]")).stream()
							.map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
					newDriver.close();
					complete=true;
					System.out.println("search term count: "+searchTerms.size());
			}
			catch (Exception e) {
				//e.printStackTrace(console); 
				e.printStackTrace();
				newDriver.quit();
			}
		}
			
		return searchTerms;
	}
	public static void clickStaleElement(WebElement elem,int attempts,int wait) throws InterruptedException {
		for (int i=0;i<attempts;i++) {
			try {
				elem.click();
			}
			catch (StaleElementReferenceException e) {
				Thread.sleep(wait);
				System.out.println(i);
			}
		}
		
	}
	public static WebElement findElement(String xpath,int times) throws InterruptedException {
		return findElement(xpath,times,null);
	}
	public static WebElement findElement(String xpath,int times,WebDriver methodDriver) throws InterruptedException {
		if (methodDriver==null) {
			methodDriver=driver;
		}
		int count=0;
		boolean found=false;
		WebElement elem = null;
		while (count<times && found==false) {
			try {
				elem=methodDriver.findElement(By.xpath(xpath));
				found=true;
				//System.out.println("successfully acquired element");
			}
			catch (StaleElementReferenceException | ElementNotInteractableException | NoSuchElementException e) {
				count++;
				System.out.println(count);
				Thread.sleep(3000);
			}
		}
		return elem;	
	}

	public static List<WebElement> findElements(String xpath,int times) throws InterruptedException {
		int count=0;
		boolean found=false;
		List<WebElement> elem = null;
		while (count<times && found==false) {
			try {
				elem=driver.findElements(By.xpath(xpath));
				found=true;
				//System.out.println("successfully acquired element");
			}
			catch (StaleElementReferenceException | ElementNotInteractableException e) {
				count++;
				System.out.println(count);
				Thread.sleep(3000);
			}
			if (elem.size()==0) {
				found=false;
				count++;
				System.out.println(count);
				Thread.sleep(3000);
			}
		}
		return elem;	
	}	
	
	public static Account[] parseJSONAccounts(String path_to_json) {
		Gson gson=new Gson();
		String path=path_to_json;
		FileReader reader;
		try {
			reader = new FileReader(path);
			 accounts=gson.fromJson(reader, Account[].class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(console);
			e.printStackTrace();
		}
		return accounts;
				

	}
	public static void doGuessingGame(card item) throws InterruptedException {
        boolean complete=false;
        while (complete==false) {
        	Thread.sleep(2000);
        	String question=findElement("//div[contains(@class,'bt_Quefooter')]",9).getText();
        	System.out.println("q: "+question);
        	String[] parts = question.split(" of ");
        	int questionNo = Integer.parseInt(parts[0]), questionTotal = Integer.parseInt(parts[1]);
        	if (questionNo==questionTotal) {
        		complete=true;
        	}
        	findElement("//div[contains(@class,'btOptionText')]",20).click();
        	Thread.sleep(5000);	
        	
        }
        currentAccount.completedTasks.add(item.title);
        
        	
	}
	public static ArrayList<String> getElementIDs (List<WebElement> elems) {
		ArrayList<String> ids=new ArrayList<String>();
		for (WebElement elem:elems) {
			ids.add(elem.getAttribute("id"));
		}
		return ids;
	}
	
	public static int doQuiz(card item,boolean is50PointQuiz) throws InterruptedException {
			Thread.sleep(1000);
	        try {
	            driver.findElement(By.id("rqStartQuiz")).click();
	        } catch (Exception e) {}

	        List<WebElement> elems = driver.findElements(By.xpath("//div[@iscorrectoption='True']"));
	        System.out.println(elems.size());
	        if (elems.size()==0 && is50PointQuiz==true) {
	        	return 0;
	        }
	        String[] q=findElement("//span[contains(@class,'rqPoints')]",20).getText().split("/");
	        System.out.println(q[0]+" "+q[1]);
            int cycles=(Integer.parseInt(q[1])-Integer.parseInt(q[0]))/10;
            System.out.println(cycles);
	        if (!elems.isEmpty()) {
	        	for (int i=0;i<cycles; i++) {
	        		Thread.sleep(4000);
	         		elems = findElements("//div[@iscorrectoption='True']",50);
	        		for (int j=0;j<elems.size();j++) {
	        			elems = findElements("//div[@iscorrectoption='True']",50);
	        			String id=elems.get(j).getAttribute("id");
	        			findElement("//div[contains(@id,'"+id+"')]", 20).click();
	        			Thread.sleep(500);
	        			}
	        		}
	        	}
	        else {
	        	for (int i=0;i<cycles;i++) {
	        		Thread.sleep(4000);
	        		
	        		elems=findElements("//input[contains(@class,'rqOption')]",20);
	        		
		        	for (int j=0;j<elems.size();j++) {
		        		elems=findElements("//input[contains(@class,'rqOption')]",20);
		        		
		        		String id=elems.get(j).getAttribute("id");
		        		
		        		String points=findElement("//span[contains(@class,'rqPoints')]",20).getText();
		        		
		        		findElement("//input[contains(@id,'"+id+"')]", 20).click();
		        		
		        		Thread.sleep(500);
		        		String newPoints=findElement("//span[contains(@class,'rqPoints')]",20).getText();
		        		
		        		if (!points.equals(newPoints)) {
		        			break;
		        		}
		        	}
	        	}
	        	
	        }
	        Thread.sleep(2000);
	        currentAccount.completedTasks.add(item.title);
	        return 1;
	    }

						
					
					
	
	public static void doDailyPoll (card item) throws InterruptedException {
			Thread.sleep(500);
		    findElement("//*[contains(@id,'btoption0')]",20).click();
		    currentAccount.completedTasks.add(item.title);
		    
			Thread.sleep(2000);
		}
	
	public static WebDriver driver=null;
	public static List<card> getExtraTasks() {
		WebElement cards=driver.findElement(By.id("more-activities"));
		List<WebElement> elems = cards.findElements(By.tagName("mee-card"));
		List<card> items=new ArrayList<card>();
		for (WebElement elem:elems) {
			String elementText=elem.findElement(By.tagName("h3")).getText(); //name of card eg daily poll
			System.out.println(elementText);
			try {
				WebElement clickable=elem.findElement(By.xpath(".//span[@ng-if='$ctrl.pointsString']"));
				String getScoreAsString=clickable.getText();
				int score;
				if (getScoreAsString.contains("/")) { 
					score=Integer.parseInt(getScoreAsString.split("/")[1]);
					}
				else {
					score=Integer.parseInt(getScoreAsString);
				}
				boolean alreadyCompleted=checkComplete(clickable);
				if (alreadyCompleted==false) {
					items.add(new card(clickable,score,elementText));
					}
				}
			catch(NoSuchElementException e) {}
			}
		return items;
		}
		
		
	public static void secondLogin() {
			try {
				driver.findElement(By.xpath("//a[contains(text(),'Sign in')]")).click();
				Thread.sleep(2500);
			}
			catch (Exception e){}
			try {
				driver.findElement(By.id("id_a")).click();
				Thread.sleep(2000);
				driver.navigate().refresh();
				Thread.sleep(2500);
			} catch (Exception e) {}
	}
	public static void doTask(card item) throws InterruptedException {
		
		if (item.score>5) {
			try {
				driver.findElement(By.xpath("//button[contains(text(),'Reject')]")).click();
			} catch (Exception e) {}
			boolean complete=false;
			while (complete==false) {
				try {
					item.elem.click();
					complete=true;
				}
				catch (ElementClickInterceptedException e) {
					driver.navigate().refresh();
					Thread.sleep(1000);
					}
			}
				
			
			ArrayList<String> handles=new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(handles.get(1));
			Thread.sleep(2000);
			secondLogin();
			try {
				driver.findElement(By.id("bnp_btn_accept")).click();
			}
			catch (Exception e) {}
			if (item.score==10 && item.title.toLowerCase().equals("daily poll")) {
				doDailyPoll(item);
			}
			if (item.score==30) {
				//try {
					doQuiz(item,false);
					//}	catch (Exception e) {e.printStackTrace()}
				}
			
			if (item.score==50) {
				System.out.println("doing the thing");
				try {
		            driver.findElement(By.id("rqStartQuiz")).click();
		        } catch (Exception e) {}
				Thread.sleep(2000);
				if (doQuiz(item,true)==0) {
					doGuessingGame(item);
				}
			}
			
				driver.close();
				driver.switchTo().window(handles.get(0));
				Thread.sleep(2500);
				
		}
	}
	
	public static boolean checkComplete(WebElement item) {
		//check if item has tick img next to score
		By nextElem=RelativeLocator.with(By.tagName("span")).toLeftOf(item);
		if (driver.findElement(nextElem).getAttribute("class").equals("mee-icon mee-icon-SkypeCircleCheck")) {
			return true;
		}
		return false;
	}
		

	public static void MainProgram(String email, String password,ArrayList<String> searches) throws InterruptedException {
		
		
		boolean tasks_complete=false;
		while (tasks_complete==false) {
			try {
				ChromeOptions options=new ChromeOptions();
				//options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36");
				options.addArguments("--remote-allow-origins=*");
				options.addArguments("disable-infobars");
				//options.addArguments("--headless");
				driver=new ChromeDriver(options);
				driver.manage().window().setPosition(new Point(-1000,-1000));
				WebElement cards=null;
				driver.get("https://login.live.com/oauth20_authorize.srf?client_id=9c941f7c-a811-4e9c-8e66-29fdec50490f&scope=openid+profile+offline_access&redirect_uri=https%3a%2f%2frewards.bing.com%2fsignin-oidc&response_type=code&state=CfDJ8LbKok95JvtCpPQvgHivJ2piCCD_5f3NoAV0CxfRXZQ_mP4FiDPGPsB_pYj8_t3MMqIWVGvjNSwkUeu3iXLED9Wszm0RTr9f2N9oPm0TKyJNoEjikkzepq89VOIUkDxQGxHXj3Q2vsLlRvYIVRDOxoOCE7MjpUS5Yz6FF2Lbu_a2fMGaEIPaSHrzOmw-z7Y7WzW1dYWi-bswA09_0aeN2ImWhIi3GxcQcXeoxq7K7NlYyhtF9zlR8pv6aHXzxDvaQ-MfeXxLCnL2d8lthY0nuFIlwMLlc0lBjhIVZgnS6baK9zPG7d4NjDbkB6oVyWzwMpvudPD-CdVRAxnTPTDSGRrnrBT1sU_vkpKzOWa7J6YX4HdrEeuYixDFkCGV6nflPBb_HKFbNnC0jmhzagWnZTg&response_mode=form_post&nonce=638144115004802604.NjNiMDBiNjktZmYxZC00YjZkLTkxNWUtYmRjYWFlNmQ0ZmRlNWM0ZmQ1OWYtYzg4OS00YzQ3LWE5ZWEtYmZjZDc0ZWQzZmI2&code_challenge=QmIjHn10p2UqLucCXUaoPO1I97c80b3WSJ1rqfxOYRE&code_challenge_method=S256&x-client-SKU=ID_NET6_0&x-client-Ver=6.23.1.0&uaid=cd34874cc8704d6fb8207aa9363fe164&msproxy=1&issuer=mso&tenant=consumers&ui_locales=en-GB&client_info=1&epct=AQABAAAAAAD--DLA3VO7QrddgJg7Wevr5zFjXDlzbUvCNgLgJSzij0T6viV4dh_E_ogISkxMJd8OxUepe34HWsc3DvZ9o98x_hsrCKpAg4OjrDQPrW9uOmIqvilI5FPvMBmzLTZN_vj1JE1QYA0QE6-UDnhycxPDurZVEsk7gnccHPe3uJtXjA9FOH7rss_6bHCEtFvICVGwFlrtUUYHtKIRq1OCeKMBGgvwfVIMU-P7ddv9jagCsSAA&jshs=0&fl=easi2&cobrandid=03c8bbb5-2dff-4721-8261-a4ccff24c81a&lw=1#");
				findElement("//*[contains(@id,'i0116')]",20).sendKeys(email);
				findElement("//*[contains(@id, 'idSIButton9')]",20).click();
				Thread.sleep(1000);
				findElement("//*[contains(@name,'passwd')]",20).sendKeys(password);
				findElement("//*[contains(@id, 'idSIButton9')]",20).click();
				Thread.sleep(500);
				findElement("//*[contains(@id, 'idSIButton9')]",20).click();
				Thread.sleep(500);
				WebElement wait = new WebDriverWait(driver,Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(By.tagName("mee-card-group")));
				cards=driver.findElement(By.tagName("mee-card-group"));
																																			List<WebElement> elems = cards.findElements(By.tagName("mee-card"));
				List<card> items=new ArrayList<card>();
				for (WebElement elem:elems) {
					String elementText=elem.findElement(By.tagName("h3")).getText(); //name of card eg daily poll
					System.out.println(elementText);
					try {
						WebElement clickable=elem.findElement(By.xpath(".//span[@ng-if='$ctrl.pointsString']")); 
					String getScoreAsString=clickable.getText();
					// check if card has tick next to it, if so do not do the task
					boolean alreadyCompleted=checkComplete(clickable); 
					int score;
					if (getScoreAsString.contains("/")) { 
						score=Integer.parseInt(getScoreAsString.split("/")[1]);
						}
					else {
						score=Integer.parseInt(getScoreAsString);
					}
					if (alreadyCompleted==false) {
						items.add(new card(clickable,score,elementText)); 
						}
					} 
					catch(NoSuchElementException e) {}
				}
					
				for(card item:getExtraTasks()) {
					items.add(item);
				}
				items.sort(Comparator.comparing(card::getScore));
				for (card item:items) {
					System.out.println(item.score);
				}
				for (card item:items) {
						//try {
						doTask(item);
						//} 
						//catch (Exception e) {e.printStackTrace()}
				}
				tasks_complete=true;
			}
			 catch (Exception e) {
				//e.printStackTrace(console);
				e.printStackTrace();
				System.out.println("doTasks: rebooting for "+email);
				driver.manage().deleteAllCookies();
				driver.quit();
				Thread.sleep(2000);
				}
		}
	
		
		doBingSearches(email,password,searches);
		driver.quit();	
	}
			
	static PrintStream console;
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		LocalTime starttime=LocalTime.now();

		//File textDoc=new File("Output.txt");
		//console=new PrintStream(textDoc);
		//System.setOut(console);
		System.out.println("gk");
		List<String> searchGoogle = getGoogleSearches();
		String path=System.getProperty("user.dir")+"\\accounts.json";
		Account[] accountData = parseJSONAccounts(path);
		for (Account account:accountData) {
			currentAccount=account;
			ArrayList<String> passSearchGoogle=new ArrayList<>(List.copyOf(searchGoogle));
			MainProgram(account.email,account.password,passSearchGoogle);
			System.out.println("Account email: "+currentAccount.email+"\nCompleted Tasks: "+currentAccount.completedTasks.toString()+"\nDesktop Searches: "+currentAccount.desktopSearches+"\nMobile Searches: "+currentAccount.mobileSearches+"\n Total score: "+currentAccount.totalScore);
		}
		LocalTime endtime=LocalTime.now();	
		System.out.println("Program completed in "+(endtime.getHour()-starttime.getHour())+" hours, "+(endtime.getMinute()-starttime.getMinute())+" minutes, "+(endtime.getSecond()-starttime.getSecond())+" seconds.");

		console.close();
		
		
		
	}
		
		

}


