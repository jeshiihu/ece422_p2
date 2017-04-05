/**
 * Java class extending a thread
 * handles the communication with the client
 *
 * help from
 * http://stackoverflow.com/questions/5419328/multiple-client-to-server-communication-program-in-java
 */
import java.net.*;
import java.io.*;

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




