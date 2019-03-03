package main;

import java.io.Serializable;
import java.util.LinkedList;

public class Client implements Serializable{
	private String username;
	private String password;
	private LinkedList<String> files = new LinkedList<String>();
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LinkedList<String> getFiles() {
		return files;
	}
	public void setFiles(LinkedList<String> files) {
		this.files = files;
	}
}
