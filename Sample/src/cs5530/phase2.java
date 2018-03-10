package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.sql.PreparedStatement;

public class phase2 {

	/**
	 * @param args
	 */
	public static void displayMenu()
	{
		 System.out.println("        Welcome to UUber System     ");
		 System.out.println("1. User Registration");
		 System.out.println("2. Driver Registration");
		 System.out.println("3. Make a reservation");
		 System.out.println("4. Add a new UUber Car");
		 System.out.println("5. Update a UUber Car");
		 System.out.println("6. Record a ride");
		 System.out.println("7. Favorite a Car");
		 System.out.println("8. Review a UUber Car");
		 System.out.println("9. Review a feedback record");
		 System.out.println("10. Review a User");
		 System.out.println("11. Search for a UUber Car");
		 System.out.println("12. Search for useful feedbacks");
		 System.out.println("13. Search recommended UUber Cars");
		 System.out.println("14. Search for similar users");
		 System.out.println("15. View top charts");
		 System.out.println("16. View user awards");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Example for cs5530");
		Connector2 con=null;
		String choice;
        String login;
        String password;
        String address;
        String phoneNum;
        String sql=null;
        int c=0;
         try
		 {
			//remember to replace the password
			 	 con= new Connector2();
	             System.out.println ("Database connection established");
	         
	             BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	             
	             while(true)
	             {
	            	 displayMenu();
	            	 while ((choice = in.readLine()) == null && choice.length() == 0);
	            	 try{
	            		 c = Integer.parseInt(choice);
	            	 }catch (Exception e)
	            	 {
	            		 
	            		 continue;
	            	 }
	            	 if (c<1 | c>3)
	            		 continue;
	            	 if(c==1)
	            	 {
	            		 System.out.println("Choose a login-name: ");
	            		 while((login = in.readLine()) == null && login.length() == 0);
	            		 System.out.println("Choose a password: ");
	            		 while((password = in.readLine()) == null && password.length() == 0);
	            		 System.out.println("Enter your address: ");
	            		 while((address = in.readLine()) == null && address.length() == 0);
	            		 System.out.println("Enter your phone number: ");
	            		 while((phoneNum = in.readLine()) == null && phoneNum.length() == 0);
	            		 
	            		 //sql = "INSERT INTO UU(login, name, address, phone) VALUES('" + login + "', '" + password + "', '" + address + "', '" + phoneNum + "')";
	            		 
	            		 sql = "INSERT INTO UU(login, name, address, phone)" + " VALUES(?,?,?,?)";
	            		 try(
	            		 PreparedStatement pstmt = con.conn.prepareStatement(sql)){
	            		 pstmt.setString(1,  login);
	            		 pstmt.setString(2, password);
	            		 pstmt.setString(3, address);
	            		 pstmt.setString(4, phoneNum);
	            		 pstmt.executeUpdate();
	            		 } catch(SQLException e) {
	            			 System.out.println(e.getMessage());
	            		 }
	            		 
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
}
