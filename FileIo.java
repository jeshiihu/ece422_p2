/**
 * Java helper class to provide file management
 */

import java.io.*;
import java.util.regex.*;

public class FileIo
{
	public FileIo() {}

	public boolean validTxtFile(String fname)
	{
		// check if file matches .txt format
		String p = "(.+)(\\.txt)";
		return Pattern.matches(p, fname);		
	}

	public boolean fileExists(String fname)
	{
		File f = new File(fname);
		return f.exists() && !f.isDirectory();
	}

	public boolean createOutputFile(String fname)
	{
		File f = new File(fname);
		try 
		{
			// https://www.mkyong.com/java/how-to-create-a-file-in-java/
			if(!f.createNewFile())
			{
				f.delete(); // ensure new file
				f.createNewFile();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void addNewLine(String fname, String line)
	{
		try
		{ // true in 2nd param of file writer is to append 
			Writer fout = new BufferedWriter(new FileWriter(fname, true));
			fout.append(line + "\n");
			fout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getShadowPw(String usr)	
	{
		System.out.println("var: " + usr);
		String line = "";
		try
		{
			BufferedReader buf = new BufferedReader(new FileReader("shadow.txt"));
			while((line = buf.readLine()) != null)
			{
				String[] userPw = line.split(" ");
				System.out.println("user: " + userPw[0] + " pw: " + userPw[1]);
				if(usr.trim().equals(userPw[0].trim()))
					return userPw[1];
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return "";
	}
}