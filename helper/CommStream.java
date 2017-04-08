/**
 * Helper class to send and receive bytes
 *
 * since so much code was being repeated, wrapped it in nicer functions
 */
package helper;
import java.net.*;
import java.io.*;

public class CommStream
{
	private DataOutputStream out;
	private DataInputStream in;
	private	Console con = System.console();

	public CommStream(Socket sock) throws Exception
	{
		out = new DataOutputStream(sock.getOutputStream());
		in = new DataInputStream(sock.getInputStream());
	}

	public void sendBytes(byte[] b) throws Exception
	{
		out.writeInt(b.length);
		out.write(b);
	}

	public byte[] receiveBytes() throws Exception
	{
		int len = in.readInt();

		if(len <= 0)
			throw new Exception("Failed to receive message");
		
		byte[] b = new byte[len];
		in.readFully(b, 0, len);

		return b;
	}

	public byte[] getUserInput(String prompt, boolean pw) throws Exception
	{
		String input;
		if(pw)
		{
			char[] chars = con.readPassword(prompt + ": ");
			input = String.valueOf(chars);
		}
		else
			input = con.readLine(prompt + ": ");
		
		if(input == null)
			throw new Exception("Null input from user");

		return input.getBytes("UTF-8");
	}
}