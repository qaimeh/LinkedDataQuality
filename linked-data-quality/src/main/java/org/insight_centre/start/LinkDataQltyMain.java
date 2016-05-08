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
		//endpointLst.add("http://bio2rdf.org/sparql");
		endpointLst.add("http://data.semanticweb.org/sparql");
		endpointLst.add("http://lod.openlinksw.com/sparql/");
		endpointLst.add("http://geo.linkeddata.es/sparql");
		
		//for test
		//endpointLst.add("http://kegg.bio2rdf.org/sparql");
		endpointLst.add("http://chebi.bio2rdf.org/sparql");
		
		
		//endpointLst.add("http://lod.openlinksw.com/sparql/");
		//endpointLst.add("http://geo.linkeddata.es/sparql");
	}
	
	
	public static void main(String[] args) {
	
		for(String endp: endpointLst){
		
			Model datasetMdl= ModelFactory.createDefaultModel();
			
			RecordFetcher recordFetcher= new RecordFetcher();
			
			recordFetcher.queryAndGenerateRDF(endp,datasetMdl);
			
		}

	}
	
	
	
}
