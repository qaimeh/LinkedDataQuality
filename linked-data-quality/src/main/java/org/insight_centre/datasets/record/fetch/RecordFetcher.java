package org.insight_centre.datasets.record.fetch;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.insight_centre.uri.factory.URIDereference;
import org.insight_centre.uri.factory.URIValidator;
import org.insight_centre.util.LDQUtils;
import org.insight_centre.vocab.VocabLDQ;
import org.insight_centre.vocab.Void;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;



public class RecordFetcher {
Logger _log= LoggerFactory.getLogger(RecordFetcher.class);
	
FileWriter out=null;

FileOutputStream outputStream;
	
	public void queryAndGenerateRDF(String endp, Model mdl) {
		
		Set<String> derefURISet;
		Set<String> validURISet;
		
		// get the total resources of the given dataset to do further processing 
		Set<String> allResources= getAllResources(endp);
		
		// create the dataset resource with the LDQ vocab as prefix used
		Resource endpResource=mdl.createProperty(VocabLDQ.NS,endp).asResource();
		// create the the hasQualityProile URI as new resource 
		Resource qualityProfile= null;
		Resource qualityProfileDerefInfo= null;
		Resource qualityProfileDerefQOI= null;
		Resource qualityProfileValidURIsInfo= null;
		Resource qualityProfileValidURIsQOI=null;
		try {
			String currentTime= LDQUtils.getCurrentTime();
			endpResource.addProperty(Void.sparqlEndpoint, mdl.createResource(endp))
			.addProperty(VocabLDQ.hasQualityProfile,
					mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)));
			// create a dataset property and add into the model					
			qualityProfile=mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime));
			
			// use the hasQualityProfile resoruce of the dataset as new resource
			
			qualityProfile.addProperty(VocabLDQ.isGeneratedAt, mdl.createTypedLiteral(currentTime,
					"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
					.addProperty(VocabLDQ.totalResources, mdl.createTypedLiteral(allResources.size()));
			
			
			
			// get the set of dereferenced URIs for future work this time only the size is required
			derefURISet=URIDereference.getDereferencedURI(allResources);
		
			if(derefURISet.size()>0){
			qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash()).concat("totalDereferenceableURIs")));
			
			// dereference Info
			qualityProfileDerefInfo=mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash())
					.concat("totalDereferenceableURIs"));
			
			qualityProfileDerefInfo.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
					"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
					.addProperty(VocabLDQ.hasName, "Total Dereferenceable URIs")
					.addProperty(VocabLDQ.hasCategory, "Completeness")
					.addProperty(VocabLDQ.hasCategory, "accuracy")
					.addProperty(VocabLDQ.hasType, "Info")
					.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
					.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(derefURISet.size())));
			
			// dereference QOI
			qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash()).concat("percentageDereferenceableURIs")));
			
			qualityProfileDerefQOI=mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash())
					.concat("percentageDereferenceableURIs"));
			
			qualityProfileDerefQOI.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
					"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
					.addProperty(VocabLDQ.hasName, "Percentage of Dereferenceable URIs")
					.addProperty(VocabLDQ.hasCategory, "Completeness")
					.addProperty(VocabLDQ.hasCategory, "accuracy")
					.addProperty(VocabLDQ.hasType, "QOI")
					.addProperty(VocabLDQ.hasQualityMetric, "Percentage(%)")
					.addProperty(VocabLDQ.hasTendency, "increase")
					.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Float(LDQUtils.calcPercentage(derefURISet.size(),allResources.size()))));
				
			}

			// get size of  the  valid uri's 
			validURISet = URIValidator.getValidatedURIs(allResources);
			
			if(validURISet.size()>0){
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalValidURIs")));
				
				
				// validURIs info
				qualityProfileValidURIsInfo=mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("totalValidURIs"));
				
				qualityProfileValidURIsInfo.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Total valid URIs")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "Info")
						.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(validURISet.size())));
					
				// valid URIs QOI
				

				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("percentageValidURIs")));
				
				qualityProfileValidURIsQOI=mdl.createResource(VocabLDQ.NS.concat(endp.concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("percentageValidURIs"));
				
				qualityProfileValidURIsQOI.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Percentage of valid URIs")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "QOI")
						.addProperty(VocabLDQ.hasQualityMetric, "Percentage(%)")
						.addProperty(VocabLDQ.hasTendency, "increase")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Float(LDQUtils.calcPercentage(validURISet.size(),allResources.size()))));
				
				}
			
		
		} catch (Exception e) {
			_log.error("exception {}", e.getCause());
		}
		
		
		try {
			outputStream = new FileOutputStream( "/Users/qaiser/git/LinkedDataQuality/linked-data-quality/src/main/resources/data.n3",true );
			mdl.write(outputStream,"RDF/XML");
		} catch (IOException e) {
			_log.error("IO exception {}", e.getCause());
		}
		
		

		
	}
	
	public Set<String> getAllResources(String endp){
		

		Set<String> setOfUri=  new HashSet<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct * {?s ?p ?o} limit 500";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			RDFNode rdfNSub = sol.get("?s");
			RDFNode rdfNPred = sol.get("?p");
			RDFNode rdfNObj = sol.get("?o");

			if (!rdfNSub.toString().startsWith("http://www.openlinksw.com/")) {
				if (checkIfResource(rdfNSub)) {
					setOfUri.add(sol.get("?s").toString());
				}
				if (checkIfResource(rdfNPred)) {
					setOfUri.add(sol.get("?p").toString());
				}
				if (checkIfResource(rdfNObj)) {
					setOfUri.add(sol.get("?o").toString());
				}
			}
		}

		return setOfUri;
	}
	
	public Set<String> getClasses(String endp) {
		
		Set<String> setOfUri=  new HashSet<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?class {[] a ?class} limit 10";
		
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();
			RDFNode rdfN = sol.get("?class");
			
			if(checkIfResource(rdfN)){
				setOfUri.add(sol.get("?class").toString());
			}
		}
		
		return setOfUri;
	}
	
	
	public Set<String> getClassInstances(String endp, String classUri){
		Set<String> listOfUri=  new HashSet<String>();
	
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
