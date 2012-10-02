package CodeSample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ParseFileCSV extends ParseFile
{    
	/****************************************************
	   Constructor - Parses a 2 column (user_id, region_id) 
	   CSV file delimited by ','and inserts data into the 
	   DB
	****************************************************/
    public ParseFileCSV(String fileName) 
    {
    	//Open CSV File
    	if(!this.openFileHandler(fileName))
    		return;
    	
    	//Open connection to MySQL Server
    	if(!openHandleMySQL())
    		return;
    	
    	System.out.println("Populating user table...");
    	System.out.print("Populating user_region table...\n");
    	
    	//Read CSV File
    	this.readFile();    	

    	return;
    }
    
    /****************************************************
	*   Open a file
	****************************************************/
    public boolean openFileHandler(String fileName)
    {
    	try
        { 
            //Create BufferedReader to read CSV file
            br = new BufferedReader( new FileReader(fileName)) ;            
            st = null;  
            System.out.println(fileName + " opened...");
            return true;
        }
        catch(Exception e)
        {
        	System.out.println("Exception while reading CSV file: " + e);
        	return false;
        }
    }
    
    /****************************************************
	*   Close file
	****************************************************/
	public void closeFileHandler()
    {
		try
		{
			br.close();
			System.out.println("CSV file closed...\n");
			return;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

    	return;
    }	
	
	/****************************************************
	*   Token a line from a given file
	****************************************************/
	public void readLine(String strLine)
    {
		//Temporary variable holders for current line
		String userId = "";
		String regionId = "";
    	
		//Number of tokens read on a given line
        int tokenNumber = 0;
				
        //Break comma separated line using ","
        st = new StringTokenizer(strLine, ",");
        
        //Process line
        while(st.hasMoreTokens())
        {                    
            tokenNumber++;
            
            if(tokenNumber == 1)
            	userId = st.nextToken();  
            else if(tokenNumber == 2)
            	regionId = st.nextToken();
        }		
		
        //first line of CSV file has titles of columns, skip it 
    	if(!(userId.equals("user_id")) && !(regionId.equals("region_code")))
    	{ 
    		//Check if user_id field is empty
            if(userId.length() == 0)
            {                        	
            	this.incrementLineErrorCount();
            }
            else
            {
            	this.incrementLineSuccessCount();
            	
                //Check if region_code field is empty                        	
                if(regionId.length() == 0)
                {
                	regionId = "5000";
                }                
              
                //Insert userId into user table in MySQL DB
                this.insertUser(userId);                
                
                //Insert userId/region code into user_region table in MySQL DB
                this.insertUserRegion(userId, regionId);               
            }
    	} 
    	else
    	{
    		this.incrementLineSkipCount();
    	}

    	return;
    } 
	
	/****************************************************
	*   Insert row into user table
	****************************************************/
	private void insertUser(String userId)
	{
		mysqlHandle.insertUser(userId);
    	return;
	}
	
	/****************************************************
	*   Insert row into user_region table
	****************************************************/
	private void insertUserRegion(String userId, String regionId)
	{
		mysqlHandle.insertUserRegion(userId, regionId);
    	return; 
	}
}
