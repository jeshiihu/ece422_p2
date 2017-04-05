/**
 * Java Program: Server side of client server communication
 *
 * Code is based on the following online source
 * http://introcs.cs.princeton.edu/java/84network/EchoServer.java.html
 */
import java.net.*;

public class Server
{
	static private String hostname = "127.0.0.1";
	static private int port = 16000;

	public static void main(String[] args) throws Exception
	{
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
            System.err.println("Accepted connection from client " + Integer.toString(clientPort));
            CommThread comm = new CommThread(sock, clientSock);
            comm.start();
		}
	}

	private static ServerSocket startConnection() throws Exception
	{
		ServerSocket sock = null;
		sock = new ServerSocket(port);

		return sock;
	}
}