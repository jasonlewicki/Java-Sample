package CodeSample;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class ParseFileTXT extends ParseFile
{  	
	private SimpleDateFormat sdf;
	
	/****************************************************
	   Constructor - Parses a 3 column (user_id, event_type, 
	   event_date) TXT file delimited by '\t' and inserts
	    data into the DB
	****************************************************/
    public ParseFileTXT(String fileName) 
    {
    	//Open TXT File
    	if(!this.openFileHandler(fileName))
    		return;
    	
    	//Open connection to MySQL Server
    	if(!openHandleMySQL())
    		return;
    	
    	//Initialize SimpleDateFormat
    	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	System.out.println("Populating user table...");
    	System.out.print("Populating user_history table...\n");
    	
    	//Read TXT File
    	this.readFile();

    	return;
    }
    
    /****************************************************
	*   Open file
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
        	System.out.println("Exception while reading TXT file: " + e);
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
			System.out.println("TXT file closed...\n");
			return;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}		
    }
	
	/****************************************************
	*   Token line read from file
	****************************************************/
	public void readLine(String strLine)
    {		
		//Temporary variable holders for current line
		String userId = "";
		String eventType = "";
		String eventDate = "";
		
		//Number of tokens read on a given line
        int tokenNumber = 0;	
    	
        //Break comma separated line using "\t"
        st = new StringTokenizer(strLine, "\t");
       
        //Process line
        while(st.hasMoreTokens())
        {                    
            tokenNumber++;
            
            if(tokenNumber == 1)
            	userId = st.nextToken();  
            else if(tokenNumber == 2)
            	eventType = st.nextToken();
            else if(tokenNumber == 3)
            	eventDate = st.nextToken();
        }
		
        //first line of TXT file has titles of columns, skip it 
    	if(!(userId.equals("UserID")) && !(eventType.equals("MessageType")) && !(eventDate.equals("TimeStamp")))
    	{
    		//Check if UserID field is empty
            if(userId.length() == 0)
            {
            	this.incrementLineErrorCount();
            }
            //Check if MessageType field is empty
            else if(eventType.length() == 0)
            {
            	this.incrementLineErrorCount();
            }
            //Check if TimeStamp field is empty
            else if(eventDate.length() == 0)
            {
            	this.incrementLineErrorCount();
            }
            else
            {
            	this.incrementLineSuccessCount();
            	
            	//Insert userId into user table in MySQL DB
            	this.insertUser(userId);                
                
                //format eventDate
            	eventDate = this.formatDate(eventDate);
                eventDate = sdf.format(Long.parseLong(eventDate));
                
                //Insert userId/region code into user_region table in MySQL DB
                this.insertUserHistory(userId, eventType, eventDate); 
            }  
    	} 
    	else
    	{
    		this.incrementLineSkipCount();  		
    	}

    	return;
    }
	
	/****************************************************
	*   Format date for insertion into DB
	****************************************************/
	private String formatDate(String eventDate)
	{
		eventDate = sdf.format(Long.parseLong(eventDate));
		return eventDate;
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
	*   Insert row into user_history table
	****************************************************/
	private void insertUserHistory(String userId, String eventType, String eventDate)
	{
		mysqlHandle.insertUserHistory(userId, eventType, eventDate); 
		return;
	}
}
