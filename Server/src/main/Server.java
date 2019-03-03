package main;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
	public static LinkedList<ClientHandler> onlineUsers = new LinkedList<>();
	public static LinkedList<String> files = new LinkedList<>();
	public static void main(String[] args) {
		int port = 11000;
		ServerSocket socket = null;
		Socket connectionSocket = null;
		try {
				socket = new ServerSocket(port);
			while(true) {
					System.out.println("Cekam na konekciju...");
					connectionSocket = socket.accept();
					System.out.println("Uspesno uspostavljena konekcija");
					
					ClientHandler client = new ClientHandler(connectionSocket);
					onlineUsers.add(client);
					client.start();
					} 
				
		} catch (IOException e) {
			System.out.println("Doslo je do greske prilikom pokretanja servera!!!");
		} 
	}
}
