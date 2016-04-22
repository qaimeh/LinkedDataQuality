package org.insight_centre.uri.factory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.validator.routines.UrlValidator;


public abstract class URIManipulator {
	
	
	public static String uriDereferencer (List<String> lstURI){
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
     
		for (String url:lstURI) {
            Runnable worker = new MyRunnable(url);
            executor.execute(worker);
        }
        executor.shutdown();
        
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
 
        }
        System.out.println("\nFinished all threads");
        
		
		return null;
	}
	
	
	public static void uriValidator (List<String> URIstr){
	
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	     
		for (String url:URIstr) {
            Runnable worker = new URIValidatorRunnable(url);
            executor.execute(worker);
        }
        executor.shutdown();
        
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
 
        }
        System.out.println("\nFinished all threads");
		
		
	}
	
	public static class URIValidatorRunnable implements Runnable{

		private final String url;
		
		URIValidatorRunnable(String url) {
			this.url= url;
		}
		public void run() {
					
			boolean checkValidity;
			UrlValidator validator= new UrlValidator();
			
			checkValidity=validator.isValid(url);
			
			if(checkValidity){
				System.out.println("uri is valid");
			}else{
				System.err.println("uri is not valid");
			}
			
		}
		
	}
	
	
	
    public static class MyRunnable implements Runnable {
        private final String url;
 
        MyRunnable(String url) {
            this.url = url;
        }
 
        public void run() {
        	
            String result = "";
            int code = 200;
            try {
                URL siteURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) siteURL
                        .openConnection();
                connection.setRequestProperty("Accept", "application/rdf+xml");
                //connection.setRequestMethod("HEAD");
                connection.connect();
 
                code = connection.getResponseCode();
                if (code == 200) {
                    result = "Green\t";
                    long threadId = Thread.currentThread().getId();
    	            System.out.println("Thread # " + threadId + " is doing this task");
    	            
                }
            } catch (Exception e) {
                result = "->Red<-\t";
                long threadId = Thread.currentThread().getId();
	            System.out.println("Thread # " + threadId + " is doing this task");
	            
            }
            System.out.println(url + "\t\tStatus:" + result);
        }
    }
}
