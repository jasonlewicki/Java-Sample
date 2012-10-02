package CodeSample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class CodeSample 
{
	public static void main(String[] args) 
	{
		while(true)
		{			
			//UI		
			System.out.println("CodeSample");
			System.out.println("------------------------------------------------");
			System.out.println("1: Import a CSV file into user/user_region tables");
			System.out.println("2: Import a TXT file into user/user_history tables");
			System.out.println("3: Display the number of users shown an advertisement a specific number of times");
			System.out.println("4: Display the impression count by region");
			System.out.println("5: Display the attributed activity count");
			System.out.println("0: Exit");
			System.out.println("------------------------------------------------");
			System.out.println("Enter your choice: ");			
			 
			//get user input
			int inputInt = checkInputInt();			
			
			switch (inputInt) 
			{
				case 1: //Import a CSV file into user/user_region tables
					importCSVFile();
					break;
				case 2: //Import a TXT file into user/user_history tables  
					importTXTFile();
					break;
				case 3: //Display the number of users shown an advertisement a specific number of times
					displayNumAdvertToUsers();
					break;
				case 4: //Display the impression count by region
					displayImpressionCount();
					break;
				case 5: //Display the attributed activity count
					displayAttributedActivities();
					break;			   
				case 0: //Exit
					System.exit(0);
			}
		}		
	}
	
	
	/****************************************************
	*   Check for valid integer input
	****************************************************/
	private static int checkInputInt() 
	{
		Scanner sc = new Scanner(System.in);
		String _inputString;
		int _inputInt = -1;
		
		while (_inputInt == -1) 
		{
			_inputString = sc.next();
			
			try 
			{
				_inputInt = Integer.parseInt(_inputString);
				
				if(!(_inputInt >=0 && _inputInt <= 6))
				{
					System.out.println("Enter a number 1-6, 0 to exit: ");
					_inputInt = -1;
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println("Enter a number 1-6, 0 to exit: ");
			}
		}
		return _inputInt;		
	}
	
	
	/****************************************************
	*   Check for valid string input
	****************************************************/
	private static String checkInputString() 
	{
		Scanner sc = new Scanner(System.in);
		String _inputString = "";
		
		while (_inputString.length() == 0) 
		{			
			_inputString = sc.nextLine();
			
			if(_inputString.length() == 0)
				System.out.println("Length of input must be greater than 0: ");			
		}
		return _inputString;		
	}
	

	/****************************************************
	*   Import a CSV file into user/user_region tables
	****************************************************/
	private static void importCSVFile()
	{
		System.out.println("Enter the directory and filename of a CSV file e.g.(C:/data.csv):");
		
		//get user input
		String inputString = checkInputString();	
		
		//Generate new object to parse CSV file and insert into DB
		@SuppressWarnings("unused")
		ParseFileCSV csvFile = new ParseFileCSV(inputString);	
		
		return;
	}
	
	
	/****************************************************
	*   Import a TXT file into user/user_region tables
	****************************************************/
	private static void importTXTFile()
	{
		System.out.println("Enter the directory and filename of a TXT file e.g.(C:/data.txt:");
		
		//get user input
		String inputString = checkInputString();	
		
		//Generate new object to parse TXT file and insert into DB
		@SuppressWarnings("unused")
		ParseFileTXT txtFile = new ParseFileTXT(inputString);
		
		return;
	}
	
	
	/****************************************************
	*   Display the number of users shown an advertisement 
	*   a specific number of times
	****************************************************/
	private static void displayNumAdvertToUsers()
	{	
		//For pulling strings from the ResultSet
		String dbResultNum;
		String dbResultTotal;
		
		//Open MySQL connection
		MySQL mysqlHandle;
		
		try
		{
			//declare DXMySQL handle
			mysqlHandle = new MySQL();
			
			System.out.println("Selecting data from table...");
			
			//Query String
			String mysqlQuery = "SELECT count, count(*) as total FROM (SELECT user_id, count(*) as count FROM user_history WHERE event_type='impression' GROUP BY user_id) as hi GROUP BY count;";
			
			//Select data
			ResultSet rs = mysqlHandle.selectData(mysqlQuery);
			 
			System.out.println("Displaying records...");
			
			System.out.println("\nNumber of ads they saw\t\tNumber of users who saw that number of ads");
			
			//Output results
			while (rs.next()) 
			{
				dbResultNum = rs.getString(1);
				dbResultTotal = rs.getString(2);
				System.out.println(dbResultNum + "\t\t\t\t" + dbResultTotal);
			}
			
			//Close connection to MySQL DB
			mysqlHandle.close();   
		}
		catch(MySQLOpenConnectionException e)
		{
			 System.out.println(e);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return;
	}
	
	
	/****************************************************
	*   Display the impression count by region
	****************************************************/
	private static void displayImpressionCount()
	{		
		//Open MySQL connection
		MySQL mysqlHandle;
		
		//String to be used for queries
		String mysqlQuery;
		
		try
		{
			//declare DXMySQL handle
			mysqlHandle = new MySQL();
			
			System.out.println("Selecting data from table...");
			
			//MySQL to select every region
			mysqlQuery = "SELECT region_id, name FROM region";			
			//Select data
			ResultSet rsRegion = mysqlHandle.selectData(mysqlQuery);
			
			//MySQL to count number of regions
			mysqlQuery = "SELECT count(region_id) as count FROM region";			
			//Select data
			ResultSet rsRegionCount = mysqlHandle.selectData(mysqlQuery);
			
			//initialize arrays to hold the data and counts
			rsRegionCount.next();
			int regionCount = Integer.parseInt(rsRegionCount.getString(1));
			ResultSet[] rsRegionArray = new ResultSet[regionCount+1];
			int[] rsRegionArraySize = new int[regionCount+1];
			ResultSet tempSet;
			
			//Output table name
			System.out.print("Number of Ads They Saw\t\t");
			
			//Get each region's data set
			for(int i = 1; i<= regionCount; i++)
			{	  
				rsRegion.next();
				
				//Print title
				System.out.print(rsRegion.getString(2)+"\t");
				
				//Pull region data set
				mysqlQuery = "SELECT count, count(*) as total FROM (SELECT user_history.user_id, user_region.region_id, count(*) as count FROM user_history LEFT JOIN user_region ON user_history.user_id=user_region.user_id WHERE event_type='impression' AND user_region.region_id='" + rsRegion.getString(1) + "' GROUP BY user_history.user_id) as temp_table GROUP BY count;";
				rsRegionArray[i] = mysqlHandle.selectData(mysqlQuery);
				
				//Pull region data count
				mysqlQuery = "SELECT count(*) as count FROM(SELECT count, count(*) as total FROM (SELECT user_history.user_id, user_region.region_id, count(*) as count FROM user_history LEFT JOIN user_region ON user_history.user_id=user_region.user_id WHERE event_type='impression' AND user_region.region_id='" + rsRegion.getString(1) + "' GROUP BY user_history.user_id) as temp_table GROUP BY count) as temp_table;";
				tempSet = mysqlHandle.selectData(mysqlQuery);
				tempSet.next();
				rsRegionArraySize[i] = Integer.parseInt(tempSet.getString(1));
			}
			
			System.out.print("\n");
			
			//independently increments each region and marks how close to the end of a data set we are
			int[] rsRegionArraySizeMarker = new int[regionCount+1];
			Arrays.fill(rsRegionArraySizeMarker, 1);
			
			//Output data
			boolean dataLeft = true;
			int dataLeftChecker;
			int lineCounter=1;
			while(dataLeft == true)
			{  
				System.out.print(lineCounter + "\t\t\t\t");
				
				for(int i = 1; i <= regionCount; i++)
				{					
					if(rsRegionArraySizeMarker[i] < rsRegionArraySize[i])
					{
						rsRegionArray[i].next();
						if(lineCounter == Integer.parseInt(rsRegionArray[i].getString(1)))
						{
							System.out.print(rsRegionArray[i].getString(2) + "\t\t");
							rsRegionArraySizeMarker[i]++;
						}
						else
						{
							System.out.print("0\t\t");
							rsRegionArray[i].previous();
						}						
					}
					else
					{
						System.out.print("0\t\t");						
					}
				}
				
				System.out.print("\n");
				
				lineCounter++;
				
				dataLeftChecker = 0;				
				for(int i = 1; i<= regionCount; i++)
				{
					if(rsRegionArraySizeMarker[i] == rsRegionArraySize[i])
					{
						dataLeftChecker++;
					}
				}
				if(dataLeftChecker == regionCount)
				{
					dataLeft = false;
				}
			}

			//Close connection to MySQL DB
			mysqlHandle.close();   
		}
		catch(MySQLOpenConnectionException e)
		{
			 System.out.println(e);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return;
	}

	
	/****************************************************
	*   Display the attributed activity count
	****************************************************/
	private static void displayAttributedActivities()
	{
		//Open MySQL connection
		MySQL mysqlHandle;
		
		//String to be used for queries
		String mysqlQuery;
		
		try
		{
			//declare DXMySQL handle
			mysqlHandle = new MySQL();
			
			System.out.println("Selecting data from table...");
			
			//MySQL to select data
			mysqlQuery = "SELECT count(*) FROM user_history AS a INNER JOIN user_history AS b ON a.user_id=b.user_id WHERE a.event_type='activity' AND b.event_type='impression' AND a.event_date>b.event_date;";			
			
			//Select data
			ResultSet rsData = mysqlHandle.selectData(mysqlQuery);   
			
			//Output data
			rsData.next();
			System.out.println("Attributed activity count: " + rsData.getString(1));

			//Close connection to MySQL DB
			mysqlHandle.close();   
		}
		catch(MySQLOpenConnectionException e)
		{
			 System.out.println(e);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return;
	}	
}
