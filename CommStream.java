/**
 * Helper class to send and receive bytes
 *
 * since so much code was being repeated, wrapped it in nicer functions
 */
import java.net.*;
import java.io.*;

public class CommStream
{
	private DataOutputStream out;
	private DataInputStream in;

	public CommStream(Socket sock) throws Exception
	{
		// this.sock = sock;
		out = new DataOutputStream(sock.getOutputStream());
		in = new DataInputStream(sock.getInputStream());
	}

	// public void sendString(String s) throws Exception
	// {
	// 	sendBytes(s.getBytes());
	// }

	public void sendBytes(byte[] b) throws Exception
	{
		out.writeInt(b.length);
		out.write(b);
		System.out.println("sent " + Integer.toString(b.length) + " bytes");
		// System.out.println(new String(b));
	}

	// public String receiveString() throws Exception
	// {
	// 	byte[] b = receiveBytes();
	// 	return new String(b);
	// }

	public byte[] receiveBytes() throws Exception
	{
		// DataInputStream in = new DataInputStream(sock.getInputStream());
		int len = in.readInt();
		System.out.println("received " + Integer.toString(len) + " bytes");

		if(len <= 0)
			throw new Exception("Failed to receive message");
		
		byte[] b = new byte[len];
		in.readFully(b, 0, len);

		// System.out.println(new String(b));
		return b;
	}

	public byte[] getUserInput() throws Exception
	{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String input = inFromUser.readLine();
		if(input == null)
			throw new Exception("Null input from user");

		return input.getBytes();
	}
}