package cs5530;
/*Driver Registration
Add a new UUber Car
Update a UUber Car*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.ResultSetMetaData;

public class DriverOptions
{
	Connector2 con;
	BufferedReader in;
	String userLogin;
	public DriverOptions(Connector2 con, String userLogin)
	{
		this.con = con;
		this.userLogin = userLogin;
	}
	public void selectDriverOp()
	{
		 in = new BufferedReader(new InputStreamReader(System.in));
		 String choice = null;
	     int c=0;
		 while(c != 3)
		 {
			 System.out.println("        UUber Driver Options     ");
			 System.out.println("1. Add a new UUber Car");
			 System.out.println("2. Update a UUber Car");
			 System.out.println("3. Go back\n");
			 System.out.println("Choose an option (1-3): ");
			 
			 try {
				 while ((choice = in.readLine()) == null || choice.length() == 0);
			 } catch (IOException e1) { /*ignore*/}
			 try{
				 c = Integer.parseInt(choice);
			 }catch (Exception e)
			 {
				 continue;
			 }
			 if (c<1 | c>2)
				 continue;
			 switch(c) {
	       	 
		     	case 1: //Add a new UUber Car
		     		addNewCar();
		       		break;
		       	case 2: //UUpdate a UUber Car
		       		if(printCars())
		       		{
		       			updateCar();
		       		}
		       		break;
	       	}
        }
	}
	public boolean addNewCar()
	{
		try 
		{
			String choice = null;
			int c2 = 0;
			String category = null;
			String make = null;
			String model = null;
			String yearS = null;
			String sql=null;
			
			System.out.println("Choose a category: ");
			while(c2<1 | c2>3)
			 {
				 System.out.println("1. Economy");
				 System.out.println("2. Comfort");
				 System.out.println("3. Luxury\n");
				 System.out.println("Choose an option (1-3): ");
				 
				 try {
					 while ((choice = in.readLine()) == null || choice.length() == 0);
				 } catch (IOException e1) { /*ignore*/}
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
			
			System.out.println("Enter the make of your car: ");
			while((make = in.readLine()) == null || make.length() == 0);
			System.out.println("Enter the model of your car: ");
			while((model = in.readLine()) == null || model.length() == 0);
			System.out.println("Enter the year of your car: ");
			while((yearS = in.readLine()) == null || yearS.length() == 0) 
			{
				try{
					Integer.parseInt(yearS);
				 }catch (Exception e)
				 {
					 System.out.println("Please enter a valid year");
				 }
			}
			sql = "INSERT INTO UC(category, year, login) VALUES(?,?,?)"; //vin autoincrements
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				String tid = addCarInfo(make, model);
				pstmt.setString(1, category);
				pstmt.setString(2, yearS);
				pstmt.setString(3, userLogin);
				int success = pstmt.executeUpdate();
				if(success == 1)
				{
					ResultSet generatedKeys = pstmt.getGeneratedKeys();
					generatedKeys.next();
					String vin = Integer.toString(generatedKeys.getInt(1));
					String sql2 = "INSERT INTO IsCtypes(vin, tid) VALUES(?,?)";
					try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql2))
					{
						pstmt2.setString(1, vin);
						pstmt2.setString(2, tid);
						pstmt2.executeUpdate();
					}
					catch(SQLException e) {
					}
					System.out.println("Car has been registered!\n");
					return true; // success
				}

			} 
			catch(SQLException e) 
			{
				System.out.println("Car registration has failed!\n");
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to login
	}
	public boolean printCars()
	{
		String sql;
		try 
		{
			sql = "SELECT * FROM UC WHERE login = ?";
			String sql2 = "SELECT * FROM IsCtypes WHERE vin = ?";
			String sql3 = "SELECT * FROM Ctypes WHERE tid = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{ 	
				pstmt.setString(1,  userLogin);
				ResultSet result = pstmt.executeQuery();
				boolean isNext = result.next();
				if(isNext)
				{
					PreparedStatement pstmt2 = con.conn.prepareStatement(sql2);
					pstmt2.setInt(1, result.getInt("vin"));
					ResultSet result2 = pstmt2.executeQuery();
					result2.next();
					PreparedStatement pstmt3 = con.conn.prepareStatement(sql3);
					pstmt3.setInt(1, result2.getInt("tid"));
					ResultSet result3 = pstmt3.executeQuery();
					result3.next();
				    System.out.println("vin: " + result.getString("vin") + "\n\t" + result.getString("category") + ", " +
				    		result3.getString("make") + " " + result3.getString("model") + ", " + result.getString("year"));
				    while(result.next() && result3.next())
				    {
					    System.out.println("vin: " + result.getString("vin") + "\n\t" + result.getString("category") + ", " +
					    		result3.getString("make") + " " + result3.getString("model") + ", " + result.getString("year"));
				    }
				    System.out.println("\n");
				    return true;
				}
				else
				{
					System.out.println("You have no registered cars.\n");
				}
			} 
			catch(SQLException e) 
			{
			}
		}
		catch (Exception e) { /* ignore close errors */ }
		return false; // failure to log in
	}
	public boolean updateCar()
	{
		try 
		{
			String choice = null;
			String choice2 = null;
			int c2 = 0;
			String category = null;
			String make = null;
			String model = null;
			String yearS = null;
			String sql=null;
			System.out.println("Type in the vin of the vehicle you would like to update: ");
			try 
			{
				 while(true)
				 {
					 while ((choice = in.readLine()) == null || choice.length() == 0);
					 try 
					 {
						 Integer.parseInt(choice);
					 } catch (Exception e) { }
					 if(isUsersCar(choice))
					 {
						 break;
					 }
					 System.out.println("Not a valid vin. Try again: ");
				 }
			 } catch (IOException e1) {}
			
			//update
			System.out.println("Choose a category: ");
			while(c2<1 | c2>3)
			 {
				 System.out.println("1. Economy");
				 System.out.println("2. Comfort");
				 System.out.println("3. Luxury\n");
				 System.out.println("Choose an option (1-3): ");
				 
				 try {
					 while ((choice2 = in.readLine()) == null || choice2.length() == 0);
				 } catch (IOException e1) { /*ignore*/}
				 try{
					 c2 = Integer.parseInt(choice2);
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
			
			System.out.println("Enter the make of your car: ");
			while((make = in.readLine()) == null || make.length() == 0);
			System.out.println("Enter the model of your: ");
			while((model = in.readLine()) == null || model.length() == 0);
			System.out.println("Enter the year of your car: ");
			while((yearS = in.readLine()) == null || yearS.length() == 0) 
			{
				try{
					Integer.parseInt(yearS);
				 }catch (Exception e)
				 {
					 System.out.println("Please enter a valid year");
				 }
			}
			String tid = addCarInfo(make, model);
			String sql2 = "UPDATE IsCtypes SET tid = ? where vin = ?";
			try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql2))
			{
				pstmt2.setString(1, tid);
				pstmt2.setString(2, choice);
				pstmt2.executeUpdate();
			}
			catch(SQLException e) {
			}
			sql = "UPDATE UC SET category = ?, year = ? WHERE login = ? AND vin = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1, category);
				pstmt.setString(2, yearS);
				pstmt.setString(3, userLogin);
				pstmt.setString(4, choice);
				int success = pstmt.executeUpdate();
				if(success == 1)
				{
					System.out.println("Car has been updated!\n");
					return true; // success
				}

			} 
			catch(SQLException e) 
			{
				System.out.println("Car update has failed!\n");
			}
			
		}catch(Exception e) {
		}
		
		return false;
	}
	
	public boolean isUsersCar(String choice)
	{
		try 
		{
			String sql=null;
				 
			sql = "SELECT * FROM UC WHERE vin = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  choice);

				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return true;
				}
			} 
			catch(SQLException e) 
			{
				System.out.println("Vehicle chosen is not registered by you");
			}
		}
		catch (Exception e) 
		{ 
			System.out.println("Vehicle chosen is not registered by you");
		}
		return false; //no car
	}
	
	/*
	 * Adds car info to Ctypes and IsCtypes. Returns tid
	 */
	public String addCarInfo(String make, String model)
	{
		String tid = getCtypeTid(make, model);
		//create new car type if car type doesn't exist already
		if(tid == null)
		{
			try 
			{
				String sql=null;

				sql = "INSERT INTO Ctypes(make, model) VALUES(?,?)";
				try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
				{
					pstmt.setString(1,  make);
					pstmt.setString(2, model);
					int success = pstmt.executeUpdate();
					if(success == 1)
					{
						ResultSet generatedKeys = pstmt.getGeneratedKeys();
						generatedKeys.next();
						tid = Integer.toString(generatedKeys.getInt(1));
					}

				} 
				catch(SQLException e) 
				{
				}
			}
			catch (Exception e) 
			{
			}
		}
		return tid;
	}
	
	/*
	 * Returns tid of car type if exists already, otherwise, return null
	 */
	public String getCtypeTid(String make, String model)
	{
		try 
		{
			String sql=null;
				 
			sql = "SELECT * FROM Ctypes WHERE make = ? AND model = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  make);
				pstmt.setString(2,  model);
				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					return result.getString("tid");
				}
			} 
			catch(SQLException e) 
			{
			}
		}
		catch (Exception e) 
		{
		}
		return null;
	}
}