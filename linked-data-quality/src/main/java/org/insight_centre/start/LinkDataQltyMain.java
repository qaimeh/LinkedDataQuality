package org.insight_centre.start;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.insight_centre.datasets.record.fetch.RecordFetcher;
import org.insight_centre.uri.factory.URIDereference;
import org.insight_centre.uri.factory.URIValidator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class LinkDataQltyMain {

static List<String> endpointLst= new ArrayList<String>();
	
	static{
		endpointLst.add("http://dbpedia.org/sparql");
	}
	
	
	public static void main(String[] args) {
		
		Model datasetMdl= ModelFactory.createDefaultModel();
		
		
		List<String> lstOfClasses=null;
		List<String> lstOfSPO_URi= null;
			
		for(String endp: endpointLst){
		
			lstOfClasses=workOnClasses(endp);
			
			lstOfSPO_URi=workOnInstances(endp, lstOfClasses);
			
			
			
		}

	}
	
	private static List<String> workOnClasses( String endp){
		
		RecordFetcher recordFetcher = new RecordFetcher();

		List<String> classList = null;
		
		// get the classes of the particular endpoint
		classList = recordFetcher.getClasses(endp);

		// validate the uris
		URIValidator.uriValidator(classList);
		// dereference the class uri
		URIDereference.uriDereferencer(classList);
			
		return classList;
	}
	
	private static List<String> workOnInstances( String endp, List<String> lstOfClassees){
		
		RecordFetcher recordFetcher = new RecordFetcher();

		List<String>lstOfInstacnes = null;
		
		for(String clazz:lstOfClassees){
			lstOfInstacnes=recordFetcher.getClassInstances(endp, clazz);
			
		}
		// validate the uris
		URIValidator.uriValidator(lstOfInstacnes);
		// dereference the class uri
		URIDereference.uriDereferencer(lstOfInstacnes);
			
		return lstOfInstacnes;
	}

	
	
}
