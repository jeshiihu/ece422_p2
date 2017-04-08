/**
 * Java program: client
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.io.Console;
import java.nio.file.*;

public class Client {
	static private String hostname = "127.0.0.1";
	static private int port = 16000;
	static private SecretKey sharedKey;
	static private TEAEncryption tea = new TEAEncryption();
	static private CommStream commStream;

	public static void main(String[] args) throws Exception
	{
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
			// e.printStackTrace();
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
				fio.createOutputFile(fname);
				Files.write(Paths.get(fname), msg);
				System.out.println("File has been created\n");
			}
		}
	}
}





