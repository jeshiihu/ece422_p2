/**
 * Java program: client
 */
import java.net.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
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

		// System.out.println("TESTING SHIT");
		// TEAEncryption tea = new TEAEncryption();
		// String str = "encode me";
		
		// KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		// SecretKey key = keyGen.generateKey();

		// String getback = new String(str.getBytes());
		// System.out.println("int to str: " + getback);
		

		// byte[] strBytes = str.getBytes();
		// long[] strLong = tea.byteArrToLongArr(str.getBytes());
		// tea.encrypt(strLong, tea.byteArrToLongArr(key.getEncoded()));
		// System.out.println("tea encrypt: " + new String(strBytes));
		// for(long l: strLong)
		// 	System.out.println(Long.toString(l));

		// tea.decrypt(strLong, tea.byteArrToLongArr(key.getEncoded()));
		// System.out.println("tea decrypt: " + new String(strBytes));
		// for(long l: strLong)
		// 	System.out.println(Long.toString(l));

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    	BufferedReader clientInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	DataOutputStream clientOutput = new DataOutputStream(sock.getOutputStream());

    	negotiateKey(clientInput, clientOutput);


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

	private static void negotiateKey(BufferedReader in, DataOutputStream out) throws Exception
	{
		// create a secret key and a public key to send over to the server
		KeyGenerator gen = KeyGenerator.getInstance("AES");
		SecretKey cPrivKey = gen.generateKey();
		SecretKey cPubKey = gen.generateKey();

		// send client public key
		byte[] cPubKeyBytes = cPubKey.getEncoded();
		System.out.println("my public: " + new String(cPubKeyBytes));
		out.writeBytes(new String(cPubKeyBytes) + "\n");

		// receive server's public
		String sPubKeyStr = in.readLine();
		if(sPubKeyStr == null)
			throw new Exception("received null public key");

		System.out.println("server's pub: " + sPubKeyStr);
		// // convert to key object!
		// X509EncodedKeySpec dhSpec = new X509EncodedKeySpec(sPubKeyStr.getBytes());
		// KeyFactory factory = KeyFactory.getInstance("DH");
		// PublicKey sPubKey = factory.generatePublic(dhSpec);

		// // do the agreement of shared key!
		// KeyAgreement agree = KeyAgreement.getInstance("DH");
		// agree.init(cPrivKey);
		// agree.doPhase(sPubKey, true);

		// SecretKey sharedKey = agree.generateSecret("AES");
		// System.out.println("Shared: " + new String(sharedKey.getEncoded()));
	}
}





