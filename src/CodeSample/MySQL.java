package CodeSample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL
{
	private String dbDomain = "localhost:3306";
	private String dbName = "test";
	private String dbUsername = "root";
	private String dbPassword = "password";		
	private String driver = "com.mysql.jdbc.Driver";
	private Connection con = null;
	
	private int queryCount = 0;
    private int querySuccessCount = 0;
    private int queryDuplicateCount = 0;
    private int queryErrorCount = 0;

	/****************************************************
	*   Constructor - Uses the given private variables to
	*    open a MySQL connection.
	****************************************************/
    public MySQL() throws MySQLOpenConnectionException
    {
    	//Open connection
		System.out.println("Connecting to " + dbDomain + "...");
		
		String url = "jdbc:mysql://" + dbDomain + "/";	
		
		try 
		{
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url+dbName,dbUsername,dbPassword);
			System.out.println("Connected to the database");			
		}		
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}		
		catch (SQLException e) 
		{
			throw new MySQLOpenConnectionException("Error - SQLException - Connection could not be made.");
		}		
		catch (InstantiationException e) 
		{
			throw new MySQLOpenConnectionException("Error - InstantiationException - Connection could not be made.");
		}		
		catch (IllegalAccessException e) 
		{
			throw new MySQLOpenConnectionException("Error - IllegalAccessException - Connection could not be made.");
		}

    	return;
    }
    
    
    /****************************************************
	*   Selects data from the MySQL DB and outputs the result
	*   return values: 0=Error, 1=OK
	****************************************************/
    public ResultSet selectData(String _mysqlQuery) throws MySQLOpenConnectionException
    {
    	this.incrementQueryCount();
    	
    	try
    	{
	    	Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(_mysqlQuery);			
			this.incrementQuerySuccessCount(); 			
			return rs;
    	}
    	catch (SQLException e) 
		{
    		this.incrementQueryErrorCount();    		
    		throw new MySQLOpenConnectionException("Error - SQLException - Select could not be made.");
		}
    }
    
    
    /****************************************************
	*   Inserts data into the user table in the MySQL DB
	*   return values: 0=Error, 1=OK, 2=Duplicate Entry
	****************************************************/
    public void insertUser(String _userId)
    {
    	this.incrementQueryCount();
    	
    	try
    	{
    		String query = "INSERT INTO user(user_id) VALUES('"+_userId+"');";     		
	    	Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			this.incrementQuerySuccessCount();
    	}
    	//Duplicate entry
    	catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e)
    	{
    		this.incrementQueryDuplicateCount();
    	}
    	//Error inserting
    	catch (SQLException e) 
		{    		
    		System.out.println("Error - SQLException - Insert could not be made.");    		
    		this.incrementQueryErrorCount();  
		}

    	return;
    }
    
    
    /****************************************************
	*   Inserts data into the user_history table in the MySQL DB
	*   return values: 0=Error, 1=OK, 2=Duplicate Entry
	****************************************************/
    public void insertUserHistory(String _userId, String _eventType, String _eventDate)
    {
    	this.incrementQueryCount();
    	
    	try
    	{
    		String query = "INSERT INTO user_history(user_id, event_type, event_date) VALUES('"+_userId+"','"+_eventType+"','"+_eventDate+"');"; 
	    	Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			this.incrementQuerySuccessCount();
    	}    	
    	//Duplicate entry
    	catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e)
    	{
    		this.incrementQueryDuplicateCount();
    	}
    	//Error inserting
    	catch (SQLException e) 
		{
    		e.printStackTrace();
    		System.out.println("Error - SQLException - Insert could not be made.");    		
    		this.incrementQueryErrorCount(); 
		}
    	
    	return;
    }
    
    
    /****************************************************
	*   Inserts data into the user_region table in the MySQL DB
	****************************************************/
    public void insertUserRegion(String _userId, String _regionId)
    {    	
    	this.incrementQueryCount();
    	
    	try
    	{
    		String query = "INSERT INTO user_region(user_id, region_id) VALUES('"+_userId+"','"+_regionId+"');";    		
	    	Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			
			this.incrementQuerySuccessCount();
    	} 
    	//Duplicate entry
    	catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e)
    	{
    		this.incrementQueryDuplicateCount();
    	}    	
    	//Error inserting
    	catch (SQLException e) 
		{
    		this.incrementQueryErrorCount();    		
    		System.out.println("Error - SQLException - Insert could not be made.\n");
		}
    	
    	return;
    }
    
    
    /****************************************************
	*   Closes the connection made to the MySQL DB
	****************************************************/
    public void close()
    {
    	try
    	{
	    	con.close();
			System.out.println("\nSuccess - Disconnected from database.");
    	}   	
    	catch (SQLException e) 
		{
    		System.out.println("\nError - SQLException - Could not close connection.");			
		}
    	
    	return; 
    }
    
    private void incrementQueryCount()			{queryCount++;}
    private void incrementQuerySuccessCount()	{querySuccessCount++;}
    private void incrementQueryDuplicateCount()	{queryDuplicateCount++;}
    private void incrementQueryErrorCount()		{queryErrorCount++;}   
	
    public int getQueryCount()					{return queryCount;}
	public int getQuerySuccessCount()			{return querySuccessCount;}
	public int getQueryDuplicateCount()			{return queryDuplicateCount;}
	public int getQueryErrorCount()				{return queryErrorCount;}
	
	protected void printResults()
	{			
		System.out.println(this.getQueryCount() +" Query Count");
		System.out.println(this.getQuerySuccessCount() + " Query Successes");
		System.out.println(this.getQueryDuplicateCount() +" Query Duplicates");
		System.out.println(this.getQueryErrorCount() +" Query Errors ");
	}
}
