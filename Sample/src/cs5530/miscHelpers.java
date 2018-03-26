package cs5530;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class miscHelpers
{
	Connector2 con;
	public miscHelpers(Connector2 con)
	{
		this.con = con;
	}
	/*
	 * Prints all UUber Cars (vin, category, make, model, year, ower, city/address, average score from feedbacks)
	 */
	public boolean printUC()
	{
		try 
		{	
			String sql = "SELECT UC.vin, category, year, UC.login, make, model, address, s.avScore " + 
					"FROM Ctypes, IsCtypes, UU, UC, " + 
					"(SELECT UC.vin, AVG(Feedback.score) as avScore FROM UC LEFT OUTER JOIN Feedback ON UC.vin = Feedback.vin GROUP BY UC.vin) as s " + 
					"WHERE UC.vin = IsCtypes.vin AND IsCtypes.tid = Ctypes.tid AND UU.login = UC.login AND UC.vin = s.vin"; 
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{

				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("UUber Cars:");
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
					return true;
				}
				else
				{
					System.out.println("There are no cars.");
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false;
	}
	/*
	 * Prints reviews for a selected UUber Car. If there are no cars, return false
	 */
	public boolean printUCReviews(String vin)
	{
		try 
		{
			String sql = "SELECT * FROM Feedback WHERE vin = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, vin);
				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("Feedback for UUber car #" + vin);
					while(result.next())
					{
						System.out.println("fid: " + result.getString("fid"));
						System.out.println("Date: " + result.getString("fbdate") + ", " +
											"User: " + result.getString("login") + ", " +
											"Score: " + result.getString("score") + ", " +
											"Comment: " + result.getString("text") + "\n");
					}
					return true;
				}
				else
				{
					System.out.println("No feedback for this car has been given yet.\n");
					return false;
				}
			} 
			catch(SQLException e) {System.out.println(e.getMessage());}
		}
		catch (Exception e) {System.out.println(e.getMessage());}
		return false;
	}
	/*
	 * Checks if UC car exists
	 */
	public boolean validVin(String vin)
	{
		try 
		{		 
			String sql = "SELECT * FROM UC WHERE vin = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, vin);
				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return true; // car exists
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false; //car does not exist
	}
	
	/*
	 * Checks if given user login is a valid user in the UU table
	 */
	public boolean validUser(String login)
	{
		try 
		{		 
			String sql = "SELECT * FROM UU WHERE login = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, login);
				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return true; // user exists
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false; //user does not exist
	}
	
	/*
	 * Prints the login, name, address of all UUber users in UU table
	 */
	public boolean printUU()
	{
		try 
		{	
			String sql = "SELECT login, name, address" + 
					" FROM UU"; 
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{

				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					System.out.println("UUber Users:");
					while(result.next())
					{
						System.out.println("\tUser Login: " + result.getString("login") 
												+ "\t\tName: " + result.getString("name")
												+ "\t\tCity: " + result.getString("address"));
					}
					System.out.println();
					return true;
				}
				else
				{
					System.out.println("There are no users.");
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return false;
	}
	
	/*
	 * Gets the logins of all the users in UU table
	 */
	public List<String> getAllUU()
	{
		List<String> users =  new ArrayList<String>();
		try 
		{	
			String sql = "SELECT login" + 
					" FROM UU"; 
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{

				ResultSet result = pstmt.executeQuery();
				if(result.isBeforeFirst())
				{
					while(result.next())
					{
						users.add(result.getString("login"));
					}
				}
				else
				{
					System.out.println("There are no users.");
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return users;
	}
}