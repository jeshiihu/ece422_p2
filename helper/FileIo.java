/**
 * Java helper class to provide file management
 */
package helper;

import java.io.*;
import java.util.regex.*;
import java.util.*;
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

	public byte[] getShadowPw(String usr)	
	{
		String line = "";
		try
		{
			byte[] data = readFile("shadow.txt");
			List<byte[]> byteLines = splitBytesBy(data, "\n");

			for(byte[] b : byteLines)
			{
				List<byte[]> userPw = splitBytesBy(b, " ");
				if(userPw.size() == 0)
					continue;

				String username = new String(userPw.get(0), "UTF-8");
				if(username.trim().equals(usr.trim()))
					return userPw.get(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private List<byte[]> splitBytesBy(byte[] data, String delim) throws Exception
	{
		List<byte[]> list = new ArrayList<byte[]>();
		// figure out the delim in bytes
		byte[] bDelim = delim.getBytes("UTF-8");
		int delimLen = bDelim.length;

		int start = 0;

		for(int i = 0; i < data.length; i++)
		{
			// make sure we don't go over
			if((i+delimLen) <= data.length) 
			{
				byte[] check = Arrays.copyOfRange(data, i, i+delimLen);
				boolean matches = true;
				for(int j = 0; j < delimLen; j++)
				{
					if(check[j] != bDelim[j])
						matches = false;
				}

				if(matches)
				{
					list.add(Arrays.copyOfRange(data, start, i));
					start = i+delimLen;
				}
			}
		}

		// get the last one!
		if(start < data.length)
			list.add(Arrays.copyOfRange(data, start, data.length));

		return list;
	}
}