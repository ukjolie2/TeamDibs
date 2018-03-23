package cs5530;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class miscHelpers
{
	Connector2 con;
	public miscHelpers(Connector2 con)
	{
		this.con = con;
	}
	/*
	 * Prints all UUber Cars (vin, category, make, model, year)
	 */
	public boolean printUC()
	{
		try 
		{
			String sql = "SELECT * FROM UC";
			String sql2 = "SELECT * FROM IsCtypes WHERE vin = ?";
			String sql3 = "SELECT * FROM Ctypes WHERE tid = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				ResultSet result = pstmt.executeQuery();
				System.out.println("List of UUber Cars: ");
				if(result.isBeforeFirst())
				{
					while(result.next())
					{
						String vin = result.getString("vin");
						try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql2))
						{
							pstmt2.setString(1, vin);
							ResultSet result2 = pstmt2.executeQuery();
							if(result2.next()) 
							{
								try(PreparedStatement pstmt3 = con.conn.prepareStatement(sql3))
								{
									pstmt3.setString(1, result2.getString("tid"));
									ResultSet result3 = pstmt3.executeQuery();
									if(result3.next())
									{
										System.out.println("vin: " + vin + "\n\t" + result.getString("category") + ", " +
								    		result3.getString("make") + " " + result3.getString("model") + ", " + result.getString("year"));
									}
								}
								catch(SQLException e) {}
							}
						} 
						catch(SQLException e) {}
					}
				}
				else
				{
					System.out.println("There are no cars");
					return false;
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
		return true;
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
						return true;
					}
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
}