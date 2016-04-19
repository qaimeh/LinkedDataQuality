package org.insight_centre.configurations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.ConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlQueries {

	final static Logger _log= LoggerFactory.getLogger(SparqlQueries.class.getName());
	
	
	private BufferedReader fileReader = null;
	private JSONObject genreJsonObject = null;
	
	
	public JSONObject loadConfiguration() throws IOException,ConfigurationException {

		try {
			fileReader = new BufferedReader(new InputStreamReader(this
					.getClass().getResourceAsStream("/" + "queries.json")));
			genreJsonObject = (JSONObject) JSONValue
					.parseWithException(fileReader);

		} catch (ParseException e) {
			_log.error("json parsing exception {}", e);
		} catch (IOException ioe) {
			_log.error("File not loaded {}", ioe);
		}

		return genreJsonObject;

	}
	
	
	
}
