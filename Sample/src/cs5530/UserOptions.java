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
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserOptions
{
	Connector2 con;
	BufferedReader in;
	String userLogin; //current active user
	String userName; //current active user name
	public UserOptions(Connector2 con, String userLogin, String userName)
	{
		this.con = con;
		this.userLogin = userLogin;
		this.userName = userName;
	}
	public void selectUserOp()
	{
		 
		 in = new BufferedReader(new InputStreamReader(System.in));
		 String choice = null;
	        int c=0;
		 while(c != 11)
         {
			 
			 System.out.println("        Welcome UUber User!     ");
			 System.out.println("1. Register as a UUber Driver");
			 System.out.println("2. UUber Driver Options"); //Only works when registered as a UUber Driver
			 System.out.println("3. Reserve a ride");
			 System.out.println("4. Record a ride");
			 System.out.println("5. Favorite a Car");
			 System.out.println("6. Review a UUber Car"); //View UUber Cars and then review based off primary key?
			 System.out.println("7. Review a feedback record"); //View feed back and then review?
			 System.out.println("8. Review a user"); //View feedback then review user?
			 System.out.println("9. Search");
			 System.out.println("10. View top awards");
			 System.out.println("11. Go back\n");
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
        		 break;
        	 case 4: //Record a ride
        		 break;
        	 case 5: //Favorite a car
        		 printUC();
        		 if(!hasFav()) //if has no favorite
        		 {
        			 addNewFavorite();
        		 }
        		 else
        		 {
        			 updateFavorite();
        		 }
        		 break;
        	 case 6: //Review a car
        		 break;
        	 case 7: //Review a feedback record
        		 break;
        	 case 8: //Review a user
        		 break;
        	 case 9: //Search options
        		 break;
        	 case 10: //View top awards
        		 break;
        	 }
         }
	}
	
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
	
	public boolean hasFav()
	{
		try 
		{
			String sql=null;
				 
			sql = "SELECT * FROM Favorites WHERE login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  userLogin);

				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					sql = "SELECT * FROM UC WHERE vin = ?";
					try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql))
					{
						pstmt2.setString(1,  result.getString("vin"));

						ResultSet result2 = pstmt2.executeQuery();
						if(result2.next())
						{
							System.out.println("\nYour current favorite is: \n");
							System.out.println("vin: " + result2.getString("vin") + "\n\t" + result2.getString("category") + ", " +
					    		result2.getString("make") + " " + result2.getString("model") + ", " + result2.getString("year") + "\n");
						} 
					}
					return true; // has a fav
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) { /* ignore close errors */ }
		System.out.println("You do not have a current favorite");
		return false; //does not have a fav
	}
	
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
	public boolean updateFavorite()
	{
		try 
		{
			String sql = null;
			String choice = null;
			int c2 = 0;
			System.out.println("Type in the vin of the vehicle you would like to favorite: ");
			try 
			{
				 while(choice == null)
				 {
					 while ((choice = in.readLine()) == null || choice.length() == 0);
					 try 
					 {
						 Integer.parseInt(choice);
					 } catch (Exception e) { 
						 choice = null;
						 System.out.println("Not a valid vin. Try again: ");
					 }
				 }
			 } catch (IOException e1) {
				 System.out.println("Not a valid vin. Try again: ");
			 }
			
			//update
		
			sql = "UPDATE Favorites SET vin = ?, fvdate = ? where login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				pstmt.setString(1, choice);
				pstmt.setTimestamp(2, date);
				pstmt.setString(3, userLogin);
				int success = pstmt.executeUpdate();
				if(success == 1)
				{
					System.out.println("Favorite car has been updated!\n");
					return true; // successful update
				}

			} 
			catch(SQLException e) 
			{
				System.out.println("Favorite car update has failed!\n");
			}
			
		}catch(Exception e) {}
		
		return false;
	}
	public void printUC()
	{
		String sql;
		try 
		{
			sql = "SELECT * FROM UC";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				ResultSet result = pstmt.executeQuery();
				System.out.println("List of UUber Cars:");
				while(result.next())
				{
				    System.out.println("vin: " + result.getString("vin") + "\n\t" + result.getString("category") + ", " +
				    		result.getString("make") + " " + result.getString("model") + ", " + result.getString("year"));
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
	}
}