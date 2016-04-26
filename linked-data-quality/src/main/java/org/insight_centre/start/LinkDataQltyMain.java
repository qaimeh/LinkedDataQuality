package org.insight_centre.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		
		
		
		
		Set<String> lstOfClasses=null;
		Set<String> lstOfSPO_URi= null;
			
		for(String endp: endpointLst){
		
			Model datasetMdl= ModelFactory.createDefaultModel();
			
			RecordFetcher recordFetcher= new RecordFetcher();
			
			recordFetcher.queryAndGenerateRDF(endp,datasetMdl);
			//lstOfClasses=manipulateClasses(endp);
			
			//lstOfSPO_URi=manipulateInstances(endp, lstOfClasses);
			
			
			
		}

	}
	
	private static Set<String> manipulateClasses( String endp){
		
		RecordFetcher recordFetcher = new RecordFetcher();

		Set<String> classList = null;
		
		// get the classes of the particular endpoint
		classList = recordFetcher.getClasses(endp);

		// validate the uris
		URIValidator.uriValidator(classList);
		// dereference the class uri
		URIDereference.uriDereferencer(classList);
			
		return classList;
	}
	
	private static Set<String> manipulateInstances( String endp, Set<String> lstOfClassees){
		
		RecordFetcher recordFetcher = new RecordFetcher();

		Set<String>setOfInstacnes = null;
		
		for(String clazz:lstOfClassees){
			setOfInstacnes=recordFetcher.getClassInstances(endp, clazz);
			
		}
		// validate the uris
		URIValidator.uriValidator(setOfInstacnes);
		// dereference the class uri
		URIDereference.uriDereferencer(setOfInstacnes);
			
		return setOfInstacnes;
	}

	
	
}
