/**
 * Java program: client
 */
import helper.CommStream;
import helper.DHCrypt;
import helper.FileIo;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.io.Console;
import java.nio.file.*;

public class Client {
	static private String hostname;
	static private int port;
	static private SecretKey sharedKey;
	static private TEAEncryption tea = new TEAEncryption();
	static private CommStream commStream;

	static private String username;

	public static void main(String[] args) throws Exception
	{
		if(args.length != 2)
		{
			System.err.println("Error: invalid arguements {hostname} {port}");
			return;
		}

		hostname = args[0];
		try
		{
			port = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e)
		{
			System.err.println("Port needs to be a valid integer value");
			return;
		}


		System.loadLibrary("tea");
		Socket sock = startConnection();
		try
		{
			if(sock == null)
			{
				System.err.println("Error: Client failed to connect to port " +
					Integer.toString(port) + " and host " + hostname);
				return;
			}
			System.out.println("Client successfully connected!");
			commStream = new CommStream(sock);

	    	sharedKey = negotiateKey(sock.getInputStream(), sock.getOutputStream());

	    	if(!validateLogin())
	    	{
	    		System.out.println("Invalid login, closing connection");
				sock.close();
				return;
			}

	    	System.out.println("Valid login! Ready to send file requests");
	    	startFileSharing();

			System.out.println("Client closed by quitting");
		}
		catch(Exception e)
		{
			System.err.println("Error: Client is quitting");
		}

		sock.close();
	}

	private static Socket startConnection()
	{
		Socket sock = null;
		try
		{
			InetAddress host = InetAddress.getByName(hostname);
			sock = new Socket(host, port);
        	System.out.println("Connected to " + hostname + " on port " + port);
		}
		catch(Exception e)
		{
			System.err.println("Failed to connect to " + hostname + " on port " + port);
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
		outObj.writeObject(pg);
		outObj.flush();

		// send public key
		DHCrypt dh = new DHCrypt(pg[0],pg[1],size);
		PublicKey pubKey = dh.getPublic();

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

	private static boolean validateLogin() throws Exception
	{
		// get login prompt from server
		byte[] b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());

		// send username
		b = commStream.getUserInput(new String(b,"UTF-8"), false);
		username = new String(b,"UTF-8");

		b = tea.teaEncrypt(b, sharedKey.getEncoded());
		commStream.sendBytes(b);

		// get pw prompt from server
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());

		// send pw
		b = commStream.getUserInput(new String(b,"UTF-8"), true);
		b = tea.teaEncrypt(b, sharedKey.getEncoded());
		commStream.sendBytes(b);

		// receive access-granted or not!
		b = commStream.receiveBytes();
		b = tea.teaDecrypt(b, sharedKey.getEncoded());
		String msg = new String(b, "UTF-8");
		msg = msg.trim();

		return msg.equals("access-granted");
	}

	private static void startFileSharing() throws Exception
	{
		while(true)
		{
			// send the filename!
			byte[] msg = commStream.getUserInput("Filename Request", false);
			String fname = new String(msg, "UTF-8");

			msg = tea.teaEncrypt(msg, sharedKey.getEncoded());
			commStream.sendBytes(msg);

			if(fname.equals("finished"))
				break;

			// check the ack message or error message
			msg = commStream.receiveBytes();
			msg = tea.teaDecrypt(msg, sharedKey.getEncoded());

			String sMsg = new String(msg, "UTF-8");
			System.out.println("Server: " + sMsg);

			if(sMsg.trim().equals("file found"))
			{
				msg = commStream.receiveBytes();
				msg = tea.teaDecrypt(msg, sharedKey.getEncoded());

				FileIo fio = new FileIo();
				if(!fio.createDir(username))
					throw new Exception("Failed to create user directory");

				if(!fio.createOutputFile(username + "/" + fname))
					throw new Exception("Failed to create file");

				Files.write(Paths.get(username + "/" + fname), msg);
				System.out.println("File has been created\n");
			}
		}
	}
}





