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
			 System.out.println("1. Register as a UUber Driver"); //DONE
			 System.out.println("2. UUber Driver Options"); //DONE / Only works when registered as a UUber Driver
			 System.out.println("3. Reserve a ride");
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
        		 break;
        	 case 4: //Record a ride
        		 break;
        	 case 5: //Favorite a car
        		 if(printUC())
        		 {
        			 if(!hasFav()) //if has no favorite
	        		 {
	        			 addNewFavorite();
	        		 }
	        		 else
	        		 {
	        			 updateFavorite();
	        		 }
        		 }
        		 break;
        	 case 6: //Review a car
        		 if(printUC())
        			 reviewUC();
        		 break;
        	 case 7: //Review a feedback record
        		 if(printUC())
        			 rateFBUsefulness();
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
	 * Determines if user has a favorite or not
	 */
	public boolean hasFav()
	{
		try 
		{		 
			String sql = "SELECT * FROM Favorites WHERE login = ?";
			String sql2 = "SELECT * FROM IsCtypes WHERE vin = ?";
			String sql3 = "SELECT * FROM Ctypes where tid = ?";
			try(PreparedStatement pstmt = con.conn.prepareStatement(sql))
			{
				pstmt.setString(1,  userLogin);

				ResultSet result = pstmt.executeQuery();
				if(result.next())
				{
					try(PreparedStatement pstmt2 = con.conn.prepareStatement(sql2))
					{
						pstmt2.setString(1,  result.getString("vin"));
						ResultSet result2 = pstmt2.executeQuery();
						if(result2.next())
						{
							try(PreparedStatement pstmt3 = con.conn.prepareStatement(sql3))
							{
								pstmt3.setString(1, result2.getString("tid"));
								ResultSet result3 = pstmt3.executeQuery();
								if(result3.next())
								{
									System.out.println("\nYour current favorite is:");
									System.out.println("vin: " + result2.getString("vin") + "\n\t" + result3.getString("make") + " " + result3.getString("model")
											+ " favorited on date: " + result.getString("fvdate"));
								} 
							}
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
	/*
	 * Adds user's favorite car if they don't already have a favorite
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
	/*
	 * Updates user's favorite car if they already have a favorite
	 */
	public boolean updateFavorite()
	{
		try 
		{
			String sql = null;
			String choice = null;
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
					 if(!validVin(vin))
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
					 if(!validVin(vin))
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
			if(printUCReviews(vin))
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
	/*******MISC HELPERS/PRINTING*********/
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