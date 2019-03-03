package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Register extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JLabel lblUnesiteKorisnickoIme;
	private JLabel lblUnesiteSifru;
	private JLabel lblPonoviteSifru;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JButton btnNapraviNalog;
	Socket connectionSocket;
	BufferedReader serverInput;
	PrintWriter serverOutput;
	
	public Register(Socket connectionSocket) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				serverOutput.println("EXIT");
				String serverResponse;
				try {
					serverResponse = serverInput.readLine();
					if(serverResponse.equals("EXIT OK")) {
						connectionSocket.close();
					}
				} catch (IOException a) {
					// TODO Auto-generated catch block
					a.printStackTrace();
				}
			}
		});
		setTitle("Register");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 311, 185);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getTextField());
		contentPane.add(getLblUnesiteKorisnickoIme());
		contentPane.add(getLblUnesiteSifru());
		contentPane.add(getLblPonoviteSifru());
		contentPane.add(getPasswordField());
		contentPane.add(getPasswordField_1());
		contentPane.add(getBtnNapraviNalog());
		setVisible(true);
		this.connectionSocket = connectionSocket;
		setIOStreams();
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setBounds(181, 36, 104, 20);
			textField.setColumns(10);
		}
		return textField;
	}
	private JLabel getLblUnesiteKorisnickoIme() {
		if (lblUnesiteKorisnickoIme == null) {
			lblUnesiteKorisnickoIme = new JLabel("Unesite korisnicko ime:");
			lblUnesiteKorisnickoIme.setBounds(10, 39, 130, 14);
		}
		return lblUnesiteKorisnickoIme;
	}
	private JLabel getLblUnesiteSifru() {
		if (lblUnesiteSifru == null) {
			lblUnesiteSifru = new JLabel("Unesite sifru:");
			lblUnesiteSifru.setBounds(10, 64, 111, 14);
		}
		return lblUnesiteSifru;
	}
	private JLabel getLblPonoviteSifru() {
		if (lblPonoviteSifru == null) {
			lblPonoviteSifru = new JLabel("Ponovite sifru:");
			lblPonoviteSifru.setBounds(10, 89, 111, 14);
		}
		return lblPonoviteSifru;
	}
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setBounds(181, 61, 104, 20);
		}
		return passwordField;
	}
	private JPasswordField getPasswordField_1() {
		if (passwordField_1 == null) {
			passwordField_1 = new JPasswordField();
			passwordField_1.setBounds(181, 86, 104, 20);
		}
		return passwordField_1;
	}
	private JButton getBtnNapraviNalog() {
		if (btnNapraviNalog == null) {
			btnNapraviNalog = new JButton("Napravi nalog");
			btnNapraviNalog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!passwordField.getText().equals(passwordField_1.getText())) {
						JOptionPane.showMessageDialog(null,"Pogresno ste ponovili sifru!");
						passwordField.setText("");
						passwordField_1.setText("");
					} else {
						try {
							
							serverOutput.println("REGISTER");
							serverOutput.println(textField.getText());
							serverOutput.println(passwordField.getText());
							String serverResponse = serverInput.readLine();
							if(serverResponse.equals("true")) {
								JOptionPane.showMessageDialog(null, "Uspesno ste napravili nalog!");
								LogIn login = new LogIn();
								dispose();
							} else if(serverResponse.equals("false")) {
								JOptionPane.showMessageDialog(null, "Korisnicko ime je vec zauzeto!");
								textField.setText("");
							}
							
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			
			});
			btnNapraviNalog.setBounds(91, 114, 124, 23);
		}
		return btnNapraviNalog;
	}
	private void setIOStreams() {
		try {
			serverOutput = new PrintWriter(connectionSocket.getOutputStream(),true);
			serverInput = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
