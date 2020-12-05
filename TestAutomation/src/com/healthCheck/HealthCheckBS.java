package com.healthCheck;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import sun.misc.BASE64Decoder;

public class HealthCheckBS {

	public static void main(String[] args) throws InterruptedException, IOException {
		Properties prop=new Properties();
	    FileInputStream ip= new FileInputStream("D:/HealthCheckBS/healthCheck.properties");
		prop.load(ip);
		System.setProperty("webdriver.chrome.driver","D:/TaleoJobValidation/GLOW_Taleo/Jars/chromedriver.exe");
		System.out.println("Initializing chrome");
		Thread.sleep(5000);
	    ChromeOptions options = new ChromeOptions();
		// set chrome as Headless
		options.setHeadless(true);
		options.setBinary("D:/Chrome/Chrome/Application/chrome.exe");
		System.out.println("chrome");
		//Instantiate Chrome Driver
        WebDriver driver = new ChromeDriver(options);
        driver.navigate().to(prop.getProperty("url"));
		driver.manage().window().maximize();
		
		driver.findElement(By.id("username")).sendKeys(prop.getProperty("username"));
		System.out.println("username");
		String password =decode(prop.getProperty("password"));
	    driver.findElement(By.id("password")).sendKeys(password);
	    driver.findElement(By.id("submit-button")).click();
	    
		System.out.println("Logged in to Backstage site");
		
		driver.findElement(By.cssSelector("[title^='Tools']")).click();
		driver.findElement(By.cssSelector("[title^='Operations']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@id='globalnav-start-tools-content-collection']/coral-masonry-item[6]/coral-card/div")).click();
		
		Thread.sleep(2000);
		List<WebElement> Lst =  driver.findElements(By.className("truncatedText"));
		System.out.println("lst" +Lst);
		System.out.println("lst" +Lst.size());
		ArrayList<String> arrayLst = new ArrayList<String>();
		String successMsg =null;
		String warnMsg = null;
		String criticalMsg = null;
		ArrayList<String> msgLst = new ArrayList<String>();
		try{
		for(int rs =0;rs<Lst.size()-1;rs++)
          {
          	    String result1 = Lst.get(rs).getText();
          		Thread.sleep(2000);
          		
          		WebElement classname = driver.findElement(By.xpath
          		("//*[@id='granite-shell-content']/div[2]/div/div/coral-masonry/coral-masonry-item["+(rs+1)+"]/a/coral-card/div/coral-card-content/coral-card-title/table/tbody/tr/td[1]/div"));
          		String str = classname.getAttribute("class");
          		if((result1.equalsIgnoreCase("Request performance"))||(result1.equalsIgnoreCase("Query Performance"))||(result1.equalsIgnoreCase("Disk space"))||(result1.equalsIgnoreCase("Query traversal limits"))
              			||(result1.equalsIgnoreCase("Large Lucene indexes")))
              	{
          		if(str.contains("successBackground"))
          		{
          			System.out.println(result1 + " - "   +"OK");
          			successMsg= result1 + " - "   +"OK";
          			msgLst.add(successMsg);
          			
          		}
          		else if(str.contains("smallIcon warnBackground"))
          		{
          			System.out.println(result1 + " - "   +"WARN");
          			warnMsg= result1 + " - "   +"WARN";
          			msgLst.add(warnMsg);
          			
          		}
          		else if(str.contains("smallIcon errorBackground"))
          		{
          			System.out.println(result1 + " - "   +"CRITICAL");
          			criticalMsg= result1 + " - "   +"CRITICAL";
          			msgLst.add(criticalMsg);
          		}
          		else
          		{
          			System.out.println(result1 + " - "   +"Exception in Class Name");
          		}
          	
          
              	}
          }
		
		
		sendEmail(msgLst);
		Thread.sleep(2000);
		//kill chromedriver
		Runtime.getRuntime().exec("taskkill /im chromedriver.exe /f");
		//kill chromedriver
		Runtime.getRuntime().exec("taskkill /im chrome.exe /f");
		}
		catch (Exception e)
		{
			System.out.println("Exception Occurred" +e);
		}
	}

