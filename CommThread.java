/**
 * Java class extending a thread
 * handles the communication with the client
 *
 * help from
 * http://stackoverflow.com/questions/5419328/multiple-client-to-server-communication-program-in-java
 */
import java.net.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

public class CommThread extends Thread 
{
	private ServerSocket serverSock;
	private Socket clientSock;
	private String port;

	public CommThread(ServerSocket sSock, Socket cSock)
	{
		serverSock = sSock;
		clientSock = cSock;
		port = "[" + Integer.toString(clientSock.getPort()) + "]";
	}

	public void run()
	{
		try
		{
    		BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
    		DataOutputStream clientOutput = new DataOutputStream(clientSock.getOutputStream());

    		negotiateKey(clientInput, clientOutput);

			String fromConnectedClient;
			while((fromConnectedClient = clientInput.readLine()) != null)
			{
				System.out.println(port + ": " + fromConnectedClient);
				if(fromConnectedClient.equalsIgnoreCase("finish"))
					shutDown(port + " has closed its connection", clientSock);
			}

			shutDown("Client has quit", clientSock);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			shutDown("Error occured: Closing connection with client " + port, clientSock);
		}
	}

	private void negotiateKey(BufferedReader in, DataOutputStream out) throws Exception
	{
		// create a secret key and a public key to send over to the server
		KeyGenerator gen = KeyGenerator.getInstance("AES");
		SecretKey sPrivKey = gen.generateKey();
		SecretKey sPubKey = gen.generateKey();

		// receive client's public key
		String cPubKeyStr = in.readLine();
		if(cPubKeyStr == null)
			throw new Exception("received null public key");

		System.out.println("client's: " + cPubKeyStr);
		// // convert to key object!
		// X509EncodedKeySpec dhSpec = new X509EncodedKeySpec(cPubKeyStr.getBytes());
		// KeyFactory factory = KeyFactory.getInstance("DH");
		// PublicKey cPubKey = factory.generatePublic(dhSpec);

		// send server's public key
		byte[] sPubKeyBytes = sPubKey.getEncoded();
		System.out.println("my: " + new String(sPubKeyBytes));
		out.writeBytes(new String(sPubKeyBytes) + "\n");

		// // do the agreement of shared key!
		// KeyAgreement agree = KeyAgreement.getInstance("DH");
		// agree.init(sPrivKey);
		// agree.doPhase(cPubKey, true);

		// SecretKey sharedKey = agree.generateSecret("AES");
		// System.out.println("shared: " + new String(sharedKey.getEncoded()));
	}

	private void shutDown(String msg, Socket sock)
	{
		try
		{
			clientSock.close();
			System.err.println(msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Error: unable to properly close socket for client" + port);
		}
	}
}




