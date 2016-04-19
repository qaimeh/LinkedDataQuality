package org.insight_centre.datasets.record.fetch;

import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class RecordFetcher {

	public void provideEndpointList( List<String> endpLst){
		
		for(String endp: endpLst){
			queryEndpoint(endp);
		}
	}

	private void queryEndpoint(String endp) {
		
		getClasses(endp);
	}

	private void getClasses(String endp) {
		
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct * {[] a ?class} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			System.out.println(sol);
			
		}
		
	}
	
	
	private QueryExecution execQueries(String endpoint, String qry) {

		Query query = QueryFactory.create(qry);
		return QueryExecutionFactory.sparqlService(endpoint, query);

	}
	
}
