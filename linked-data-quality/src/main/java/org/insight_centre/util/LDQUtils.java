package org.insight_centre.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.sparql.util.DateTimeStruct.DateTimeParseException;

public class LDQUtils {

	
	
	/*
	 * get current time of the system in GMT 
	 */
	public static String getCurrentTime() throws ParseException {

		DateFormat dateFormat=null;
		Date date = new Date(System.currentTimeMillis());
	try{	dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		//dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}catch(DateTimeParseException ex){
			ex.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return dateFormat.format(date);
	}

	
	public static String getExecStartTime() throws ParseException {

		return getCurrentTime();
	}

	public static String getExecEndTime() throws ParseException {

		return getCurrentTime();
	}

	
	/*
	 * calculate the total time interval for queries
	 */
	public static long getTotalTime(String startTime, String endTime)
			throws ParseException {

		
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date date1 = formatter.parse(startTime);
		Date date2 = formatter.parse(endTime);

		return (date2.getTime() - date1.getTime());
	}
	
	public static String appendHash(){
		
		return "#";
	}
	
	public static String appendSlash(){
		
		return "/";
	}

}
