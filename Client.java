/**
 * Java program: client
 */
import java.net.*;
import javax.crypto.*;
import java.io.*;

public class Client {
	static private String hostname = "127.0.0.1";
	static private int port = 16000;

	public static void main(String[] args) throws Exception
	{
		// System.loadLibrary("tea");
		
		Socket sock = startConnection();
		if(sock == null)
		{
			System.err.println("Error: Client failed to connect to port " +
				Integer.toString(port) + " and host " + hostname);
			return;
		}

		System.out.println("Client successfully connected!");

		PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
    	BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		String userInput;
		while ((userInput = stdIn.readLine()) != null) 
		{
    		out.println(userInput);
	    	System.out.println("echo: " + in.readLine());
		}

		// TEAEncryption tea = new TEAEncryption();

		// String val = "cello";
		// long[] lval = tea.strToLongArr(val);
		// String re = tea.longArrToStr(lval);

		// System.out.println("Orig: " + val);
		// System.out.println("lval: " + lval.toString());
		// System.out.println("re: " + re);

		// SecretKey key;
		// try
		// {
		// 	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		// 	key = keyGen.generateKey();
		// 	System.out.println("key bytes: " + key.getEncoded().toString());
		// 	long[] longKey = tea.byteArrToLongArr(key.getEncoded());
		// 	System.out.println("key long: " + longKey.toString());
		// 	byte[] ne = tea.LongArrToByteArr(longKey);
		// 	System.out.println("new long: " + ne.toString());

		// 	tea.encrypt(lval, longKey);
		// }
		// catch(Exception e)
		// {
		// 	e.printStackTrace();
		// }
	}

	private static Socket startConnection()
	{
		Socket sock = null;
		try
		{
			InetAddress host = InetAddress.getByName(hostname);
			sock = new Socket(host, port);
        	System.err.println("Connected to " + host + " on port " + port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return sock;
	}
}