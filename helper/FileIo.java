/**
 * Java helper class to provide file management
 */
package helper;

import java.io.*;
import java.util.regex.*;
import java.nio.file.*;

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

	public boolean createDir(String dir)
	{
		File dirF = new File(dir);
		if (dirF.exists()) 
		{
			return true;
		}

		return dirF.mkdir();
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

	public byte[] readFile(String fname)
	{
		if(!fileExists(fname))
			return null;

		try
		{
			return Files.readAllBytes(Paths.get(fname));
		}
		catch(Exception e)
		{
			System.err.println("Failed to read the file bytes");
		}

		return null;
	}
}