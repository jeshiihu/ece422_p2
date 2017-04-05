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
		System.loadLibrary("tea");
		
		Socket sock = startConnection();
		if(sock == null)
		{
			System.err.println("Error: Client failed to connect to port " +
				Integer.toString(port) + " and host " + hostname);
			return;
		}

		System.out.println("Client successfully connected!");

		System.out.println("TESTING SHIT");
		TEAEncryption tea = new TEAEncryption();
		String str = "encode me";
		int[] edc = tea.strToIntArr(str);
		int[] key = {1,1,1,5,1,2,1,1,12,4,5,23,5,2,2,2,4,2,1};
		System.out.println("int to str: " + tea.intArrToStr(edc));
		
		tea.encrypt(edc, key);
		System.out.println("tea encrypt: " + tea.intArrToStr(edc));
		tea.decrypt(edc, key);
		System.out.println("tea decrypt: " + tea.intArrToStr(edc));

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    	BufferedReader clientInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	DataOutputStream clientOutput = new DataOutputStream(sock.getOutputStream());

    	String fromCurrClient;
		while((fromCurrClient = inFromUser.readLine()) != null)
		{
			// send to the server what client wrote to the terminal
			clientOutput.writeBytes(fromCurrClient + "\n");
			clientOutput.flush();

			System.out.println("MY input: " + fromCurrClient);
			
			if(fromCurrClient.equalsIgnoreCase("finish"))
			{
				System.out.println("Closing the connection with the server");
				sock.close();
				return;
			}
		}

		System.out.println("Client closed by quitting");
		sock.close();

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