package org.insight_centre.uri.factory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class URIManipulator {
	
	static final Logger _log= LoggerFactory.getLogger(URIManipulator.class);
	
	public static Set<String> uriDereferencer (Set<String> lstURI){
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<String> derefrencedVar;
		
		Set<String> dereferencedURIs= new HashSet<String>();
		
		for (String url:lstURI) {
            derefrencedVar = executor.submit(new DerefCallable(url));
            try {
            	// add the dereferenced URI into set
				dereferencedURIs.add(derefrencedVar.get());
				
			} catch (InterruptedException e) {
				_log.error(" thread interrupted exception {}", e.getCause());
				e.printStackTrace();
			} catch (ExecutionException e) {
				_log.error("thread execution exception {}", e.getCause());
			}
            
        }
        executor.shutdown();
        
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
 
        }
        System.out.println("\nFinished all threads");
        
		
		return dereferencedURIs;
	}
	
	
	public static Set<String> uriValidator (Set<String> URIstr){
	
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	    Future<String> validURIVar;
	     
	    Set<String> validURIs= new HashSet<String>();
	    
		for (String url:URIstr) {
            validURIVar= executor.submit(new URIValidatorCallable(url));
            
            try {
            	// add the valid URI into set
            	if(!validURIVar.get().equals(""))
            	validURIs.add(validURIVar.get());
				
			} catch (InterruptedException e) {
				_log.error(" thread interrupted exception {}", e.getCause());
				e.printStackTrace();
			} catch (ExecutionException e) {
				_log.error("thread execution exception {}", e.getCause());
			}

        }
        executor.shutdown();
        
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
 
        }
        System.out.println("\nFinished all threads");
		
        return validURIs;
		
	}
	
	public static class URIValidatorCallable implements Callable<String>{

		private final String url;
		
		URIValidatorCallable(String url) {
			this.url= url;
		}

		public String call() throws Exception {

			boolean checkValidity;
			UrlValidator validator = new UrlValidator();

			checkValidity = validator.isValid(url);

			if (checkValidity) {
				System.out.println("uri is valid");
				return url;
			} else {
				System.err.println("uri is not valid");
				return "";
			}
			
		}

	}
	
	
	
    public static class DerefCallable implements Callable<String> {
        private final String url;
 
        DerefCallable(String url) {
            this.url = url;
        }
 
		public String call() throws Exception {

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
                    result = url;
                    long threadId = Thread.currentThread().getId();
    	            System.out.println("Thread # " + threadId + " is doing this task");
    	            
                }
            } catch (Exception e) {
                System.err.println("not a dereferenced uri"+ url);
                _log.error("not a dereferenceable uri {}", url);
                long threadId = Thread.currentThread().getId();
	            System.out.println("Thread # " + threadId + " is doing this task");
	            
            }
            
			return result;
		}
    }
}
