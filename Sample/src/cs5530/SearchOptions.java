package cs5530;
/*Search for a UUber Car
Search for useful feedbacks
Search recommended UUber Cars
Search for similar users*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SearchOptions
{
	private Connector2 con;
	private BufferedReader in;
	private String userLogin; //current active user
	private miscHelpers miscH;
	public SearchOptions(Connector2 con, String userLogin)
	{
		in = new BufferedReader(new InputStreamReader(System.in));
		this.con = con;
		this.userLogin = userLogin;
		miscH = new miscHelpers(con);
	}
	public void selectSearchOp()
	{
		in = new BufferedReader(new InputStreamReader(System.in));
		 String choice = null;
	        int c=0;
		 while(c != 5)
        {
			System.out.println("        Search Options     ");
			System.out.println("1. Search for a UUber Car");
			System.out.println("2. Seach for useful feedbacks");
			System.out.println("3. Seach for recommended UUber Cars");
			System.out.println("4. Seach for similar users");
			System.out.println("5. Go back\n");
			System.out.println("Choose an option (1-5): ");
			 
	       	try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) { /*ignore*/}
	       	try{
	       		c = Integer.parseInt(choice);
	       	}catch (Exception e)
	       	{
	       		continue;
	       	}
	       	if (c<1 | c>4)
	       		continue;
	       	switch(c) {
	       	 
		       	case 1: //Search for UUber Car
		       		searchCar();
		       		break;
		       	case 2: //Search for useful feedbacks
		       		break;
		       	case 3: //Search for recommended UUber Cars
		       		break;
		       	case 4: //Search for similar users
		       		break;
	       	}
        }
	}
	public boolean searchCar()
	{
		try 
		{
			String sql = null;
			String category = null;
			String city = null;
			String model = null;
			System.out.println("Type the category you want: ");
			category = in.readLine();
			System.out.println("Type the city you want: ");
			city = in.readLine();
			System.out.println("Type the car model you want: ");
			model = in.readLine();
			
			//if all strings are empty, print all UC cars
			if(category.replaceAll("\\s+","").isEmpty() && city.replaceAll("\\s+","").isEmpty() && model.replaceAll("\\s+","").isEmpty())
			{
				miscH.printUC();
			}
			else
			{
				printSearchUCOps(category, city, model);
			}
		}
		catch (Exception e) 
		{
			System.out.println("Failed to favorite a car.\n");
		}
		return false; // fail to favorite a car
	}
	/*
	 * Prints options for searching the conjunctive queries. Returns an option number that tells which
	 * sql statement to use when executing the search 
	 */
	public int printSearchUCOps(String category, String city, String model)
	{
		int ret = -1;
		String choice = null;
		int c;
		//Only by category
		if(!category.isEmpty() && city.isEmpty() && model.isEmpty())
		{
			System.out.println("Searching by category: " + category);
			ret =  1;
		}
		//Only by city
		else if(!city.isEmpty() && category.isEmpty() && model.isEmpty())
		{
			System.out.println("Searching by city: " + city);
			ret =  2;
		}
		//Only by model
		else if(!model.isEmpty() && category.isEmpty() && city.isEmpty())
		{
			System.out.println("Searching by model: " + model);
			ret =  3;
		}
		//Category and/or city
		else if(!category.isEmpty() && !city.isEmpty() && model.isEmpty())
		{
			String op1 = String.format("category = %s AND  city = %s", category, city);
        	String op2 = String.format("category = %s OR city = %s", category, city);
			System.out.println("Pick which search option you would like: ");
			System.out.println("4. " + op1);
			System.out.println("5. " + op2);
			System.out.println("Choose an option (4-5): ");
			
			while(true)
	       	{
				try {
					while ((choice = in.readLine()) == null || choice.length() == 0);
				} catch (IOException e1) {}
		       	try{
		       		c = Integer.parseInt(choice);
		       	}catch (Exception e)
		       	{
		       		continue;
		       	}
		       	if (c==4 | c==5)
		       	{
			       	switch(c) {       	 
				       	case 4: //category AND  city
				       		System.out.println("Searching by category: " + category + " AND city: " + city);
				       		ret = 4;
				       		break;
				       	case 5: //category OR city
				       		System.out.println("Searching by category: " + category + " OR city: " + city);
				       		ret = 5;
				       		break;
			       	}
			       	break;
		       	}
		       	else
		       		System.out.println("Not an option. Try again: ");
	       	}
		}
		//Category and/or model
		else if(!category.isEmpty() && !model.isEmpty() && city.isEmpty())
		{
			
		}
		//City and/or model
		else if(!city.isEmpty() && model.isEmpty() && category.isEmpty())
		{
			
		}
		//All
		else
		{
		}
		return ret;
	}
	public String makeSQLSearchUC(String category, String city, String model, int conjuction)
	{
		//Only by category
		
		//Only by city
		
		//Only by model
		
		//Category and/or city
		
		//Category and/or model
		return null;
		
	}
}