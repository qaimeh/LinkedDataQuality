package org.insight_centre.datasets.record.fetch;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class RecordFetcher {

	public List<String> getClasses(String endp) {
		
		List<String> listOfUri=  new ArrayList<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?class {[] a ?class} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			RDFNode rdfN = sol.get("?class");
			
			if(checkIfResource(rdfN)){
				listOfUri.add(sol.get("?class").toString());
			}
		}
		
		return listOfUri;
	}
	
	
	public List<String> getClassInstances(String endp, String classUri){
		List<String> listOfUri=  new ArrayList<String>();
	
	QueryExecution qryExec= null;
		
		String qryStr= String.format("select distinct ?class {?class a <%s>} limit 10",classUri);
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			System.out.println(sol.get("?s"));
			listOfUri.add(sol.get("?s").toString());
		}
		
		
		return listOfUri;
		
	}
	
	public List<String> getSubject(String endp) {
		
		List<String> listOfUri=  new ArrayList<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?s {?s ?p ?o} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			System.out.println(sol.get("?s"));
			listOfUri.add(sol.get("?s").toString());
		}
		
		
		return listOfUri;
	}
	
	public List<String> getPredicate(String endp){

		List<String> listOfUri=  new ArrayList<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?p {?s ?p ?o} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			System.out.println(sol.get("?p"));
			listOfUri.add(sol.get("?p").toString());
		}
		
		return listOfUri;
	}
	

	public List<String> getObject(String endp) {
		
		List<String> listOfUri=  new ArrayList<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?o {?s ?p ?o} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			System.out.println(sol.get("?o"));
			listOfUri.add(sol.get("?o").toString());
		}
		
		
		return listOfUri;
	}

	
	private boolean checkIfResource(RDFNode rdfNode){
		
		if(rdfNode.isResource()){
			return true; // return true if rdfNode is URI
		}else{
			return false; // false if rdfNode is literal or ...
		}
		
	}
	
	
	private QueryExecution execQueries(String endpoint, String qry) {

		Query query = QueryFactory.create(qry);
		return QueryExecutionFactory.sparqlService(endpoint, query);

	}
	
}
