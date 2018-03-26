package cs5530;
/*Search for a UUber Car
Search for useful feedbacks
Search recommended UUber Cars
Search for similar users*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SearchOptions
{
	private Connector2 con;
	private BufferedReader in;
	private String userLogin;
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
		 while(c != 4)
        {
			System.out.println("        Search Options     ");
			System.out.println("1. Search for a UUber Car"); //DONE
			System.out.println("2. Seach for useful feedbacks");
			System.out.println("3. Determine degree of seperation of two users"); //DONE
			System.out.println("4. Go back\n");
			System.out.println("Choose an option (1-4): ");
			 
	       	try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) {}
	       	try{
	       		c = Integer.parseInt(choice);
	       	}catch (Exception e)
	       	{
	       		continue;
	       	}
	       	if (c<1 | c>3)
	       		continue;
	       	switch(c) {
	       	 
		       	case 1: //Search for UUber Car
		       		searchCar();
		       		break;
		       	case 2: //Search for useful feedbacks
		       		break;
		       	case 3: //Determine degree of separation
		    		if(miscH.printUU())
		       		{
		    			System.out.println("One Degree Apart: Users 'A' and 'B' have both favorite at least one common UUber Car.");
		    			System.out.println("Two Degrees Apart: There exists a user 'C' where both users 'A' and 'B' are one degree away from.\n"
		    					+ ", but 'A' and 'B' are not one degree apart.");
		    			String[] twoUsers = getTwoUsers();
		    			if(!twoUsers[0].isEmpty() || !twoUsers[1].isEmpty())
		    			{
		    				if(!isOneDegreeApart(twoUsers))
		    				{
		    					if(!isTwoDegreesApart(twoUsers))
		    					{
			    					System.out.println("\nThere is no relation between users " + twoUsers[0] + " and " + twoUsers[1] + "\n");
		    					}
		    				}
		    				else
		    				{
		    					System.out.println("\nUser: " + twoUsers[0] + " and user: " + twoUsers[1] + " are ONE DEGREE apart."
		    							+ "\nMeaning the have favorited at least one common UUber Car\n");
		    				}
		    			}
		    			
		       		}
		       		break;
	       	}
        }
	}
	/**************UUber Car Search**************/
	/*
	 * Asks for input for category, city, model
	 */
	private void searchCar()
	{
		try 
		{
			String category = null;
			String city = null;
			String model = null;
			String choice = null;
			int c2 = 0;
			System.out.println("Choose a category: ");
			while(c2<1 | c2>3)
			 {
				 System.out.println("1. Economy");
				 System.out.println("2. Comfort");
				 System.out.println("3. Luxury\n");
				 System.out.println("Choose an option (1-3) (Press enter to exclude this from your search): ");
				 choice = in.readLine();
				 if(choice.compareTo("") == 0)
				 {
					 category = "";
					 break;
				 }
				 try{
					 c2 = Integer.parseInt(choice);

				 }catch (Exception e)
				 {
					 continue;
				 }
				 if (c2<1 | c2>3)
					 continue;
				 switch(c2) {
		       	 
			     	case 1: //Add a new UUber Car
			     		category = "Economy";
			       		break;
			       	case 2: //UUpdate a UUber Car
			       		category = "Comfort";
			       		break;
			       	case 3:
			       		category = "Luxury";
			       		break;
		       	}
	        }
			System.out.println("Type the city you want (Press enter to exclude this from your search): ");
			city = in.readLine();
			System.out.println("Type the car model you want (Press enter to exclude this from your search): ");
			model = in.readLine();
			//if all strings are empty, print all UC cars
			SearchUCOps(category, city, model);
		}
		catch (Exception e) 
		{
		}
	}
	/*
	 * Prints options for searching the conjunctive queries, sets up sql query, and calls the methods for the sql query executions
	 */
	private void SearchUCOps(String category, String city, String model)
	{
		String sql = pickSortingOp();
		String[] CCM = new String[3];
		String choice = null;
		int c;
		if(category.replaceAll("\\s+","").isEmpty() && city.replaceAll("\\s+","").isEmpty() && model.replaceAll("\\s+","").isEmpty())
		{
			System.out.println("Searching for all UUber Cars");
			executeUCSearchQ(CCM, sql, 0);
		}
		//Only by category
		else if(!category.isEmpty() && city.isEmpty() && model.isEmpty())
		{
			System.out.println("Searching by category: " + category);
			sql += " AND category = ?";
			CCM[0] = category;
			executeUCSearchQ(CCM, sql, 1);
		}
		//Only by city
		else if(!city.isEmpty() && category.isEmpty() && model.isEmpty())
		{
			System.out.println("Searching by city: " + city);
			sql += " AND address = ?";
			CCM[0] = city;
			executeUCSearchQ(CCM, sql, 1);
		}
		//Only by model
		else if(!model.isEmpty() && category.isEmpty() && city.isEmpty())
		{
			System.out.println("Searching by model: " + model);
			sql += " AND model = ?";
			CCM[0] = model;
			executeUCSearchQ(CCM, sql, 1);
		}
		//Category and/or city
		else if(!category.isEmpty() && !city.isEmpty() && model.isEmpty())
		{
			String op1 = String.format("category = %s AND  city = %s", category, city);
        	String op2 = String.format("category = %s OR city = %s", category, city);
			System.out.println("Pick which search option you would like: ");
			System.out.println("1. " + op1);
			System.out.println("2. " + op2);
			System.out.println("Choose an option (1-2): ");
			
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
		       	if (c==1 | c==2)
		       	{
			       	switch(c) {       	 
				       	case 1: //category AND  city
				       		System.out.println("Searching by category: " + category + " AND city: " + city);
				       		sql += " AND (category = ? AND address = ?)";
				       		break;
				       	case 2: //category OR city
				       		System.out.println("Searching by category: " + category + " OR city: " + city);
				       		sql += " AND (category = ? OR address = ?)";
				       		break;
			       	}
					CCM[0] = category;
					CCM[1] = city;
					executeUCSearchQ(CCM, sql, 2);
			       	break;
		       	}
		       	else
		       		System.out.println("Not an option. Try again: ");
	       	}
		}
		//Category and/or model
		else if(!category.isEmpty() && !model.isEmpty() && city.isEmpty())
		{
			String op1 = String.format("category = %s AND  model = %s", category, model);
        	String op2 = String.format("category = %s OR model = %s", category, model);
			System.out.println("Pick which search option you would like: ");
			System.out.println("1. " + op1);
			System.out.println("2. " + op2);
			System.out.println("Choose an option (1-2): ");
			
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
		       	if (c==1 | c==2)
		       	{
			       	switch(c) {       	 
				       	case 1: //category AND  model
				       		System.out.println("Searching by category: " + category + " AND model: " + model);
				       		sql += " AND (category = ? AND model = ?)";
				       		break;
				       	case 2: //category OR model
				       		System.out.println("Searching by category: " + category + " OR model: " + model);
				       		sql += " AND (category = ? OR model = ?)";
				       		break;
			       	}
					CCM[0] = category;
					CCM[1] = model;
					executeUCSearchQ(CCM, sql, 2);
			       	break;
		       	}
		       	else
		       		System.out.println("Not an option. Try again: ");
	       	}
		}
		//City and/or model
		else if(!city.isEmpty() && !model.isEmpty() && category.isEmpty())
		{
			String op1 = String.format("city = %s AND  model = %s", category, model);
        	String op2 = String.format("city = %s OR model = %s", category, model);
			System.out.println("Pick which search option you would like: ");
			System.out.println("1. " + op1);
			System.out.println("2. " + op2);
			System.out.println("Choose an option (1-2): ");
			
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
		       	if (c==1 | c==2)
		       	{
			       	switch(c) {       	 
				       	case 1: //city AND model
				       		System.out.println("Searching by city: " + city + " AND model: " + model);
				       		sql += " AND (address = ? AND model = ?)";
				       		break;
				       	case 2: //city OR model
				       		System.out.println("Searching by city: " + city + " OR model: " + model);
				       		sql += " AND (address = ? OR model = ?)";
				       		break;
			       	}
					CCM[0] = city;
					CCM[1] = model;
					executeUCSearchQ(CCM, sql, 2);
			       	break;
		       	}
		       	else
		       		System.out.println("Not an option. Try again: ");
	       	}
		}
		//All
		else
		{
			String op1 = String.format("category = %s AND  city = %s AND model = %s", category, city, model);
			String op2 = String.format("category = %s AND  city = %s OR model = %s", category, city, model);
			String op3 = String.format("category = %s OR  city = %s AND model = %s", category, city, model);
			String op4 = String.format("category = %s OR  city = %s OR model = %s", category, city, model);
			System.out.println("Pick which search option you would like: ");
			System.out.println("1. " + op1);
			System.out.println("2. " + op2);
			System.out.println("3. " + op3);
			System.out.println("4. " + op4);
			System.out.println("Choose an option (1-4): ");
			
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
		       	if (c >= 1 && c <= 4)
		       	{
			       	switch(c) {       	 
				       	case 1: //category AND  city AND model
				       		System.out.println("Searching by category: " + category + " AND city: " + city + " AND model: " + model);
				       		sql += " AND (category = ? AND address = ? AND  model = ?)";
				       		break;
				       	case 2: //category AND  city OR model
				       		System.out.println("Searching by category: " + category + " AND city: " + city + " OR model: " + model);
				       		sql += " AND (category = ? AND address = ? OR  model = ?)";
				       		break;
				       	case 3: //category OR city AND model
				       		System.out.println("Searching by category: " + category + " OR city: " + city + " AND model: " + model);
				       		sql += " AND (category = ? OR address = ? AND  model = ?)";
				       		break;
				       	case 4: //category OR city OR model
				       		System.out.println("Searching by category: " + category + " OR city: " + city + " OR model: " + model);
				       		sql += " AND (category = ? OR address = ? OR  model = ?)";
				       		break;
			       	}
					CCM[0] = category;
					CCM[1] = city;
					CCM[2] = model;
					executeUCSearchQ(CCM, sql, 3);
			       	break;
		       	}
		       	else
		       		System.out.println("Not an option. Try again: ");
	       	}
		}
	}
	/*
	 * Executes UC search query
	 */
	private void executeUCSearchQ(String[] CCM, String sql, int size)
	{
		try 
		{	
			sql += " ORDER BY s.AvScore DESC";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				for(int i = 0; i < size; i++)
				{
					pstmt.setString(i+1, CCM[i]);
				}
				ResultSet result = pstmt.executeQuery();
				printUCSearchResult(result);
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
	}
	/*
	 * Prints result of UC search
	 */
	private void printUCSearchResult(ResultSet result)
	{
		try
		{
			if(result.isBeforeFirst())
			{
				System.out.println("Search results:");
				while(result.next())
				{
					System.out.println("vin: " + result.getString("vin"));
					System.out.println("\t" + "Category: " + result.getString("category") 
											+ "    Make: " + result.getString("make")
											+ "    Model: " + result.getString("model")
											+ "    Year: " + result.getString("year")
											+ "    Owner: " + result.getString("login")
											+ "    City: " + result.getString("address")
											+ "    Average Score: " + result.getString("avScore"));
				}
				System.out.println();
			}
			else
			{
				System.out.println("There are no cars with your specifications.");
			}
		}
		catch(SQLException e) {}
	}
	/*
	 * Allows user to pick whether to have only trusted users in the average UUber car score search or not
	 */
	private String pickSortingOp()
	{
		System.out.println("Pick the ordering of your search: ");
		System.out.println("1. By average score of all feedbacks");
		System.out.println("2. By average score of feedbacks from users you trust");
		System.out.println("Choose an option (1-2): ");
		String sql = null;
		String choice = null;
		int c;
		while(true)
       	{
			try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) {}
	       	try{
	       		c = Integer.parseInt(choice);
	       	}catch (Exception e)
	       	{
	       		System.out.println("Not an option. Try again: ");
	       		continue;
	       	}
	       	if (c==1 | c==2)
	       	{
		       	switch(c) {       	 
			       	case 1: 
			       		sql = "SELECT UC.vin, category, year, UC.login, make, model, address, s.avScore " + 
			       				"FROM Ctypes, IsCtypes, UU, UC, " + 
			       				"(SELECT UC.vin, AVG(Feedback.score) as avScore FROM UC LEFT OUTER JOIN Feedback ON UC.vin = Feedback.vin GROUP BY UC.vin) as s " + 
			       				"WHERE UC.vin = IsCtypes.vin AND IsCtypes.tid = Ctypes.tid AND UU.login = UC.login AND UC.vin = s.vin";
			       		break;
			       	case 2: //only trusted users calculated in score
			       		sql = "SELECT UC.vin, category, year, UC.login, make, model, address, s.avScore " + 
			       				"FROM Ctypes, IsCtypes, UU, UC, " + 
			       				"(SELECT UC.vin, AVG(Feedback.score) as avScore FROM Trust, UC LEFT OUTER JOIN Feedback ON UC.vin = Feedback.vin WHERE Trust.login2 = Feedback.login AND Trust.login1 = '" + userLogin
			       				+ "' AND Trust.isTrusted = 1 GROUP BY UC.vin) as s " + 
			       				"WHERE UC.vin = IsCtypes.vin AND IsCtypes.tid = Ctypes.tid AND UU.login = UC.login AND UC.vin = s.vin";
			       		break;
		       	}
		       	break;
	       	}
	       	else
	       		System.out.println("Not an option. Try again: ");
       	}
		return sql;
	}
	
	/**************Useful Feedbacks Search***************/

	
	/**************Degree of Separation Search****************/
	/*
	 * Prompts the user to input two users, returns those users in an array
	 */
	private String[] getTwoUsers()
	{
		String[] twoUsers = new String[2];
		try 
		{
			String user1 = null;
			String user2 = null;
			System.out.println("Type in the login name of the first user you want to compare: ");
			while((user1 = in.readLine()) == null || user1.length() == 0);
			if(miscH.validUser(user1))
			{
				System.out.println("Type in the login name of the second user you want to compare: ");
				while((user2 = in.readLine()) == null || user2.length() == 0);
				if(miscH.validUser(user2) && user2.compareTo(user1) != 0)
				{
					twoUsers[0] = user1;
					twoUsers[1] = user2;
				}
				else
				{
					System.out.println("Not a valid user");
					twoUsers[0] = null;
					twoUsers[1] = null;
				}
			}
			else
			{
				System.out.println("Not a valid user");
				twoUsers[0] = null;
				twoUsers[1] = null;
			}
		}
		catch (Exception e) 
		{
		}
		return twoUsers;
	}
	
	/*
	 * Determines if two users have a common favorite car/one degree apart
	 */
	private boolean isOneDegreeApart(String[] twoUsers)
	{
		try 
		{
			String sql = "SELECT F1.vin, F1.login as login1, F2.login as login2 FROM Favorites AS F1 JOIN Favorites AS F2 "
					+ "WHERE F1.login != F2.login AND F1.vin = F2.vin AND F1.login = ? AND F2.login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, twoUsers[0]);
				pstmt.setString(2, twoUsers[1]);
				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					return true;
				}
				else
				{
					return false;
				}
			} 
			catch(SQLException e) {System.out.println(e.getMessage());}
		}
		catch (Exception e) {System.out.println(e.getMessage());}
		return false;
	}
	
	/*
	 * Determines if two users have a common user 'C' where they are both one degree apart from the user 'C'/two degrees apart
	 */
	private boolean isTwoDegreesApart(String[] twoUsers)
	{
		List<String> users = miscH.getAllUU();
		String[] twoUTemp1 = new String[2];
		String[] twoUTemp2 = new String[2];
		twoUTemp1[0] = twoUsers[0];
		twoUTemp2[0] = twoUsers[1];
		for(int j = 0; j < users.size(); j++)
		{
			String temp = users.get(j);
			if(temp != twoUTemp1[0] && temp != twoUTemp2[0])
			{
				twoUTemp1[1] = temp;
				twoUTemp2[1] = temp;
				if(isOneDegreeApart(twoUTemp1) && isOneDegreeApart(twoUTemp2))
				{
					System.out.println("\nUser: " + twoUsers[0] + " and user: " + twoUsers[1] + " are TWO DEGREES apart."
							+ "\nMeaning there is a user " + temp + " where " + twoUsers[0] + " and " + twoUsers[1]
							+ " are both one degree apart from " + temp + " but " + twoUsers[0] + " and " + twoUsers[1]
							+ " are not one degree apart.\n");
					return true;
				}
			}
		}
		return false;
	}
}