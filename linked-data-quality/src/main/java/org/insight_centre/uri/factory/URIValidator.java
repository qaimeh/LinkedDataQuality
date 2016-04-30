package org.insight_centre.uri.factory;

import java.util.Set;

public class URIValidator extends URIManipulator{

	public static Set<String> getValidatedURIs(Set<String> uriSet, String endp){

		return uriValidator(uriSet, endp);
	}
	
	
}
