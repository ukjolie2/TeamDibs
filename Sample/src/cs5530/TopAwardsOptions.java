package cs5530;
/*View top charts
View user awards*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopAwardsOptions
{
	private Connector2 con;
	private BufferedReader in;
	public TopAwardsOptions(Connector2 con)
	{
		in = new BufferedReader(new InputStreamReader(System.in));
		this.con = con;
	}
	public void selectTopOp()
	{
		 String choice = null;
	        int c=0;
		 while(c != 3)
        {
			System.out.println("        Top Awards Options     ");
			System.out.println("1. View Top Charts"); //DONE / Statistics
			System.out.println("2. View User Awards");
			System.out.println("3. Go back\n");
			System.out.println("Choose an option (1-3): ");
			 
	       	try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) {}
	       	try{
	       		c = Integer.parseInt(choice);
	       	}catch (Exception e)
	       	{
	       		continue;
	       	}
	       	if (c<1 | c>2)
	       		continue;
	       	switch(c) {
	       	 
		       	case 1: //View Top Charts
		       		selectTopChartOps(); //DONE
		       		break;
		       	case 2: //View User Awards
		       		break;
	       	}
        }
	}
	/*
	 * View most popular UCs, expensive UCs, highly rated UDs in each car category.
	 */
	private void selectTopChartOps()
	{
		String choice = null;
        int c = 0;
        String num = null;
        int m = 0;
        while(c != 4)
        {
			System.out.println("        Top Charts Options     ");
			System.out.println("1. View list of most popular UUber Cars for each category");
			System.out.println("2. View list of most expensive UUber Cars for each category");
			System.out.println("3. View list of most highly rated UUber Drivers for each category");
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
	       	System.out.println("Type the max number of positions you would like to see for each category: ");
	       	while(true)
	       	{
	       		try {
					while ((num = in.readLine()) == null || num.length() == 0);
				} catch (IOException e1) {}
		       	try{
		       		m = Integer.parseInt(num);
		       		break;
		       	}catch (Exception e)
		       	{
		       		System.out.println("Not a valid number. Try again: ");
		       	}
	       	}
	       	switch(c) {
	       	 
		       	case 1: //Most popular
		       		printTopPopular(m);
		       		break;
		       	case 2: //Most expensive
		       		printTopCost(m);
		       		break;
		       	case 3: //Most highly rated
		       		printTopRated(m);
		       		break;
	       	}
        }
	}
	/*
	 * List of m most popular UCs in terms of total rides for each category
	 */
	private void printTopPopular(int m)
	{
		String[] categories = new String[3];
		categories[0] = "Economy";
		categories[1] = "Comfort";
		categories[2] = "Luxury";
		try 
		{	
			String sql = "SELECT x.TotalRides, x.vin, x.Owner, x.year, Ctypes.make, Ctypes.model "
					+ "FROM Ctypes, IsCtypes, "
					+ "(SELECT COUNT(*) as TotalRides, UC.vin, UC.year, UC.login as Owner "
					+ "FROM Ride, UC WHERE Ride.vin = UC.vin AND category = ? "
					+ "GROUP BY UC.vin ORDER BY TotalRides DESC LIMIT ?) as x "
					+ "WHERE IsCtypes.vin = x.vin AND Ctypes.tid = IsCtypes.tid;";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				for(int i = 0; i < categories.length; i++)
				{
					pstmt.setString(1, categories[i]);
					pstmt.setInt(2, m);
					ResultSet result = pstmt.executeQuery();
					if(result.isBeforeFirst())
					{
						int rank = 1;
						System.out.println("Top " + Integer.toString(m) + " Most" + " Popular " + categories[i] + " Cars:");
						while(result.next())
						{
							System.out.println("#" + Integer.toString(rank) + "\n\tvin: " + result.getString("vin"));
							System.out.println("\t"+ "Make: " + result.getString("make")
							+ "    Model: " + result.getString("model")
							+ "    Year: " + result.getString("year")
							+ "    Owner: " + result.getString("Owner")
							+ "    Total Rides: " + result.getString("TotalRides"));
							rank++;
						}
						System.out.println();
					}
					else
					{
						System.out.println("There are no cars.");
					}
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
	}
	/*
	 * List of m most expensive UCs defined by the average cost of all rides on a UC for each category
	 */
	private void printTopCost(int m)
	{
		String[] categories = new String[3];
		categories[0] = "Economy";
		categories[1] = "Comfort";
		categories[2] = "Luxury";
		try 
		{	
			String sql = "SELECT x.avCost, x.vin, x.Owner, x.year, Ctypes.make, Ctypes.model "
					+ "FROM Ctypes, IsCtypes, (SELECT AVG(cost) as avCost, UC.vin, UC.year, UC.login as Owner "
					+ "FROM Ride, UC WHERE Ride.vin = UC.vin AND category = ? "
					+ "GROUP BY UC.vin ORDER BY avCost DESC LIMIT ?) "
					+ "as x WHERE IsCtypes.vin = x.vin AND Ctypes.tid = IsCtypes.tid;";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				for(int i = 0; i < categories.length; i++)
				{
					pstmt.setString(1, categories[i]);
					pstmt.setInt(2, m);
					ResultSet result = pstmt.executeQuery();
					if(result.isBeforeFirst())
					{
						int rank = 1;
						System.out.println("Top " + Integer.toString(m) + " Most" + " Expensive " + categories[i] + " Cars:");
						while(result.next())
						{
							System.out.println("#" + Integer.toString(rank) + "\n\tvin: " + result.getString("vin"));
							System.out.println("\t"+ "Make: " + result.getString("make")
							+ "    Model: " + result.getString("model")
							+ "    Year: " + result.getString("year")
							+ "    Owner: " + result.getString("Owner")
							+ "    Average Cost: " + result.getString("avCost"));
							rank++;
						}
						System.out.println();
					}
					else
					{
						System.out.println("There are no cars.");
					}
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
	}
	/*
	 * List of m highly rated UDs defined by the average scores from all feedbacks a UD has received for
	 * all of his/her UCs for each category
	 */
	private void printTopRated(int m)
	{
		String[] categories = new String[3];
		categories[0] = "Economy";
		categories[1] = "Comfort";
		categories[2] = "Luxury";
		try 
		{	
			String sql = "SELECT UC.login, AVG(score) as avScore FROM UC LEFT OUTER JOIN Feedback ON UC.vin = Feedback.vin WHERE category = ? " + 
					"GROUP BY UC.login ORDER BY avScore DESC LIMIT ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				for(int i = 0; i < categories.length; i++)
				{
					pstmt.setString(1, categories[i]);
					pstmt.setInt(2, m);
					ResultSet result = pstmt.executeQuery();
					if(result.isBeforeFirst())
					{
						int rank = 1;
						System.out.println("Top " + Integer.toString(m) + " Highest Rated " + categories[i] + " Drivers:");
						while(result.next())
						{
							System.out.println("#" + Integer.toString(rank));
							System.out.println("\t" + "UUber Driver: " + result.getString("login") +
												"\tAverage Score for " + categories[i] + ": " + result.getString("avScore"));
							rank++;
						}
						System.out.println();
					}
					else
					{
						System.out.println("There are no cars.");
					}
				}
			} 
			catch(SQLException e) {}
		}
		catch (Exception e) {}
	}
}