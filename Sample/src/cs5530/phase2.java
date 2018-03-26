//Kathy Huang u0916600
//Jolie Uk u1008240
package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.sql.PreparedStatement;

public class phase2 {

	/**
	 * @param args
	 */
	private static String userLogin; //current active user
	private static String userName; //current active user's name
	private static BufferedReader in;
	public static void displayMenu()
	{
		 System.out.println("        Welcome to UUber System     ");
		 System.out.println("1. User Registration"); //DONE
		 System.out.println("2. Login"); //DONE
		 System.out.println("3. Quit\n"); //DONE
		 System.out.println("Choose an option (1-3): ");
	}
	
	public static void main(String[] args) {
		Connector2 con=null;
		String choice;
        int c=0;
         try
		 {
			//remember to replace the password
			 	 con= new Connector2();
	             System.out.println ("Database connection established");
	         
	             in = new BufferedReader(new InputStreamReader(System.in));
	             
	             while(true)
	             {
	            	 displayMenu();
	            	 while ((choice = in.readLine()) == null || choice.length() == 0);
	            	 try{
	            		 c = Integer.parseInt(choice);
	            	 }catch (Exception e)
	            	 {
	            		 
	            		 continue;
	            	 }
	            	 if (c<1 | c>3)
	            		 continue;
	            	 if(c==1) //User Registration
	            	 {
	            		 if(createUser(con))
	            		 {
	            			 UserOptions userOp = new UserOptions(con, userLogin, userName);
	            			 userOp.selectUserOp();
	            		 } 
	            	 }
	            	 else if(c==2)
	            	 {
	            		 if(loginUser(con))
	            		 {
	            			 UserOptions userOp = new UserOptions(con, userLogin, userName);
	            			 userOp.selectUserOp();
	            		 }
	            	 }
	            	 else if(c== 3)
	            	 {
	            		 System.out.println("Goodbye!");
	            		 con.stmt.close(); 
	            		 break;
	            	 }
	            	 else
	            	 {   
	            		 System.out.println("EoM");
	            		 con.stmt.close(); 
	            		 break;
	            	 }
	             }
		 }
         catch (Exception e)
         {
        	 e.printStackTrace();
        	 System.err.println ("Either connection error or query execution error!");
         }
         finally
         {
        	 if (con != null)
        	 {
        		 try
        		 {
        			 con.closeConnection();
        			 System.out.println ("Database connection terminated");
        		 }
        	 
        		 catch (Exception e) { /* ignore close errors */ }
        	 }	 
         }
	}
	
	/*
	 * Creates a new user in the UU table
	 */
	public static boolean createUser(Connector2 con)
	{
		try 
		{
			String login;
			String password;
			String name;
			String address;
			String phoneNum;
			String sql=null;
			
			System.out.println("Choose a login-name: ");
			while((login = in.readLine()) == null || login.length() == 0);
			
			System.out.println("Choose a password: ");
			while((password = in.readLine()) == null || password.length() == 0);
			
			System.out.println("Enter your name: ");
			while((name = in.readLine()) == null || name.length() == 0);
			
			System.out.println("Enter your city: ");
			while((address = in.readLine()) == null || address.length() == 0);
			
			System.out.println("Enter your phone number: ");
			while((phoneNum = in.readLine()) == null || phoneNum.length() == 0);  
		 
			sql = "INSERT INTO UU(login, password, name, address, phone) VALUES(?,?,?,?,?)";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  login);
				pstmt.setString(2, password);
				pstmt.setString(3,  name);
				pstmt.setString(4, address);
				pstmt.setString(5, phoneNum);
				int success = pstmt.executeUpdate();
				if(success == 1)
				{
					userLogin = login;
					userName = name;
					System.out.println("Account created! You have been logged in. Hello " + userName + "!\n");
					return true; // success logging in
				}

			} 
			catch(SQLException e) 
			{
				System.out.println("Account not created. Try again with a different login name.\n");
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to login
	}
	
	/*
	 * Login user
	 */
	public static boolean loginUser(Connector2 con)
	{
		try 
		{
			String login;
			String password;

			String sql=null;
			
			System.out.println("Enter your login-name: ");
			while((login = in.readLine()) == null || login.length() == 0);
			
			System.out.println("Enter your password: ");
			while((password = in.readLine()) == null || password.length() == 0);
				 
			sql = "SELECT login, password, name FROM UU WHERE login = ? && password = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  login);
				pstmt.setString(2, password);

				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					userLogin = login;
					userName = result.getString("name");
					System.out.println("Log in successful! Hello " + userName + "!\n");
					return true; // success logging in
				}
				else
				{
					System.out.println("Log in unsuccessful. Try again.\n");
				} 
			} 
			catch(SQLException e) 
			{
				System.out.println("Log in not successful. Try again.\n");
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to log in
	}
}
