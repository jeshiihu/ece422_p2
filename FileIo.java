/**
 * Java helper class to provide file management
 */

import java.io.*;
import java.util.regex.*;

public class FileIo
{
	public FileIo() {}

	public boolean ValidTxtFile(String fname)
	{
		// check if file matches .txt format
		String p = "(.+)(\\.txt)";
		boolean validTxt = Pattern.matches(p, fname);

		// check if file exists
		File f = new File(fname);
		boolean exists = f.exists() && !f.isDirectory();

		return validTxt && exists;
	}

	public boolean CreateOutputFile(String fname)
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
}