package cs5530;
/*View top charts
View user awards*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
			System.out.println("1. View Top Charts");
			System.out.println("2. View User Awards");
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
	       	 
		       	case 1: //View Top Charts
		       		break;
		       	case 2: //View User Awards
		       		break;
	       	}
        }
	}
}