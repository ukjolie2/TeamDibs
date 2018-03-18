package cs5530;
/*Driver Registration
Add a new UUber Car
Update a UUber Car*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DriverOptions
{
	Connector2 con;
	public DriverOptions(Connector2 con)
	{
		this.con = con;
	}
	public void selectDriverOp()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		 String choice = null;
	        int c=0;
		 while(c != 3)
        {
			 System.out.println("        UUber Driver Options     ");
			 System.out.println("1. Add a new UUber Car");
			 System.out.println("2. UUpdate a UUber Car");
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
		       		updateCar();
		       		break;
	       	}
        }
	}
	public void addNewCar()
	{
		//sql to add UUber car
	}
	public void updateCar()
	{
		//sql to update car
	}
}