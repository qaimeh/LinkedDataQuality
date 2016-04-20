package org.insight_centre.start;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.insight_centre.datasets.record.fetch.RecordFetcher;

public class LinkDataQltyMain {

static List<String> endpointLst= new ArrayList<String>();
	
	static{
		endpointLst.add("http://dbpedia.org/sparql");
	}
	
	
	public static void main(String[] args) {
		
		RecordFetcher recordFetcher= new RecordFetcher();
	
		recordFetcher.provideEndpointList(endpointLst);
		//URIValidator("http://dbpedia.org/sparql");

	}
	
	public static void URIValidator (String URIstr){
		
		boolean checkValidity;
		UrlValidator validator= new UrlValidator();
		checkValidity=validator.isValid(URIstr);
		
		if(checkValidity){
			System.out.println("uri is valid");
		}else{
			System.err.println("uri is not valid");
		}
	}

}
