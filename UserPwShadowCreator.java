/**
 * Program to generate a shadow pw file
 */
import helper.FileIo;
import java.security.MessageDigest;
import java.io.*;
import java.util.*;

public class UserPwShadowCreator 
{
	public static void main(String[] args) 
	{
		FileIo fio = new FileIo();
		fio.createOutputFile("shadow.txt");

		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			if(args.length == 1 && args[0].equals("manual"))
				manualMode(md);
			else
			{
				BufferedReader buf = new BufferedReader(new FileReader("unhashed.txt"));
				
				String line = ""; // read in unhashed file and parse
				while((line = buf.readLine()) != null)
				{
					String[] userPw = line.split(" ");
					String hashed = userPw[0] + " " + encrypt(userPw[1], md);
					fio.addNewLine("shadow.txt", hashed);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error: failed to create shadow pw file");
		}
	}

	public static String encrypt(String str, MessageDigest md) throws Exception
	{
		md.update(str.getBytes("UTF-8"));
		return bytesToHex(md.digest());
	}

	// http://www.herongyang.com/Cryptography/SHA1-Message-Digest-in-Java.html
	// used to compare properly!
	private static String bytesToHex(byte[] b) 
	{
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		StringBuffer buf = new StringBuffer();
		for (int j=0; j<b.length; j++) 
		{
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}

		return buf.toString();
	}

	public static byte[] getShadowPw(String usr)	
	{
		FileIo fio = new FileIo();
		String line = "";
		try
		{
			byte[] data = fio.readFile("shadow.txt");
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

	private static List<byte[]> splitBytesBy(byte[] data, String delim) throws Exception
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

	/**
	 * this mode lets the user manually add in user and password through the terminal
	 * enter :quit to complete your process
	 */
	private static void manualMode(MessageDigest md) throws Exception
	{
		FileIo fio = new FileIo();
		Console con = System.console();
		System.out.println("Please enter all usernames and passwords! Use \":quit\" at anytime to finish and terminate.");
		while(true)
		{
			String username = con.readLine("Username: ");
			if(username.equals(":quit"))
				break;

			char[] chars = con.readPassword("Password: ");
			String pw = String.valueOf(chars);
			if(pw.equals(":quit"))
				break;

			String hashed = username + " " + encrypt(pw, md);
			fio.addNewLine("shadow.txt", hashed);
		}
	}
}