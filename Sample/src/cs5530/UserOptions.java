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

public class UserOptions
{
	Connector2 con;
	public UserOptions(Connector2 con)
	{
		this.con = con;
	}
	public void selectUserOp()
	{
		 
		 BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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
        		 break;
        	 case 2: //Driver options
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
}