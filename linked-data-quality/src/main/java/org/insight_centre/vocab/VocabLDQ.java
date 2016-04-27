package org.insight_centre.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class VocabLDQ {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.insight-centre.org/LinkedDataQuality#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property hasQualityProfile = m_model.createProperty( NS_Hash_Prp(NS,"hasQualityProfile"));
    
    public static final Property qualityProfile = m_model.createProperty( NS_Hash_Prp(NS,"qualityProfile"));
    
    public static final Property contains = m_model.createProperty( NS_Hash_Prp(NS,"contains"));
    
    public static final Property isGeneratedAt = m_model.createProperty( NS_Hash_Prp(NS,"isGeneratedAt"));
    
    public static final Property totalDereferenceableURIs = m_model.createProperty( NS_Hash_Prp(NS,"totalDereferenceableURIs"));
    
    public static final Property evaluatedAt = m_model.createProperty( NS_Hash_Prp(NS,"evaluatedAt"));
    public static final Property hasName = m_model.createProperty( NS_Hash_Prp(NS,"hasName"));
    public static final Property hasCategory = m_model.createProperty( NS_Hash_Prp(NS,"hasCategory"));
    public static final Property hasType = m_model.createProperty( NS_Hash_Prp(NS,"hasType"));
    public static final Property hasValue = m_model.createProperty( NS_Hash_Prp(NS,"hasValue"));
    public static final Property hasQualityMetric = m_model.createProperty( NS_Hash_Prp(NS,"hasQualityMetric"));
    /*public static final Property responseCode = m_model.createProperty( "http://vocab.deri.ie/ldq#responseCode" );
    
    public static final Property responseHeader = m_model.createProperty( "http://vocab.deri.ie/ldq#responseHeader" );
    
    public static final Property resultDataset = m_model.createProperty( "http://vocab.deri.ie/ldq#resultDataset" );
    
    public static final Property resultGraph = m_model.createProperty( "http://vocab.deri.ie/ldq#resultGraph" );
    
    public static final Property resultTupleCount = m_model.createProperty( "http://vocab.deri.ie/ldq#resultTupleCount" );
    
    public static final Resource QueryRun = m_model.createResource( "http://vocab.deri.ie/ldq#QueryRun" );
    
   */ 
    /** <p>RDFS namespace</p> */ 
    public static final Property RDFS = m_model.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    
    private static String NS_Hash_Prp(String NS, String Prp){
      	return NS.concat(Prp);
      }
    
}
