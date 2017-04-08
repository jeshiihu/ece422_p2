/**
 * Java Program: Server side of client server communication
 *
 * Code is based on the following online source
 * http://introcs.cs.princeton.edu/java/84network/EchoServer.java.html
 */
import java.net.*;
import java.io.*;
import javax.crypto.*;
import java.nio.file.*;

public class Server
{
	static private String hostname = "127.0.0.1";
	static private int port = 16000;

	public static void main(String[] args) throws Exception
	{
		SecretKey pwKey = encryptShadowTxt();

		ServerSocket sock = startConnection();
		if(sock == null)
		{
			System.err.println("Error: Server failed to connect to port " + Integer.toString(port));
			return;
		}

		System.out.println("Server successfully connected!");

		while(true)
		{
			// a "blocking" call which waits until a connection is requested
			int clientPort;

           	Socket clientSock = sock.accept();
            clientPort = clientSock.getPort();
            System.err.println("Accepted connection from " + Integer.toString(clientPort));
            CommThread comm = new CommThread(clientSock, pwKey);
            comm.start();
		}

		// sock.close();
	}

	private static SecretKey encryptShadowTxt() throws Exception
	{
		FileIo fileIo = new FileIo();
		String fin = "unhashed.txt";
		String fout = "shadow.txt";

		if(!fileIo.validTxtFile(fin) || !fileIo.fileExists(fin))
			throw new Exception("Error: filenames must have the .txt extention");

		if(!fileIo.createOutputFile(fout))
			throw new Exception("Error: failed to create output file");

		KeyGenerator gen = KeyGenerator.getInstance("AES");
		SecretKey key = gen.generateKey();

		System.loadLibrary("tea");
		TEAEncryption tea = new TEAEncryption();

		// read the unhased file
		BufferedReader buf = new BufferedReader(new FileReader(fin));
		String line = "";		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

		// http://stackoverflow.com/questions/1769776/how-can-i-write-a-byte-array-to-a-file-in-java
		// http://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays/23292834
		while((line = buf.readLine()) != null)
		{
			String[] userPw = line.split(" ");

			String pw = userPw[1].replaceAll("(\\s||\\n)", "");
			byte[] pwBytes = pw.getBytes("UTF-8");
			byte[] encryptedPw = tea.teaEncrypt(pwBytes, key.getEncoded());

			String user = userPw[0] + " ";
			byte[] userBytes = user.getBytes("UTF-8");

			String nl = "\n";
			byte[] nlBytes = nl.getBytes("UTF-8");

			outputStream.write(userBytes);
			outputStream.write(encryptedPw);
			outputStream.write(nlBytes);
		}

		byte[] allData = outputStream.toByteArray( );
		Files.write(Paths.get(fout), allData);
		
		return key;
	}

	private static ServerSocket startConnection() throws Exception
	{
		ServerSocket sock = null;
		sock = new ServerSocket(port);

		return sock;
	}
}