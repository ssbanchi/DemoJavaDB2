package com.ibm.cloudoe.samples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.ibm.db2.jcc.DB2SimpleDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

@WebServlet("/SQLDB2Example")
public class SQLDB2Example extends HttpServlet {
	private static final long serialVersionUID = 1L;


	//----------------------
	
	public SQLDB2Example() {
		super();
	}

  //--------------------- (2.1)
  
	 private static String databaseHost;
	 private static Integer port;
	 private static String databaseName;
	 private static String user;
	 private static String password;
	 private static String url;
	 private static String schemaName;
	 private static String tabName;

  
	private boolean processVCAP(PrintWriter writer) {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for DB2 connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		writer.println("VCAP_SERVICES content: " + VCAP_SERVICES);

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			writer.println("Searching through VCAP keys");
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				writer.println("Key is: " + eachkey);
				// Just in case the service name gets changed to lower case in the future, use toUpperCase
				if (eachkey.toUpperCase().contains("DASHDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				writer.println("Cannot find any SQLDB service in the VCAP; exiting");
				return false;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			writer.println("Service found: " + obj.get("name"));
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			databaseName = (String) obj.get("db");
			port = (Integer)obj.get("port");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			url = (String) obj.get("jdbcurl");
		} else {
			writer.println("VCAP_SERVICES is null");
			return false;
		}
		writer.println();
		writer.println("database host: " + databaseHost);
		writer.println("database port: " + port);
		writer.println("database name: " + databaseName);
		writer.println("username: " + user);
		writer.println("password: " + password);
		writer.println("url: " + url);
		return true;
	}
  //--------------------- (2.1)
  
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
     
    //--------------------- (1) 
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("TEST");
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("Host IP:" + InetAddress.getLocalHost().getHostAddress());
   //--------------------- (1)
   
   //--------------------- (2.2)    
		    writer.println();
		    writer.println("PARSING SYS VAR VCAP");
		    writer.println();
        processVCAP(writer);
   //--------------------- (2.2)

		// process the VCAP env variable and set all the global connection parameters
		if (processVCAP(writer)) {
	
  
     //--------------------- (3)
     
			//CONNECT TO DB
		Connection con = null;
		try {
			writer.println();
			writer.println("Connecting to the database");
			DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
			dataSource.setServerName(databaseHost);
			dataSource.setPortNumber(port);
			dataSource.setDatabaseName(databaseName);
			dataSource.setUser(user);
			dataSource.setPassword (password);
			dataSource.setDriverType(4);
			con=(Connection) dataSource.getConnection();
			writer.println();
			con.setAutoCommit(false);
    
			writer.println("Connected to DB !!!");
    
		} catch (SQLException e) {
			writer.println("Error connecting to database");
			writer.println("SQL Exception: " + e);
			return;
		}
      
       
    //--------------------- (3)
    
    //--------------------- (4)
    
    
		//DYN SQL STATEMENT
		Statement stmt = null;
		String tableName = "";
		String sqlStatement = "";
		//schema name
		//String schemaName = "SQLDBSAMPLE";
		//table name
		//tableName = schemaName + "." + "CLIENT" + System.currentTimeMillis();
		//tableName = schemaName + "." + tabName;
		tableName = "PERSONS";

		//---------- create schema
//		try {
//			stmt = con.createStatement();
//			// Create the CREATE SCHEMA SQL statement and execute it
//			sqlStatement = "CREATE SCHEMA " + schemaName;
//			writer.println("Executing: " + sqlStatement);
//			stmt.executeUpdate(sqlStatement);
//		} catch (SQLException e) {
//			writer.println("Error creating schema: " + e);
//		}

		//---------- create table
		try {
			stmt = con.createStatement();
			// Create the CREATE TABLE SQL statement and execute it
			sqlStatement = "CREATE TABLE " + tableName
					+ " (NAME VARCHAR(20), AGE INTEGER)";
			writer.println("Executing: " + sqlStatement);
			stmt.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			writer.println("Error creating table: " + e);
		}

		// Execute some SQL statements on the table: Insert, Select and Delete
		try {
			
			//insert
			sqlStatement = "INSERT INTO " + tableName
					+ " VALUES (\'Paolo Rossi\', 52)";
			writer.println("Executing: " + sqlStatement);
			stmt.executeUpdate(sqlStatement);
			
			sqlStatement = "INSERT INTO " + tableName
					+ " VALUES (\'Mauro Bianchi\', 48)";
			writer.println("Executing: " + sqlStatement);
			stmt.executeUpdate(sqlStatement);
			
			sqlStatement = "INSERT INTO " + tableName
					+ " VALUES (\'Alessandro Verdi\', 34)";
			writer.println("Executing: " + sqlStatement);
			stmt.executeUpdate(sqlStatement);
			
			sqlStatement = "INSERT INTO " + tableName
					+ " VALUES (\'Paolo Ciani\', 34)";
			writer.println("Executing: " + sqlStatement);
			stmt.executeUpdate(sqlStatement);
			
			//select
			sqlStatement = "SELECT * FROM " + tableName
					+ " WHERE NAME LIKE \'Paolo%\'";
			ResultSet rs = stmt.executeQuery(sqlStatement);
			writer.println("Executing: " + sqlStatement);

			// Process the result set
			String empNo;
			while (rs.next()) {
				empNo = rs.getString(1);
				writer.println("  Found Persons: " + empNo);
			}
			// Close the ResultSet
			rs.close();

//			// Delete the record
//			sqlStatement = "DELETE FROM " + tableName
//					+ " WHERE NAME = \'John Smith\'";
//			writer.println("Executing: " + sqlStatement);
//			stmt.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			writer.println("Error executing:" + sqlStatement);
			writer.println("SQL Exception: " + e);
		}

//		// Remove the table from the database
//		try {
//			sqlStatement = "DROP TABLE " + tableName;
//			writer.println("Executing: " + sqlStatement);
//			stmt.executeUpdate(sqlStatement);
//		} catch (SQLException e) {
//			writer.println("Error dropping table: " + e);
//		}
		
//		// Remove the schema from the database
//		try {
//			sqlStatement = "DROP SCHEMA " + schemaName + " RESTRICT";
//			writer.println("Executing: " + sqlStatement);
//			stmt.executeUpdate(sqlStatement);
//		} catch (SQLException e) {
//			writer.println("Error Dropping schema: " + e);
//		}

		// Close everything off
		try {
			// Close the Statement
			stmt.close();
			// Connection must be on a unit-of-work boundary to allow close
			con.commit();
			// Close the connection
			con.close();
			writer.println("Finished");

		} catch (SQLException e) {
			writer.println("Error closing things off");
			writer.println("SQL Exception: " + e);
		}
      
      
         //--------------------- (4)
      
		}
    
    

    
		writer.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
