package org.insight_centre.start;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.insight_centre.datasets.record.fetch.RecordFetcher;
import org.insight_centre.uri.factory.URIDereference;

public class LinkDataQltyMain {

static List<String> endpointLst= new ArrayList<String>();
	
	static{
		endpointLst.add("http://dbpedia.org/sparql");
	}
	
	
	public static void main(String[] args) {
		
		RecordFetcher recordFetcher= new RecordFetcher();
	
		List<String> classList=null;
		List<String> subjList=null;
		
		for(String endp: endpointLst){
		
			// get the classes of the particular endpoint
			classList=recordFetcher.getClasses(endp);
			
			// dereference the class uri
			URIDereference.uriDereferencer(classList);
			
			//get the subject of given instance
			
			
		}
		
		
		

	}
	
}
