/**
 * Java program: client
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;

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

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    	DataOutputStream clientOutput = new DataOutputStream(sock.getOutputStream());

    	SecretKey sharedKey = negotiateKey(sock.getInputStream(), sock.getOutputStream());
    	System.out.println(new String(sharedKey.getEncoded()));

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

	// http://stackoverflow.com/questions/14110986/new-objectinputstream-blocks
	private static SecretKey negotiateKey(InputStream inStream, OutputStream outStream) throws Exception
	{

    	// create the paramters for DH		
		int size = 512;
		SecureRandom rand = new SecureRandom();
		BigInteger[] pg = new BigInteger[2];
		pg[0] = BigInteger.probablePrime(size,rand);
		pg[1] = BigInteger.probablePrime(size,rand);
		
		// send these to the server
		ObjectOutputStream outObj = new ObjectOutputStream(outStream);
		outObj.writeObject(size);
		outObj.flush();
		System.out.println("send size");

		outObj.writeObject(pg);
		outObj.flush();

		// send public key
		DHCrypt dh = new DHCrypt(pg[0],pg[1],size);
		PublicKey pubKey = dh.getPublic();
		if(pubKey == null)
			throw new Exception("i shad");

    	DataOutputStream out = new DataOutputStream(outStream);
		byte[] pubKeyBytes = pubKey.getEncoded();
		out.writeInt(pubKeyBytes.length);
		out.write(pubKeyBytes);

		// receive server's public key
		DataInputStream in = new DataInputStream(inStream);
		int len = in.readInt();
		if(len <= 0)
			throw new Exception("Failed to get Server's key");
		byte[] servKey = new byte[len];
		in.readFully(servKey, 0, len);
		
		// generate the shared key		
		dh.setOtherKey(servKey);
		dh.generateSharedSecret();

		return dh.getShared();
	}
}





