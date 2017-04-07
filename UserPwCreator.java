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
		if(args.length != 1)
		{
			System.err.println("Error: expected 1 args {input.txt}");
			return;
		}

		FileIo fileIo = new FileIo();
		if(!fileIo.validTxtFile(args[0]) || !fileIo.fileExists(args[0]))
		{
			System.err.println("Error: filenames must have the .txt extention");
			return;
		}

		String fin = args[0];
		String fout = "shadow.txt";
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
				System.out.println(userPw[0].getBytes("UTF-8"));
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