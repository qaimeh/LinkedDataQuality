package org.insight_centre.uri.factory;

import java.util.List;

public class URIDereference extends URIManipulator{

	public static List<String> getDereferencedURI(List<String> lstOfURIs){
	
		for(String uri:lstOfURIs){
			URIDereferencer(uri);
		}
		return null;
	}
	
	
	
	
}
