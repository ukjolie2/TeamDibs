package cs5530;
/* 
This part is used when the user has successfully logged in
Driver Registration
Driver Options (class) only valid if user is registered as driver
Record a ride
Favorite a Car
Review a UUber Car
Review a feedback record
Review a user
Search Options (class)
View top awards (class)
*/

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserOptions
{
	private Connector2 con;
	private BufferedReader in;
	private String userLogin; //current active user
	private String userName; //current active user name
	private miscHelpers miscH;
	public UserOptions(Connector2 con, String userLogin, String userName)
	{
		in = new BufferedReader(new InputStreamReader(System.in));
		this.con = con;
		this.userLogin = userLogin;
		this.userName = userName;
		miscH = new miscHelpers(con);
	}
	public void selectUserOp()
	{
		 
		 String choice = null;
	        int c=0;
		 while(c != 11)
         {
			 
			 System.out.println("        Welcome UUber User!     ");
			 System.out.println("1. Register as a UUber Driver"); //DONE
			 System.out.println("2. UUber Driver Options"); //DONE / Only works when registered as a UUber Driver
			 System.out.println("3. Reserve a ride"); //DONE
			 System.out.println("4. Record a ride");
			 System.out.println("5. Favorite a Car"); //DONE
			 System.out.println("6. Review a UUber Car"); //DONE
			 System.out.println("7. View UUber Cars and reviews"); //DONE / View UUBer Cars, then feed back and then review feedback
			 System.out.println("8. Review a user"); //View UUber users then review user
			 System.out.println("9. Search");
			 System.out.println("10. View top awards");
			 System.out.println("11. Go back\n"); //DONE
			 System.out.println("Choose an option (1-11): ");
			 
        	 try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) { /*ignore*/
			}
        	 try{
        		 c = Integer.parseInt(choice);
        	 }catch (Exception e)
        	 {
        		 continue;
        	 }
        	 if (c<1 | c>10)
        		 continue;
        	 switch(c) {
        	 case 1: //Register Driver
        		 createDriver();
        		 break;
        	 case 2: //Driver options
        		 if(isDriver())
        		 {
        			 DriverOptions driverOps = new DriverOptions(con, userLogin);
        			 driverOps.selectDriverOp();
        		 }
        		 else
        		 {
        			 System.out.println("Please register as a UUber Driver first!\n");
        		 }
        		 break;
        	 case 3: //Reserve a ride
        		 reserveRide();
        		 break;
        	 case 4: //Record a ride
        		 recordRide();
        		 break;
        	 case 5: //Favorite a car
        		 if(miscH.printUC())
        		 {
        			 printFavorites();
        			 addNewFavorite();
        		 }
        		 break;
        	 case 6: //Review a car
        		 if(miscH.printUC())
        			 reviewUC();
        		 break;
        	 case 7: //Review a feedback record
        		 if(miscH.printUC())
        			 rateFBUsefulness();
        		 break;
        	 case 8: //Review a user
        		 break;
        	 case 9: //Search options
        		 SearchOptions searchOps = new SearchOptions(con, userLogin);
        		 searchOps.selectSearchOp();
        		 break;
        	 case 10: //View top awards
        		 TopAwardsOptions topOps = new TopAwardsOptions(con);
        		 topOps.selectTopOp();
        		 break;
        	 }
         }
	}
	
	/*******DRIVER**********/
	/*
	 * Creates a UUber Driver in UD table with current user's login
	 */
	public boolean createDriver()
	{
		try 
		{
			String sql=null;
			String confirmation = null;
			System.out.println("Confirm you would like to register as an UUber Driver as: ");
			System.out.println("Login: " + userLogin);
			System.out.println("Name: " + userName);
			System.out.println("yes/no?");
			while(true)
			{
				confirmation = in.readLine();
				if(confirmation.equals("yes") || confirmation.equals("no"))
					break;
			}
			if(confirmation.equals("yes"))
			{
				sql = "INSERT INTO UD(login, name) VALUES(?,?)";
				try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
				{
					pstmt.setString(1,  userLogin);
					pstmt.setString(2, userName);
					int success = pstmt.executeUpdate();
					if(success == 1)
					{
						System.out.println("You have been registered as a UUber Driver!\n");
						return true; // success with registering as driver
					}
	
				} 
				catch(SQLException e) 
				{
					System.out.println("Registration failed. You are already registered as a driver\n");
				}
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to create driver
	}
	
	/*
	 * Checks if current active user is a registered driver
	 */
	public boolean isDriver()
	{
		try 
		{
			String sql=null;
				 
			sql = "SELECT login FROM UD WHERE login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  userLogin);

				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return true; // is a driver
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; //not a driver
	}
	
	/*******FAVORITES*********/
	/*
	 * Prints users favorites
	 */
	public void printFavorites()
	{
		try 
		{
			String sql=null;
			
			sql = "SELECT Favorites.vin, Favorites.fvdate, UC.category, UC.year, Ctypes.make, Ctypes.model, UC.login "
					+ "FROM Favorites, UC, Ctypes, IsCtypes "
					+ "WHERE Favorites.vin = UC.vin AND Favorites.vin = IsCtypes.vin AND IsCtypes.tid = Ctypes.tid AND Favorites.login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  userLogin);
				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("Your Favorites: ");
					while(result.next())
					{
						System.out.println("vin: " + result.getString("vin"));
						System.out.println("\t" + "Category: " + result.getString("category") 
											+ "    Make: " + result.getString("make")
											+ "    Model: " + result.getString("model")
											+ "    Year: " + result.getString("year")
											+ "    Owner: " + result.getString("login"));
					}
				}
				else
					System.out.println("You have no favorites.");

			} 
			catch(SQLException e) 
			{
			}
		}
		catch (Exception e) 
		{
		}
	}
	/*
	 * Adds car to users favorites
	 */
	public boolean addNewFavorite()
	{
		try 
		{
			String sql=null;
			String choice = null;
			System.out.println("Type in the vin of the vehicle you would like to favorite: ");
			while(choice == null)
			{
				choice = in.readLine();
				try
				{
					 Integer.parseInt(choice);
				}
				catch (Exception e) 
				{
					System.out.println("Not a valid vin, try again: ");
					choice = null;
				}
			}
			sql = "INSERT INTO Favorites(vin, login, fvdate) VALUES(?,?,?)";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				pstmt.setString(1,  choice);
				pstmt.setString(2, userLogin);
				pstmt.setTimestamp(3, date);
				int success = pstmt.executeUpdate();
				if(success == 1)
				{
					System.out.println("You have successfully favorited a car!\n");
					return true; // success in favorite a car
				}

			} 
			catch(SQLException e) 
			{
				System.out.println("Failed to favorite a car.\n");
			}
		}
		catch (Exception e) 
		{
			System.out.println("Failed to favorite a car.\n");
		}
		return false; // fail to favorite a car
	}
	
	/*******FEEDBACK*********/
	/*
	 * Review a UUber car with short text, score, and date
	 */
	public boolean reviewUC()
	{
		try 
		{
			String sql=null;
			String vin = null;
			String text = null;
			String score = null;
		
			System.out.println("Type in the vin of the vehicle you would like to review: ");
			while(vin == null)
			{
				vin = in.readLine();
				try
				{
					 Integer.parseInt(vin);
					 if(!miscH.validVin(vin))
					 {
						 vin = null;
						 System.out.println("Not a valid vin, try again: ");
					 }
				}
				catch (Exception e) 
				{
					System.out.println("Not a valid vin, try again: ");
					vin = null;
				}
			}
			if(!hasReviewed(vin))
			{
				System.out.println("Type in a short comment for this car: ");
				text = in.readLine();
				System.out.println("Type in a score for this car (0-10): ");
				while(score == null)
				{
					score = in.readLine();
					try
					{
						 int scoreInt = Integer.parseInt(score);
						 if(scoreInt < 0 | scoreInt > 10)
						 {
							 score = null;
							 System.out.println("Not a valid score, try again: ");
						 }
					}
					catch (Exception e) 
					{
						System.out.println("Not a valid score, try again: ");
						score = null;
					}
				}
				sql = "INSERT INTO Feedback(text, score, vin, login, fbdate) VALUES(?,?,?,?,?)";
				try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
				{
					java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
					pstmt.setString(1,  text);
					pstmt.setString(2, score);
					pstmt.setString(3, vin);
					pstmt.setString(4, userLogin);
					pstmt.setTimestamp(5, date);
					int success = pstmt.executeUpdate();
					if(success == 1)
					{
						System.out.println("You have successfully reviewed the car!\n");
						return true; // success in reviewing a car
					}
	
				} 
				catch(SQLException e) 
				{
					System.out.println("Failed to review the car.\n");
				}
			}
			else
			{
				System.out.println("You have already reviewed this car.\n");
			}
		}
		catch (Exception e) 
		{
			System.out.println("Failed to review the car.\n");
		}
		return false; // fail to review a car
	}
	/*
	 * Checks if user has reviewed the given UUber car already.
	 */
	public boolean hasReviewed(String vin)
	{
		try 
		{		 
			String sql = "SELECT * FROM Feedback WHERE login = ? AND vin = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  userLogin);
				pstmt.setString(2, vin);
				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return true; // has already reviewed this car
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false; //has not reviewed this car
	}
	/*
	 * Views UUber cars. User picks which car to view feedback of, then has the option of reviewing those feedbacks
	 * (Usefulness rating)
	 */
	public boolean rateFBUsefulness()
	{
		try 
		{
			String sql=null;
			String vin = null;
			String fid = null;
			String rating = null;
			System.out.println("Type in the vin of the vehicle you would like see the feedback of: ");
			while(vin == null)
			{
				vin = in.readLine();
				try
				{
					 Integer.parseInt(vin);
					 if(!miscH.validVin(vin))
					 {
						 vin = null;
						 System.out.println("Not a valid vin, try again: ");
					 }
				}
				catch (Exception e) 
				{
					System.out.println("Not a valid vin, try again: ");
					vin = null;
				}
			}
			if(miscH.printUCReviews(vin))
			{
				String answer = null;
				System.out.println("Would you like to rate a feedback? y/n");
				while(answer == null)
				{
					answer = in.readLine();
					if(answer.compareTo("y") == 0)
					{
						System.out.println("Type in the fid of the feedback you would like to review the usefulness of: ");
						while(fid == null)
						{
							fid = in.readLine();
							try
							{
								 Integer.parseInt(fid);
								 sql = "SELECT * FROM Feedback WHERE fid = ? AND vin = ?";
								 try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
								 {
									 pstmt.setString(1,  fid);
									 pstmt.setString(2, vin);
									 ResultSet result = pstmt.executeQuery();
									 if(!result.isBeforeFirst())
									 {
										 System.out.println("This fid does not correspond with the selected vehicle. Try again: ");
										 fid = null;
									 }
								} 
							}
							catch (Exception e) 
							{
								System.out.println("Not a valid fid, try again: ");
								fid = null;
							}
						}
						if(!isOwnFeedback(fid))
						{
							System.out.println("Type in the rating you would like to give to this feedback (0 = useless, 1 = useful, 2 = very useful): ");
							while(rating == null)
							{
								rating = in.readLine();
								try
								{
									 int ratingInt = Integer.parseInt(rating);
									 if(ratingInt < 0 | ratingInt > 2)
									 {
										 System.out.println("Not a valid rating. Try again: ");
										 rating = null;
									 }
								}
								catch (Exception e) 
								{
									System.out.println("Not a valid rating, try again: ");
									rating = null;
								}
							}
							sql = "INSERT INTO Rates(login, fid, rating) VALUES(?,?,?)";
							try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
							{
								pstmt.setString(1,  userLogin);
								pstmt.setString(2, fid);
								pstmt.setString(3, rating);
								int success = pstmt.executeUpdate();
								if(success == 1)
								{
									System.out.println("You have successfully rated the feedback!\n");
									return true; // success in rating feedback
								}
				
							} 
							catch(SQLException e) 
							{
								System.out.println("Failed to rate the feedback. You have already rated this.\n");
							}
						}
						else
							System.out.println("You cannot rate your own feedback!\n");
					}
					else if(answer.compareTo("n") == 0)
					{
						break;
					}
					else
					{
						System.out.println("Not a valid answer. Try again: ");
						answer = null;
					}
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("Failed to rate the feedback.\n");
		}
		return false; // fail to rate the feedback
		
	}
	/*
	 * Checks if feedback user is rating is their own feedback
	 */
	public boolean isOwnFeedback(String fid)
	{
		try 
		{		 
			String sql = "SELECT * FROM Feedback WHERE fid = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, fid);
				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					if(userLogin.compareTo(result.getString("login")) == 0)
						return true; // is own feedback
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false; //is not own feedback
	}
	
	/*******DATE**********/
	public static Date parseDate(String date) {
	     try {
	         return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	     } catch (ParseException e) {
	         return null;
	     }
	  }
	/*******RECORD A RIDE*********/
	public void recordRide() {
		int c = 0;
		List<ReserveObj> records = new ArrayList<ReserveObj>();
		System.out.println("        Record a Ride!     ");
		while(c != 2)
        {
			 String choice = null;
			 System.out.println("1. Record Ride");
			 System.out.println("2. Finish"); 
			 System.out.println("Choose an option (1-2): ");
			 
       	 try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) {
			}
       	 try{
       		 c = Integer.parseInt(choice);
       	 }catch (Exception e)
       	 {
       		 continue;
       	 }
       	 if(c == 1) {
       		ReserveObj temp = new ReserveObj();
       		temp.cost  = -1;
       		temp.start  = -1;
       		temp.end  = -1;
       		System.out.println("Please type in the date had the ride (yyyy-mm-dd):");
       		while(temp.date == null) {
				try
				{
					temp.date = in.readLine();
					Date test = parseDate(temp.date);
					if(test == null) {
						System.out.println("Error: Please type in the date you had the ride (yyyy-mm-dd):");
						temp.date = null;
					}
				}
				catch (Exception e) 
				{
				}
       		}
       		System.out.println("Please type in what hour the ride started (only one integer 0 - 22):");
       		while(temp.start == -1) {
				try
				{
					temp.start = Integer.parseInt(in.readLine());
					if(temp.start > 22 || temp.start < 0) {
						System.out.println("Error: Please type in what hour you'd like to reserve the car (only one integer 0 - 22):");
						temp.start = -1;
					}
					
				}
				catch (Exception e) 
				{
				}
       		}
       		System.out.println("Please type in what hour you'd like your car ride ended (only one integer 1 - 23)\n"
       				+ "end hour must be after start hour of the same day: ");
       		while(temp.end == -1) {
				try
				{
					temp.end = Integer.parseInt(in.readLine());
					if(temp.end > 23 || temp.end < 1 || temp.end < temp.start) {
						System.out.println("Error: Please type in what hour you'd like your car reservation to end (only one integer 1 - 23)\n"
			       				+ "end hour must be after start hour of the same day: ");
						temp.start = -1;
					}
					else {
						int startInt = temp.start;
						int endInt = temp.end;
						temp.cost = endInt - startInt;
						records.add(temp);
					}
				}
				catch (Exception e) 
				{
				}
       		}
       		List<String> allowedVins = new ArrayList<String>();
       		String sql = "select * from UC U WHERE U.vin NOT IN \r\n" + 
       				"(select vin from Ride R WHERE (R.fromHour >= ? AND R.fromHour <= ? AND R.date = ?) OR (R.toHour >= ? AND R.toHour <= ? AND R.date = ?))\r\n" + 
       				"AND U.vin in (select vin from UC U2 WHERE U2.login in \r\n" + 
       				"(select login from Available A where A.pid in \r\n" + 
       				"(select pid from Period P WHERE P.fromHour <= ? AND P.toHour >= ?)));";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  String.valueOf(temp.start));
				pstmt.setString(2, String.valueOf(temp.end));
				pstmt.setString(3, temp.date);
				pstmt.setString(4, String.valueOf(temp.start));
				pstmt.setString(5, String.valueOf(temp.end));
				pstmt.setString(6, temp.date);
				pstmt.setString(7, String.valueOf(temp.start));
				pstmt.setString(8, String.valueOf(temp.end));
				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("Search results:");
					while(result.next())
					{
						System.out.println("vin: " + result.getString("vin"));
						System.out.println("\t" + "Category: " + result.getString("category")
												+ "    Year: " + result.getString("year"));
						allowedVins.add(result.getString("vin"));
					}
					System.out.println();
				}
				else
				{
					System.out.println("There are no cars available");
					c = 2;
					continue;
				}
			} 
			catch(SQLException e) {
				System.out.println("Fail");
			}
       		System.out.println("Please type in the VIN of the car you'd like to reserve");
       		while(temp.vin == null) {
				try
				{
					temp.vin = in.readLine();
					if(!allowedVins.contains(temp.vin)) {
						System.out.println("That car isn't available, please try again: ");
						temp.vin = null;
					}
				}
				catch (Exception e) 
				{
				}
       		}
       	 }
       }
		if(records.size() > 0) {
			String confirmation = "a";
			System.out.println("Confirm these records:");
			for(int i = 0; i < records.size(); i++) {			
				System.out.println("VIN:" + records.get(i).vin);
				System.out.println("Start Hour:" + String.valueOf(records.get(i).start));
				System.out.println("End Hour:" + String.valueOf(records.get(i).end));
				System.out.println("Date:" + records.get(i).date);
				System.out.println("Cost:" + String.valueOf(records.get(i).cost));
			}
			
			System.out.println("yes/no?");
			while(true)
			{
				try {
					confirmation = in.readLine();
				}
				catch(Exception e){}
				if(confirmation.equals(null)) {
					confirmation = "a";
				}
				if(confirmation.equals("yes") || confirmation.equals("no"))
					break;
			}
			if(confirmation.equals("yes")) {
				for(int i = 0; i < records.size(); i++) {
					String sql = "INSERT INTO Ride(cost, date, login, vin, fromHour, toHour) VALUES(?,?,?,?,?,?)";
					try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql))
					{
						pstmt2.setString(1,  String.valueOf(records.get(i).cost));
						pstmt2.setString(2, records.get(i).date);
						pstmt2.setString(3,  userLogin);
						pstmt2.setString(4, records.get(i).vin);
						pstmt2.setString(5,  String.valueOf(records.get(i).start));
						pstmt2.setString(6,  String.valueOf(records.get(i).end));
						pstmt2.executeUpdate();
					} 
					catch(SQLException e) 
					{
						System.out.println("Failed to insert into Ride\n");
					}
				}
			}
		}
	}
	/*******RESERVE A RIDE*********/
	public void reserveRide()
	{
		int c = 0;
		List<ReserveObj> reservations = new ArrayList<ReserveObj>();
		System.out.println("        Reserve a Ride!     ");
		while(c != 2)
        {
			 String choice = null;
			 System.out.println("1. Reserve Ride");
			 System.out.println("2. Finish"); 
			 System.out.println("Choose an option (1-2): ");
			 
       	 try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) { /*ignore*/
			}
       	 try{
       		 c = Integer.parseInt(choice);
       	 }catch (Exception e)
       	 {
       		 continue;
       	 }
       	 if(c == 1) {
       		ReserveObj temp = new ReserveObj();
       		temp.cost  = -1;
       		temp.start  = -1;
       		temp.end  = -1;
       		String sql = "SELECT * FROM UC";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("Search results:");
					while(result.next())
					{
						System.out.println("vin: " + result.getString("vin"));
						System.out.println("\t" + "Category: " + result.getString("category")
												+ "    Year: " + result.getString("year"));
					}
					System.out.println();
				}
				else
				{
					System.out.println("There are no cars to be reserved");
					c = 2;
					continue;
				}
			} 
			catch(SQLException e) {}
       		System.out.println("Please type in the VIN of the car you'd like to reserve");
       		while(temp.vin == null) {
				try
				{
					temp.vin = in.readLine();
				}
				catch (Exception e) 
				{
				}
       		}
       		System.out.println("Please type in the date you'd like the reserve a ride (yyyy-mm-dd):");
       		while(temp.date == null) {
				try
				{
					temp.date = in.readLine();
					Date test = parseDate(temp.date);
					if(test == null) {
						System.out.println("Error: Please type in the date you'd like the reserve a ride (yyyy-mm-dd):");
						temp.date = null;
					}
				}
				catch (Exception e) 
				{
				}
       		}
       		System.out.println("Please type in what hour you'd like to reserve the car (only one integer 0 - 22):");
       		while(temp.start == -1) {
				try
				{
					temp.start = Integer.parseInt(in.readLine());
					if(temp.start > 22 || temp.start < 0) {
						System.out.println("Error: Please type in what hour you'd like to reserve the car (only one integer 0 - 22):");
						temp.start = -1;
					}
					
				}
				catch (Exception e) 
				{
				}
       		}
       		System.out.println("Please type in what hour you'd like your car reservation to end (only one integer 1 - 23)\n"
       				+ "end hour must be after start hour of the same day: ");
       		while(temp.end == -1) {
				try
				{
					temp.end = Integer.parseInt(in.readLine());
					if(temp.end > 23 || temp.end < 1 || temp.end < temp.start) {
						System.out.println("Error: Please type in what hour you'd like your car reservation to end (only one integer 1 - 23)\n"
			       				+ "end hour must be after start hour of the same day: ");
						temp.start = -1;
					}
					else {
						int startInt = temp.start;
						int endInt = temp.end;
						temp.cost = endInt - startInt;
						reservations.add(temp);
					}
				}
				catch (Exception e) 
				{
				}
       		}
       	 }
       }
		if(reservations.size() > 0) {
			int totalCost = 0;
			String confirmation = "a";
			System.out.println("Confirm these reservations:");
			for(int i = 0; i < reservations.size(); i++) {			
				System.out.println("VIN:" + reservations.get(i).vin);
				System.out.println("Start Hour:" + String.valueOf(reservations.get(i).start));
				System.out.println("End Hour:" + String.valueOf(reservations.get(i).end));
				System.out.println("Date:" + reservations.get(i).date);
				System.out.println("Cost:" + String.valueOf(reservations.get(i).cost));
				totalCost = totalCost + reservations.get(i).cost;
			}
			System.out.println("Total Cost: " + String.valueOf(totalCost));
			
			System.out.println("yes/no?");
			while(true)
			{
				try {
					confirmation = in.readLine();
				}
				catch(Exception e){}
				if(confirmation.equals(null)) {
					confirmation = "a";
				}
				if(confirmation.equals("yes") || confirmation.equals("no"))
					break;
			}
			if(confirmation.equals("yes")) {
				for(int i = 0; i < reservations.size(); i++) {
					String sql = "INSERT INTO Period(fromHour, toHour) VALUES(?,?)";
					try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
					{
						pstmt.setString(1,  String.valueOf(reservations.get(i).start));
						pstmt.setString(2, String.valueOf(reservations.get(i).end));
						int success = pstmt.executeUpdate();
						if(success == 1)
						{
							ResultSet generatedKeys = pstmt.getGeneratedKeys();
							generatedKeys.next();
							int pid = generatedKeys.getInt(1);
							sql = "INSERT INTO Reserve(login, vin, pid, cost, date) VALUES(?,?,?,?,?)";
							try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql))
							{
								pstmt2.setString(1,  userLogin);
								pstmt2.setString(2, reservations.get(i).vin);
								pstmt2.setString(3,  String.valueOf(pid));
								pstmt2.setString(4, String.valueOf(reservations.get(i).cost));
								pstmt2.setString(5,  reservations.get(i).date);
								pstmt2.executeUpdate();
							} 
							catch(SQLException e) 
							{
								System.out.println("Failed to insert into Reserve\n");
								sql = "DELETE FROM Period" + "WHERE pid = " + String.valueOf(pid);
								try(PreparedStatement pstmt3 = con.conn.prepareStatement(sql))
								{
									pstmt3.executeUpdate();
								} 
								catch(SQLException eR) 
								{
									System.out.println("Failed to remove from Period");
								}
							}
						}
		
					} 
					catch(SQLException e) 
					{
						System.out.println("Failed to insert into Period\n");
					}
				}
			}
		}
	}
}