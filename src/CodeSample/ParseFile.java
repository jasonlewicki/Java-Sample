package CodeSample;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public abstract class ParseFile
{
	protected BufferedReader br;
	protected StringTokenizer st;
	protected MySQL mysqlHandle;
    
    private int lineCount = 0;
    private int lineSuccessCount = 0;
    private int lineSkipCount = 0;
    private int lineErrorCount = 0;
     
    protected abstract boolean openFileHandler(String fileName);
    protected abstract void closeFileHandler();		
	protected abstract void readLine(String line);
	
	/****************************************************
	*   Open handle to MySQL DB
	****************************************************/
	protected boolean openHandleMySQL()
    {
		try
        {
        	mysqlHandle = new MySQL(); 
        	return true;
        }
    	catch(MySQLOpenConnectionException e)
        {
        	 System.out.println(e);
        	 return false;
        } 
    }
	protected void closeHandleMySQL()	{mysqlHandle.close();}
	
	protected void incrementLineCount()			{lineCount++;}
	protected void incrementLineSuccessCount()	{lineSuccessCount++;}
	protected void incrementLineSkipCount()		{lineSkipCount++;}
	protected void incrementLineErrorCount()	{lineErrorCount++;}		
	
	protected int getLineCount()				{return lineCount;}	
	protected int getLineSuccessCount()			{return lineSuccessCount;}
	protected int getLineSkipCount()			{return lineSkipCount;}
	protected int getLineErrorCount()			{return lineErrorCount;}
	
	
	/****************************************************
	*   Read file, line by line
	****************************************************/
	protected void readFile()
    {
		String strLine = "";
		
    	//Read comma separated file line by line
        try
		{
			while( (strLine = br.readLine()) != null)
			{
				//Increment line number
				this.incrementLineCount();
				
				//Show that something is happening
				this.showLoading();
				
				//Parse current line
				this.readLine(strLine);				                 
			}
		}
        catch (IOException e)
		{
			e.printStackTrace();
		}      
        
        //Close connection to MySQL DB
        this.closeHandleMySQL();
        
        //Close file handler
        this.closeFileHandler();
        
        //Output parsing results
        this.printResults();                   
    }
	
	/****************************************************
	*   Display dots to signify that something is happening
	****************************************************/
	protected void showLoading()
    {
		if(this.getLineCount() % 4000 == 0)
    		System.out.println(".");
    	if(this.getLineCount() % 100 == 0)
    		System.out.print(".");      
    }
	
	/****************************************************
	*   Display the results of the file/db reading/writing
	****************************************************/
	protected void printResults()
	{
		System.out.println("\nTXT file processed. Results:");
		System.out.println("-----------------------------");
		System.out.println(this.getLineCount() +" Line Count");
		System.out.println(this.getLineSuccessCount() + " Line Successes");
		System.out.println(this.getLineSkipCount() +" Line Skips");
		System.out.println(this.getLineErrorCount() +" Line Errors ");
		System.out.println("");
		mysqlHandle.printResults();
		System.out.println("");
	}
}