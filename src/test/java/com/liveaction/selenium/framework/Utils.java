package com.liveaction.selenium.framework;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;


public class Utils {
	
	public static Process serverProcess;
	public static Process nodeProcess;
	
    public static String downloadFilepath = System.getProperty("user.dir") + "/src/test/resources/download";

	public static String generateRandomNumber(int numberOfDigits) {
		String number = "";
		for (int i = 0; i < numberOfDigits; i++) {
			number += (int) Math.floor((Math.random() * 10));
		}
		return number;
	}

	public static void hardWaitMilliSeconds(int milliSeconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hardWaitSeconds(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hardWaitMinutes(int minutes) {
		try {
			TimeUnit.MINUTES.sleep(minutes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String createDateForFileName() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH-mm-ss");
		return dateFormat.format(date);
	}
	
	public static String getCurrentTime() {
		   DateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY HH");
		   //get current date time with Date()
		  // Date date = new Date();
		  
		   //get current date time with Calendar()
		   Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		   return dateFormat.format(cal.getTime());
		  // System.out.println(dateFormat.format(cal.getTime()));

	  }
	
	public static String getBackTime(int i){
		DateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY HH");
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(currentDate);
		cal.add(Calendar.HOUR, -i);
		String oneHourBackTime = dateFormat.format(cal.getTime());
		return oneHourBackTime;
	}
	/**
	 * Get last modified file from the given directory.
	 * @return File: File
	 */
	public static File getLatestFile() {
		File fl = new File(downloadFilepath);
		File[] files = fl.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		return choice;
	}
	
	public static void clearDownloadDirectory() {
		File fl = new File(downloadFilepath);
		try {
			FileUtils.cleanDirectory(fl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	

}
