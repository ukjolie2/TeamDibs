package cs5530;
/*Search for a UUber Car
Search for useful feedbacks
Search recommended UUber Cars
Search for similar users*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SearchOptions
{
	Connector2 con;
	public SearchOptions(Connector2 con)
	{
		this.con = con;
	}
	public void selectSearchOp()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		 String choice = null;
	        int c=0;
		 while(c != 5)
        {
			System.out.println("        Search Options     ");
			System.out.println("1. Search for a UUber Car");
			System.out.println("2. Seach for useful feedbacks");
			System.out.println("3. Seach for recommended UUber Cars");
			System.out.println("4. Seach for similar users");
			System.out.println("5. Go back\n");
			System.out.println("Choose an option (1-5): ");
			 
	       	try {
				while ((choice = in.readLine()) == null || choice.length() == 0);
			} catch (IOException e1) { /*ignore*/}
	       	try{
	       		c = Integer.parseInt(choice);
	       	}catch (Exception e)
	       	{
	       		continue;
	       	}
	       	if (c<1 | c>4)
	       		continue;
	       	switch(c) {
	       	 
		       	case 1: //Search for UUber Car
		       		break;
		       	case 2: //Search for useful feedbacks
		       		break;
		       	case 3: //Search for recommended UUber Cars
		       		break;
		       	case 4: //Search for similar users
		       		break;
	       	}
        }
	}
}