package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;


public class ClientHandler extends Thread {
	Socket connectionSocket = null;
	BufferedReader clientInput = null;
	PrintWriter clientOutput = null;
	LinkedList<Client> clients = new LinkedList<Client>(); 
	LinkedList<String> usersFiles = new LinkedList<String>();
	
	public ClientHandler(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}
	
	@Override
	public void run() {
		try {
			clientInput = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			clientOutput = new PrintWriter(connectionSocket.getOutputStream(),true);

			boolean b = true;
			while(b) {
				String logOrReg = clientInput.readLine();
				switch(logOrReg) {
				case "LOG IN" :
					String username = clientInput.readLine();
					String password = clientInput.readLine();
					clients = loadClients(clients, "clients.out");
					usersFiles = loadFiles(usersFiles, "files.txt");
					int counter = 0;
					for (int i = 0; i < clients.size(); i++) {
						if(clients.get(i).getUsername().equals(username) && clients.get(i).getPassword().equals(password)) {
							clientOutput.println("true");
							while(true) {
								String choice = clientInput.readLine();
								if(choice.equals("upload")) {
									String fileName = upload();
									clients.get(i).getFiles().add(fileName);
								} else if(choice.equals("download")) {
									download();
								} else if(choice.equals("LIST")) {
									String username2 = clientInput.readLine();
									String list = list(username2);
									if(!list.equals("")) {
										String[] listArray = list.split("\t");
										String length = listArray.length + "";
										clientOutput.println(length);
										for (int j = 0; j < listArray.length; j++) {
											clientOutput.println(listArray[j]);
										}
									} else {
										clientOutput.println(0+"");
									}
								} else if(choice.equals("EXIT")) {
									saveClients(clients, "clients.out");
									clientOutput.println("EXIT OK");
									b = false;
									break;
								} 
							}
							break;
						}
						counter++;
					}
					if(counter==clients.size()) {
						clientOutput.println("false");
					}
					break;
				case "REGISTER" :
					String username1 = clientInput.readLine();
					String password1 = clientInput.readLine();
					clients = loadClients(clients, "clients.out");
					int counter1 = 0;
					for (int i = 0; i < clients.size(); i++) {
						if(clients.get(i).getUsername().equals(username1)) {
							clientOutput.println("false");
							break;
						}
						counter1++;
					}
					if(counter1 == clients.size()) {
						clientOutput.println("true");
						Client client = new Client();
						client.setUsername(username1);
						client.setPassword(password1);
						clients.add(client);
						saveClients(clients, "clients.out");
					}
					break;
					
				case "GUEST" :
					while(true) {
						String choice = clientInput.readLine();
						if(choice.equals("EXIT")) {
							clientOutput.println("EXIT OK");
							b = false;
							break;
						} else {
							download();
						}
					}
					break;
				case "EXIT" :
					saveClients(clients, "clients.out");
					clientOutput.println("EXIT OK");
					b = false;
					break;
				default :
					b = false;
				}
				
			}
			connectionSocket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String upload() {
		String fileName = "";
		try {
			int lineNumber = Integer.parseInt(clientInput.readLine());
			fileName = uniqueIdentifier();
			File file = new File(fileName+".txt");
			String line = "";
			for (int i = 0; i < lineNumber+1; i++) {
				line += clientInput.readLine() + "\n";
			}
			convertToFile(fileName+".txt", line);
			usersFiles.add(fileName);
			saveFiles(usersFiles, "files.txt");
			clientOutput.println(fileName);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}
	
	public void download() {
		try {
			String code = clientInput.readLine();
			usersFiles = loadFiles(usersFiles, "files.txt");
			int counter = 0;
			for (int i = 0; i < usersFiles.size(); i++) {
				if(usersFiles.get(i).equals(code)) {
					clientOutput.println("true");
					sendFile(code);
					break;
				}
				counter++;
			}
			if(counter==usersFiles.size()) {
				clientOutput.println("false");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String uniqueIdentifier() {
		String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		char[] symbols = new char[10];
		Random random = new Random();
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < symbols.length; i++) {
			 sb.append(alphanum.charAt(random.nextInt(alphanum.length())));
			
		}
		return sb.toString();
	}
	
	public void sendFile(String fileName) {
		try {
			BufferedOutputStream os = new BufferedOutputStream(connectionSocket.getOutputStream());
			byte[] buffer = new byte[1024];
			RandomAccessFile raf = new RandomAccessFile("resources/"+fileName+".txt", "r");
			int n;
			while(true) {
				n = raf.read(buffer);
				if(n==-1) {
					break;	
				}
				os.write(buffer,0,n);
				os.flush();
			}
			raf.close();
		//	os.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void convertToFile(String fileName, String text) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("resources/"+fileName)));
			for (int i = 0; i < text.length(); i++) {
				if(text.charAt(i)!='\n') {
					pw.print(text.charAt(i));
				} else {
					pw.println();
				}
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean doesItExist(String fileName) {
		File file = new File(fileName + ".txt");
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	public void saveClients(LinkedList<Client> clients, String fileClients) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("resources/"+fileClients)));
			for (int i = 0; i < clients.size(); i++) {
				out.writeObject(clients.get(i));
			}
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public LinkedList<Client> loadClients(LinkedList<Client> clients, String fileClients) {
		LinkedList<Client> clients1 = new LinkedList<Client>();
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("resources/"+fileClients)));
			try {
				while(true) {
					Client k= (Client) in.readObject();
					clients1.add(k);
				}
			} catch (EOFException e) {}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clients = clients1;

		return clients;
	}
	
	public String list(String username) {
		String list = "";
		for (int k = 0; k < clients.size(); k++) {
				if(clients.get(k).getUsername().equals(username)) {
					for (int j = 0; j < clients.get(k).getFiles().size(); j++) {
						list += (clients.get(k).getFiles().get(j)+"\t");
					}
				}
		}
		return list;
	}
	
	public LinkedList<String> loadFiles(LinkedList<String> files, String fileFiles) {
		LinkedList<String> files1 = new LinkedList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("resources/"+fileFiles));
			boolean end = false;
			while(!end) {
				String pom = in.readLine();
				if(pom == null) {
					end = true;
				} else {
					files1.add(pom);
				}
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		files = files1;
		return files;
	}
	
	public void saveFiles(LinkedList<String> files, String fileFiles) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter("resources/"+fileFiles));
			for (int i = 0; i < files.size(); i++) {
				out.print(files.get(i));
				out.println();
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
