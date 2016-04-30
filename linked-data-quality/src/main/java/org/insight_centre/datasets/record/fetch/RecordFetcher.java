package org.insight_centre.datasets.record.fetch;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.insight_centre.uri.factory.URIDereference;
import org.insight_centre.uri.factory.URIManipulator;
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
		Set<String> strDateTimeSet;
		Set<String> dateTimeSet;
		int totalBlankNodes=0;
		int totalSubBlankNodes=0;
		int totalObjBlankNodes=0;
		
		// get the total resources of the given dataset to do further processing 
		Set<String> allResources= getAllResources(endp);
		
		
		// create the dataset resource with the LDQ vocab as prefix used
		Resource endpResource=mdl.createProperty(VocabLDQ.NS,endp).asResource();
		// create the the hasQualityProile URI as new resource 
		Resource qualityProfile= null;
		Resource qualityProfileTotalURIs= null;
		Resource qualityProfileDateTypeAsStr= null;
		Resource qualityProfileDateTypeAsStrPercentage= null;
		Resource qualityProfileTotalDateType= null;
		Resource qualityProfileDerefInfo= null;
		Resource qualityProfileDerefQOI= null;
		Resource qualityProfileValidURIsInfo= null;
		Resource qualityProfileValidURIsQOI=null;
		Resource qualityProfileBlankNodesInfo=null;
		Resource qualityProfileSubBlankNodesInfo=null;
		Resource qualityProfileObjBlankNodesInfo=null;

		try {
			String currentTime= LDQUtils.getCurrentTime();
			endpResource.addProperty(Void.sparqlEndpoint, mdl.createResource(endp))
			.addProperty(VocabLDQ.hasQualityProfile,
					mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)));
			// create a dataset property and add into the model					
			qualityProfile=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime));
			
			// use the hasQualityProfile resoruce of the dataset as new resource
			
			// get the set of DateTime values
			
			strDateTimeSet=getDateTimeStamp(endp, "string");
			
			dateTimeSet=getDateTimeStamp(endp, "dateTimeStamp");
			
			qualityProfile.addProperty(VocabLDQ.isGeneratedAt, mdl.createTypedLiteral(currentTime,
					"http://www.w3.org/2001/XMLSchema#dateTimeStamp"));
					//.addProperty(VocabLDQ.totalResources, mdl.createTypedLiteral(allResources.size()))
					//.addProperty(VocabLDQ.strDateLiterals, mdl.createTypedLiteral((strDateTimeSet!=null) ? strDateTimeSet.size():0))
					//.addProperty(VocabLDQ.DateTypedLiterals, mdl.createTypedLiteral((dateTimeSet!=null) ? dateTimeSet.size():0));
			
			// get the total resources/URIs of the dataset
			if(allResources!=null&&allResources.size()>0){
				
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalURIs")));
				
				// dereference Info
				qualityProfileTotalURIs=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("totalURIs"));
				
				qualityProfileTotalURIs.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Total dateset URIs")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "Info")
						.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(allResources.size())));
			}
			
			
			// get the blank nodes
			
			totalBlankNodes= getTotalBlankNodes(endp);
			
			if(totalBlankNodes>0){
				
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalBlankNodes")));
				
				// dereference Info
				qualityProfileBlankNodesInfo=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("totalBlankNodes"));
				
				qualityProfileBlankNodesInfo.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Total Blank Nodes")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "Info")
						.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(totalBlankNodes)));
			}
			
			totalSubBlankNodes= getsubjectAsBlank(endp);
			
			if(totalSubBlankNodes>0){
				
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalSubjectBlankNodes")));
				
				// dereference Info
				qualityProfileSubBlankNodesInfo=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("totalSubjectBlankNodes"));
				
				qualityProfileSubBlankNodesInfo.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Total Subjects Blank Nodes")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "Info")
						.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(totalSubBlankNodes)));
			}
			
			totalObjBlankNodes= getobjectAsBlank(endp);
			
			if(totalObjBlankNodes>0){
				
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalObjectBlankNodes")));
				
				// dereference Info
				qualityProfileObjBlankNodesInfo=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash())
						.concat("totalObjectBlankNodes"));
				
				qualityProfileObjBlankNodesInfo.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
						"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
						.addProperty(VocabLDQ.hasName, "Total Objects Blank Nodes")
						.addProperty(VocabLDQ.hasCategory, "Completeness")
						.addProperty(VocabLDQ.hasCategory, "accuracy")
						.addProperty(VocabLDQ.hasType, "Info")
						.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
						.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(totalObjBlankNodes)));
			}
			
			
			
			// get the total DateTime as String of the dataset
						if(strDateTimeSet!=null&&strDateTimeSet.size()>0){
							
							qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
									.concat(LDQUtils.appendSlash()).concat("totalDateTimeAsString")));
							
							// dereference Info
							qualityProfileDateTypeAsStr=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
									.concat(LDQUtils.appendSlash())
									.concat("totalDateTimeAsString"));
							
							qualityProfileDateTypeAsStr.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
									"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
									.addProperty(VocabLDQ.hasName, "Total DateTime Type as String")
									.addProperty(VocabLDQ.hasCategory, "Completeness")
									.addProperty(VocabLDQ.hasCategory, "accuracy")
									.addProperty(VocabLDQ.hasType, "QOI")
									.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
									.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(strDateTimeSet.size())));
						}
						
				// get the the data type 
						
			if(dateTimeSet!=null&&dateTimeSet.size()>0){
							
							qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
									.concat(LDQUtils.appendSlash()).concat("totalDateTime")));
							
							// dereference Info
							qualityProfileTotalDateType=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
									.concat(LDQUtils.appendSlash())
									.concat("totalDateTime"));
							
							qualityProfileTotalDateType.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
									"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
									.addProperty(VocabLDQ.hasName, "Total DateTime Types")
									.addProperty(VocabLDQ.hasCategory, "Completeness")
									.addProperty(VocabLDQ.hasCategory, "accuracy")
									.addProperty(VocabLDQ.hasType, "Info")
									.addProperty(VocabLDQ.hasQualityMetric, "Number(int)")
									.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Integer(dateTimeSet.size())));
						}
				
			// get the date type string percentage 
			int totalDateTypes= ((strDateTimeSet!=null) ? strDateTimeSet.size() : 0) + ((dateTimeSet!=null) ? dateTimeSet.size() : 0);
			
			if(totalDateTypes>0){
			qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash()).concat("percentageDateTimeAsString")));
			
			qualityProfileDateTypeAsStrPercentage=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash())
					.concat("percentageDateTimeAsString"));
			
			qualityProfileDateTypeAsStrPercentage.addProperty(VocabLDQ.evaluatedAt, mdl.createTypedLiteral(LDQUtils.getCurrentTime(),
					"http://www.w3.org/2001/XMLSchema#dateTimeStamp"))
					.addProperty(VocabLDQ.hasName, "Percentage of Date type as string")
					.addProperty(VocabLDQ.hasCategory, "Completeness")
					.addProperty(VocabLDQ.hasCategory, "accuracy")
					.addProperty(VocabLDQ.hasType, "QOI")
					.addProperty(VocabLDQ.hasQualityMetric, "Percentage(%)")
					.addProperty(VocabLDQ.hasTendency, "increase")
					.addProperty(VocabLDQ.hasValue, mdl.createTypedLiteral(new Float(LDQUtils.calcPercentage(strDateTimeSet.size(),totalDateTypes))));
			
			}			
			// get the set of dereferenced URIs for future work this time only the size is required
			derefURISet=URIDereference.getDereferencedURI(allResources,endp);
		
			if(derefURISet!=null&&derefURISet.size()>0){
			qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash()).concat("totalDereferenceableURIs")));
			
			// dereference Info
			qualityProfileDerefInfo=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
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
			qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
					.concat(LDQUtils.appendSlash()).concat("percentageDereferenceableURIs")));
			
			qualityProfileDerefQOI=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
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
			validURISet = URIValidator.getValidatedURIs(allResources,endp);
			
			if(validURISet!=null&&validURISet.size()>0){
				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("totalValidURIs")));
				
				
				// validURIs info
				qualityProfileValidURIsInfo=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
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
				

				qualityProfile.addProperty(VocabLDQ.contains, mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
						.concat(LDQUtils.appendSlash()).concat("percentageValidURIs")));
				
				qualityProfileValidURIsQOI=mdl.createResource(VocabLDQ.NS.concat(getHost(endp).concat(LDQUtils.appendSlash())).concat(currentTime)
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
			_log.error("exception {}", e);
		}
		
		
		try {
			outputStream = new FileOutputStream( "data.n3",true );
			mdl.write(outputStream,"N3");
			mdl.write(System.out,"RDF/XML");
		} catch (IOException e) {
			_log.error("IO exception {}", e);
		}
		
		

		
	}
	public String getHost (String endp){
		
		URL urlDomain=null;
		try {
			urlDomain= new URL(endp);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return urlDomain.getHost();
	}
	
	
	public int getTotalBlankNodes(String endp){
		
		int totalBlankNodes=0;
		
		totalBlankNodes+=getBlankNodes(endp,"?s");
		totalBlankNodes+=getBlankNodes(endp,"?p");
		totalBlankNodes+=getBlankNodes(endp,"?o");
		
		return totalBlankNodes;
	
	}
	
	public int getsubjectAsBlank (String endp){
		int subBlankNodes=0;
		subBlankNodes+=getBlankNodes(endp,"?s");
		return subBlankNodes;
		
	}
	public int getobjectAsBlank (String endp){
		int objBlankNodes=0;
		objBlankNodes+=getBlankNodes(endp,"?o");
		return objBlankNodes;
		
	}
	public int getBlankNodes(String endp, String var){
		
		int totalBlankNodes=0;
		
		QueryExecution qryExec= null;
		
		String qryStr= "select (count("+var+") as ?asBlankNode) " +
				"where { ?s ?p ?o. " +
				"filter(isBlank("+var+")) }";
				
		try{	
			qryExec= execQueries(endp, qryStr);
			
			ResultSet resuts= qryExec.execSelect();
			
			while(resuts.hasNext()){
				
				QuerySolution sol= resuts.nextSolution();
				totalBlankNodes=(Integer) sol.get("?asBlankNode").asLiteral().getValue();
			}

		
		}catch(Exception e){
			_log.error("exception: {}", e);
		}
		
		return totalBlankNodes;
	}
	
	public Set<String> getDateTimeStamp(String endp, String dataType){
		
		
		Set<String> setOfUri= new HashSet<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "prefix xsd: <http://www.w3.org/2001/XMLSchema#> " +
				"select * { ?s ?p ?o " +
				"filter (datatype(?o) = xsd:"+dataType+") " +
				"filter regex(?o,\"^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]\",\"i\")" +
						"}";
	try{	
		qryExec= execQueries(endp, qryStr);
		
		ResultSet resuts= qryExec.execSelect();
		
		while(resuts.hasNext()){
			
			QuerySolution sol= resuts.nextSolution();

			RDFNode rdfNObj = sol.get("?o");
			setOfUri.add(sol.get("?o").toString());
		}

	
	}catch(Exception e){
		_log.error("exception: {}", e);
	}
	if(setOfUri.size()>=1){
		return setOfUri;
		}else{
			return null;
		}
	}
	public Set<String> getAllResources(String endp){
		

		Set<String> setOfUri=  new HashSet<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct * {?s ?p ?o}";
		try{	
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
		}catch(Exception e){
			_log.error("exception: {}", e);
		}
		return setOfUri;
	}
	
	public Set<String> getClasses(String endp) {
		
		Set<String> setOfUri=  new HashSet<String>();
		QueryExecution qryExec= null;
		
		String qryStr= "select distinct ?class {[] a ?class} ";
		
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
		
		String qryStr= String.format("select distinct ?class {?class a <%s>} ",classUri);
		
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
		
		String qryStr= "select distinct ?s {?s ?p ?o}";
		
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
		
		String qryStr= "select distinct ?p {?s ?p ?o}";
		
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
		
		String qryStr= "select distinct ?o {?s ?p ?o}";
		
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