	public static void sendEmail(ArrayList<String> msgLst) throws IOException, MessagingException
	{
		Properties prop=new Properties();
	    FileInputStream ip= new FileInputStream("D:/HealthCheckBS/healthCheck.properties");
		prop.load(ip);
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.hm.com");
        properties.put("mail.smtp.port", 25);
        String mailFrom = "NoReplyAEMBackstage@hm.com";
        // message info
        
        String mailTo = prop.getProperty("mailto");
        String[] recipientList = mailTo.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }
        SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd");
 	    String date = df.format(new Date());
        String subject = "Backstage HealthCheck Status_" + date;
        String message = "<p> <font size= '3'> Dear Team,</font></p>";
        message+= "<p> <font size= '3'> Please find the backstage health check details.</font></p>";
        message+= "<br><br>";
        
        if(msgLst!=null)
        {
        	
        	ArrayList<String> arrLstPar = new ArrayList<String>();
        	ArrayList<String> arrLstStatus = new ArrayList<String>();
        	int[] snoLst = {1,2,3,4,5};
        	String rowcolor = null;
        	for(int i=0;i<msgLst.size();i++)
        	{
        		String[] arrOfStr = msgLst.get(i).split("-", 2);
        		String str = Arrays.toString(arrOfStr);
        		
        		
        			String s1 = arrOfStr[0];
        			String s2 = arrOfStr[1];
        			
        			arrLstPar.add(s1);
        			arrLstStatus.add(s2);
        		
        	}
        	System.out.println("^^^" +arrLstPar);
    		System.out.println("^^^" +arrLstStatus);
    		rowcolor = (" <tr bgcolor=\"#D3D3D3\">");
        	message+= "<html>" +
         	       "<body>" +
         	       "<table border ='1'>" +
         	      rowcolor +
         	       "<th>S.no</th>" +
         	       "<th>Parameter</th>" +
         	       "<th>Status</th>" +
         	       "</tr>" ;
        	
        	for(int l=0;l<arrLstPar.size();l++)
        	{
        	message+= "<tr>" +
        	          "<td>"+
        			   snoLst[l] +
        			   "</td>" +
        	          "<td>"+
        	          arrLstPar.get(l) +
                      "</td>";
        	          
                      if(arrLstStatus.get(l).equalsIgnoreCase(" OK"))
                      {
                      
                      message+= "<td bgcolor =\"#228B22\">" +
        	          "<b> " +arrLstStatus.get(l) + "</b>" +
        	          "</td>";
                      }
                      if(arrLstStatus.get(l).equalsIgnoreCase(" WARN"))
                      {
                    	  
                      message+= "<td bgcolor =\"#FFA500\">" +
                      "<b> " +arrLstStatus.get(l) + "</b>" +
        	          "</td>";
                      }
                      if(arrLstStatus.get(l).equalsIgnoreCase(" CRITICAL"))
                      {
                      
                      message+= "<td bgcolor =\"#FF4500\">" +
                      "<b> " +arrLstStatus.get(l) + "</b>" +
        	          "</td>";
                      }
        	          
        	         message+= "</tr>";
        	}
        	message+= "</table>" +
        	          "</body>" +
        			  "</html>";
        	     
        }
        message+="<br>";
        message+="<br>";
        message+="<p>This is auto generated mail please DO NOT REPLY</p>";
        Session session = Session.getInstance(properties);
        
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(mailFrom));
        /*InternetAddress[] toAddresses = { new InternetAddress(mailTo) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);*/
        msg.setRecipients(Message.RecipientType.TO, recipientAddress);
        msg.setSubject(subject);
        
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
 
        // sends the e-mail
        Transport.send(msg);
        System.out.println("Message sent successfuly");
	}
	
	 private static String decode(String str) {
		    BASE64Decoder decoder = new BASE64Decoder();
		    try {
		        str = new String(decoder.decodeBuffer(str));
		    } catch (IOException e) {
		        e.printStackTrace();
		    }       
		    return str;
		}
	 

}
