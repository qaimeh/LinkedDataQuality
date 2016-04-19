package org.insight_centre.start;

import java.util.ArrayList;
import java.util.List;

import org.insight_centre.datasets.record.fetch.RecordFetcher;

public class LinkDataQltyMain {

static List<String> endpointLst= new ArrayList<String>();
	
	static{
		endpointLst.add("http://dbpedia.org/sparql");
	}
	
	
	public static void main(String[] args) {
		
		RecordFetcher recordFetcher= new RecordFetcher();
	
		recordFetcher.provideEndpointList(endpointLst);

	}

}
