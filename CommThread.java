/**
 * Java class extending a thread
 * handles the communication with the client
 *
 * help from
 * http://stackoverflow.com/questions/5419328/multiple-client-to-server-communication-program-in-java
 */
import helper.CommStream;
import helper.DHCrypt;
import helper.FileIo;

import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.math.BigInteger;

public class CommThread extends Thread 
{
	private Socket clientSock;
	private String port;

	private SecretKey sharedKey;

	private CommStream commStream;
	private TEAEncryption tea = new TEAEncryption();

	public CommThread(Socket cSock) throws Exception
	{
		clientSock = cSock;
		commStream = new CommStream(cSock);
		port = "[" + Integer.toString(clientSock.getPort()) + "]";
	}

	public void run()
	{
		System.loadLibrary("tea");

		try
		{
    		sharedKey = negotiateKey(clientSock.getInputStream());
    		if(!validateLogin())
    		{
    			System.out.println(port + " login failed");
    			
    			String prompt = "access-denied";
				byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
				commStream.sendBytes(b);

				return;
    		}

   			System.out.println(port + " login successful! Ready to receive file requests\n");

    		String prompt = "access-granted";
			byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
			commStream.sendBytes(b);

			startFileSharing();
			shutDown(port + " has quit", clientSock);
		}
		catch(Exception e)
		{
			shutDown("Error occured: Closing connection with " + port, clientSock);
		}
	}

	private SecretKey negotiateKey(InputStream inStream) throws Exception
	{
		ObjectInputStream inObj = new ObjectInputStream(inStream);
		// get the p and g prime parameters
		int size = (int)inObj.readObject();
		BigInteger[] pg = (BigInteger[])inObj.readObject();
		
		// receive client's public key
		byte[] cliKey = commStream.receiveBytes();

		// send public key
		DHCrypt dh = new DHCrypt(pg[0],pg[1],size);
		PublicKey pubKey = dh.getPublic();
		byte[] pubKeyBytes = pubKey.getEncoded();
		commStream.sendBytes(pubKeyBytes);
		
		// generate the shared key		
		dh.setOtherKey(cliKey);
		dh.generateSharedSecret();

		return dh.getShared();
	}

	private boolean validateLogin() throws Exception
	{
		// send login prompt
		String prompt = "Please enter your username";
		byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
		commStream.sendBytes(b);

		// get username
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());
		String usr = new String(b, "UTF-8");
		System.out.println(port + " username: " + usr);
		port = "["+usr+"]";

		// send pw prompt
		prompt = "Please enter your password";
		b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
		commStream.sendBytes(b);

		// get pw
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());
		System.out.println(port + " password is received");

		return findInShadow(usr.trim(), new String(b, "UTF-8"));
	}

	private boolean findInShadow(String usr, String pw) throws Exception
	{
		FileIo fio = new FileIo();

		byte[] shadow = fio.getShadowPw(usr);
		if(shadow == null)
			return false;

		String shadowPw = new String(shadow, "UTF-8");

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		String encryptedPw = UserPwShadowCreator.encrypt(pw, md);

		return shadowPw.equals(encryptedPw);
	}

	private void startFileSharing() throws Exception
	{
		FileIo fio = new FileIo();

		while(true)
		{
			byte[] msg = commStream.receiveBytes();
			msg = tea.teaDecrypt(msg, sharedKey.getEncoded());
			
			String fname = new String(msg, "UTF-8");
			System.out.println(port + ": " + fname);
			if(fname.equals("finished"))
				break;
			
			byte[] fileBytes = null;
			if((fileBytes = fio.readFile("serverFiles/" + fname)) == null)
			{	// send a failed error message
				String prompt = "file not found";
				byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
				commStream.sendBytes(b);
			}
			else
			{	// send the successful ack and encrypted file
				String prompt = "file found";
				byte[] b = tea.teaEncrypt(prompt.getBytes("UTF-8"), sharedKey.getEncoded());
				commStream.sendBytes(b);

				b = tea.teaEncrypt(fileBytes, sharedKey.getEncoded());
				commStream.sendBytes(b);
			}
		}

		System.out.println(port + " has finished all requests");
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




