/**
 * Java program to setup the shadow password
 *
 * Input: 
 * 	1. name of text file containing the usernames and passwords
 * 	2. name of encrypted file
 * 	
 * Output:
 * 	1. encrypted file
 *
 *  Encryption method:
 */

import java.io.*;

public class UserPwCreator 
{
	public static void main(String[] args) 
	{
		if(args.length != 2)
		{
			System.err.println("Error: expected 2 args {input.txt output.txt}");
			return;
		}

		FileIo fileIo = new FileIo();
		if(!fileIo.validTxtFile(args[0]) || !fileIo.validTxtFile(args[1])
			|| !fileIo.fileExists(args[0]))
		{
			System.err.println("Error: filenames must have the .txt extention");
			return;
		}

		String fin = args[0];
		String fout = args[1];

		if(!fileIo.createOutputFile(fout))
		{
			System.err.println("Error: failed to create output file");
			return;
		}

		try
		{
			String line = "";
			HashHelper sha1 = new HashHelper("SHA-1");
			BufferedReader buf = new BufferedReader(new FileReader(fin));
			while((line = buf.readLine()) != null)
			{
				String[] userPw = line.split(" ");
				String hashed = userPw[0] + " " + sha1.encrypt(userPw[1]);
				fileIo.addNewLine(fout, hashed);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}