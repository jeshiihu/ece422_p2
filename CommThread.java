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
	
	static private SecretKey pwKey;

	static private String port;
	static private CommStream commStream;

	static private SecretKey sharedKey;
	static private TEAEncryption tea = new TEAEncryption();

	public CommThread(ServerSocket sSock, Socket cSock, SecretKey passKey) throws Exception
	{
		serverSock = sSock;
		clientSock = cSock;
		pwKey = passKey;

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
		System.out.println(port + " password is received " + new String(b, "UTF-8"));

		return findInShadow(usr, new String(b, "UTF-8"));
	}

	/**
	 * [findInShadow description]
	 * @param  usr       [description]
	 * @param  pw        [Encrypted password!]
	 * @return           [description]
	 * @throws Exception [description]
	 */
	private static boolean findInShadow(String usr, String pw) throws Exception
	{
		FileIo fio = new FileIo();

		// HashHelper hh = new HashHelper("SHA-1");
		byte[] shadow = fio.getShadowPw(usr);
		shadow = tea.teaDecrypt(shadow, pwKey.getEncoded());
		String shadowPw = new String(shadow, "UTF-8");

		// byte[] hexBytes = tea.teaDecrypt(pw, pwKey.getEncoded());
		// String hex = new String(hexBytes, "UTF-8");
		// byte[] shadBytes = shadow.getBytes("UTF-8");
		System.out.println("shad: " + shadowPw + ", pw: " + pw);
		return true;
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




