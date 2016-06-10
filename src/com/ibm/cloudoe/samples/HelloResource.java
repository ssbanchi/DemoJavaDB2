package com.ibm.cloudoe.samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/hello")
public class HelloResource {

	@GET
	public String getInformation() {

		// 'VCAP_APPLICATION' is in JSON format, it contains useful information about a deployed application
		// String envApp = System.getenv("VCAP_APPLICATION");

		// 'VCAP_SERVICES' contains all the credentials of services bound to this application.
		// String envServices = System.getenv("VCAP_SERVICES");
		// JSONObject sysEnv = new JSONObject(System.getenv());
		
	     Logger logger = Logger.getLogger("HelloResource");
	     logger.logp(Level.INFO, "WEBINAR", "method", "INFO level message - from WebinarJava");
	     logger.logp(Level.WARNING, "WEBINAR", "method", "WARNING level message - from WebinarJava");
	     logger.logp(Level.SEVERE, "WEBINAR", "method", "SEVERE level message - from WebinarJava");
		
		return "Hello EveryOne !!!";

	}
}