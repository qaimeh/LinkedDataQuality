package org.insight_centre.uri.factory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class URIManipulator {
	
	static final Logger _log= LoggerFactory.getLogger(URIManipulator.class);
	
	public static Set<String> uriDereferencer (Set<String> lstURI, String endp){
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<String> derefrencedVar;
		
		Set<String> dereferencedURIs= new HashSet<String>();
		
		for (String url:lstURI) {
            derefrencedVar = executor.submit(new DerefCallable(url, endp));
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
	
	
	public static Set<String> uriValidator (Set<String> URIstr, String endp){
	
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	    Future<String> validURIVar;
	     
	    Set<String> validURIs= new HashSet<String>();
	    
		for (String url:URIstr) {
            validURIVar= executor.submit(new URIValidatorCallable(url, endp));
            
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
		private final String endp;
		
		URIValidatorCallable(String url, String endp) {
			this.url= url;
			this.endp=endp;
		}

		public String call() throws Exception {

			boolean checkValidity;
			UrlValidator validator = new UrlValidator();

			checkValidity = validator.isValid(url);

			if (checkValidity) {
				return url;
			} else {
				_log.error("url is not valid {} for endpoint {}", url, endp);
				//System.err.println("uri is not valid");
				return "";
			}
			
		}

	}
	
	
	
    public static class DerefCallable implements Callable<String> {
        private final String url;
        private final String endp;
 
        DerefCallable(String url, String endp) {
            this.url = url;
            this.endp=endp;
        }
 
		public String call() throws Exception {

            String result = "";
            int code = 200, code2=200;
            try {
            	String urlRedirectChecked=urlRedirect(url);
                URL siteURL = new URL(urlRedirectChecked);
                
                HttpURLConnection connection=null, connection2=null;
                connection = (HttpURLConnection) siteURL.openConnection();
                
                connection.setRequestProperty("Accept", "application/rdf+xml");
                //connection.setRequestMethod("HEAD");
                connection.connect();
                code = connection.getResponseCode();

                connection2=(HttpURLConnection) siteURL.openConnection();
                connection2.connect();
                code2=connection2.getResponseCode();
               
                if (code == 200 || code2==200) {
                    result = url;
                    //long threadId = Thread.currentThread().getId();
    	            //System.out.println("Thread # " + threadId + " is doing this task");
    	            
                }   
            	
            	
            	

                
            } catch (Exception e) {
                System.err.println("not a dereferenced uri"+ url);
                _log.error("not a dereferenceable uri {} for endpoint {}", url, endp);
                //long threadId = Thread.currentThread().getId();
	            //System.out.println("Thread # " + threadId + " is doing this task");
	            
            }
            
			return result;
		}
	
    }
    
	private static String  urlRedirect(String url){
		
		Response resp;
		try {
			resp = Jsoup.connect(url).followRedirects(false).execute();
			 
			
			   		if(resp.hasHeader("location")){
			   			url=resp.header("location");
			   			urlRedirect(url); // callback
			   			

			   	}
		} catch (IOException e) {
		_log.error("error while checking redirection of url {} {}", e.getMessage(), url);
		}
   	
		return url;
	}
    
}
