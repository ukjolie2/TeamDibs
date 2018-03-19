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
		 while(c != 10)
         {
			 
			 System.out.println("        Welcome UUber User!     ");
			 System.out.println("1. Register as a UUber Driver");
			 System.out.println("2. UUber Driver Options"); //Only works when registered as a UUber Driver
			 System.out.println("3. Record a ride");
			 System.out.println("4. Favorite a Car");
			 System.out.println("5. Review a UUber Car"); //View UUber Cars and then review based off primary key?
			 System.out.println("6. Review a feedback record"); //View feed back and then review?
			 System.out.println("7. Review a user"); //View feedback then review user?
			 System.out.println("8. Search");
			 System.out.println("9. View top awards");
			 System.out.println("10. Go back\n");
			 System.out.println("Choose an option (1-10): ");
			 
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
        	 if (c<1 | c>9)
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
        	 case 3: //Record a ride
        		 break;
        	 case 4: //Favorite a car
        		 break;
        	 case 5: //Review a car
        		 break;
        	 case 6: //Review a feedback record
        		 break;
        	 case 7: //Review a user
        		 break;
        	 case 8: //Search options
        		 break;
        	 case 9: //View top awards
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
						return true; // success logging in
					}
	
				} 
				catch(SQLException e) 
				{
					System.out.println("Registration failed. You are already registered as a driver\n");
				}
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to login
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
	
	
}