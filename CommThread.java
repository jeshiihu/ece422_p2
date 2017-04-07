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
import javax.crypto.*;
import java.math.BigInteger;

public class CommThread extends Thread 
{
	private ServerSocket serverSock;
	private Socket clientSock;
	static private String port;
	static private CommStream commStream;

	static private SecretKey sharedKey;
	static private TEAEncryption tea = new TEAEncryption();

	public CommThread(ServerSocket sSock, Socket cSock) throws Exception
	{
		serverSock = sSock;
		clientSock = cSock;
		commStream = new CommStream(cSock);
		port = "[" + Integer.toString(clientSock.getPort()) + "]";
	}

	public void run()
	{
		System.loadLibrary("tea");

		try
		{

    		DataInputStream clientInput = new DataInputStream(clientSock.getInputStream());
    		BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
    		DataOutputStream clientOutput = new DataOutputStream(clientSock.getOutputStream());

    		sharedKey = negotiateKey(clientSock.getInputStream(), clientSock.getOutputStream());

    		if(!validateLogin())
    		{
    			System.out.println("Client " + port + " login failed");
				return;
    		}

			String fromConnectedClient;
			while((fromConnectedClient = in.readLine()) != null)
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

	private SecretKey negotiateKey(InputStream inStream, OutputStream outStream) throws Exception
	{
		ObjectInputStream inObj = new ObjectInputStream(inStream);
		// commStr÷eam
		// get the p and g prime parameters
		int size = (int)inObj.readObject();
		BigInteger[] pg = (BigInteger[])inObj.readObject();
		
		// receive client's public key
		DataInputStream in = new DataInputStream(inStream);
		int len = in.readInt();
		if(len <= 0)
			throw new Exception("Failed to get Server's key");

		byte[] cliKey = new byte[len];
		in.readFully(cliKey, 0, len);

		// send public key
		DHCrypt dh = new DHCrypt(pg[0],pg[1],size);
		PublicKey pubKey = dh.getPublic();

    	DataOutputStream out = new DataOutputStream(outStream);
		byte[] pubKeyBytes = pubKey.getEncoded();
		out.writeInt(pubKeyBytes.length);
		out.write(pubKeyBytes);
		
		// generate the shared key		
		dh.setOtherKey(cliKey);
		dh.generateSharedSecret();

		return dh.getShared();
	}

	private static boolean validateLogin() throws Exception
	{
		// send login prompt
		String prompt = "Please enter your username";
		byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
		commStream.sendBytes(b);

		// get username
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());
		String usr = new String(b, "UTF-8");
		usr.trim();
		System.out.println(port + " username: " + usr);

		// send pw prompt
		prompt = "Please enter your password";
		b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
		commStream.sendBytes(b);

		// get pw
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());
		System.out.println(port + " password is received");

		if(!findInShadow(usr, b))
			return false;

		return true;
	}

	private static boolean findInShadow(String usr, byte[] pw) throws Exception
	{
		FileIo fio = new FileIo();
		if(usr.trim().equals("Jess"))
			System.out.println("whyyy");

		String shadow = fio.getShadowPw(usr);
		
		HashHelper hh = new HashHelper("SHA-1");
		System.out.println(hh.encrypt("hi"));
		// System.out.println(new String(pw,"UTF-8"));
		String pStr = new String(pw,"UTF-8");
		// System.out.println(pStr.getBytes("UTF-8"));

		String hex = hh.encrypt(pStr);

		return hh.matches(shadow,hex);
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




