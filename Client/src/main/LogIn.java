package main;


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogIn extends JFrame {

	private JPanel contentPane;
	private JTextField textField_Username;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JButton btnLogIn;
	private JLabel lblNemateNalog;
	private JButton btnRegister;
	private JButton btnNastaviBezNaloga;
	
	private JPasswordField passwordField;
	public static Socket connectionSocket = null;
	public static BufferedReader serverInput;
	public static PrintWriter serverOutput;
	public static LogIn frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			connectionSocket = new Socket("localhost", 11000);
			serverInput = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			serverOutput = new PrintWriter(connectionSocket.getOutputStream(),true);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					frame = new LogIn();
				}
			});
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Doslo je do greske prilikom  uspostavljanja konekcije", "error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Create the frame.
	 */
	public LogIn() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				serverOutput.println("EXIT");
				String serverResponse;
				try {
					serverResponse = serverInput.readLine();
					if(serverResponse.equals("EXIT OK")) {
						connectionSocket.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		setTitle("Log in");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getTextField_Username());
		contentPane.add(getLblUsername());
		contentPane.add(getLblPassword());
		contentPane.add(getBtnLogIn());
		contentPane.add(getLblNemateNalog());
		contentPane.add(getBtnRegister());
		contentPane.add(getBtnNastaviBezNaloga());
		contentPane.add(getPasswordField());
		setVisible(true);

	}
	private JTextField getTextField_Username() {
		if (textField_Username == null) {
			textField_Username = new JTextField();
			textField_Username.setBounds(131, 39, 157, 20);
			textField_Username.setColumns(10);
		}
		return textField_Username;
	}
	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Username");
			lblUsername.setBounds(131, 14, 66, 14);
		}
		return lblUsername;
	}
	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password");
			lblPassword.setBounds(131, 70, 66, 14);
		}
		return lblPassword;
	}
	private JButton getBtnLogIn() {
		if (btnLogIn == null) {
			btnLogIn = new JButton("Log in");
			btnLogIn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					 if(textField_Username.getText().equals("")) {
						 JOptionPane.showMessageDialog(null, "Niste uneli korisnicko ime!", "error", JOptionPane.ERROR_MESSAGE);
					 }else if(passwordField.getText().equals("")) {
						 JOptionPane.showMessageDialog(null, "Niste uneli sifru!", "error", JOptionPane.ERROR_MESSAGE);
					 }else {
						 try {
							serverOutput.println("LOG IN"); 
							serverOutput.println(textField_Username.getText());
							serverOutput.println(passwordField.getText());
							String serverResponse = serverInput.readLine();
							if(serverResponse.equals("true")) {
								ClientWindow client = new ClientWindow(textField_Username.getText(), passwordField.getText(),connectionSocket);
								dispose();
							} else if(serverResponse.equals("false")) {
								JOptionPane.showMessageDialog(null, "Pogresno ste uneli username ili password!", "error", JOptionPane.ERROR_MESSAGE);
							}
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
					 }
				}
			});
			btnLogIn.setBounds(166, 126, 80, 23);
		}
		return btnLogIn;
	}
	private JLabel getLblNemateNalog() {
		if (lblNemateNalog == null) {
			lblNemateNalog = new JLabel("Nemate nalog?");
			lblNemateNalog.setBounds(98, 167, 89, 14);
		}
		return lblNemateNalog;
	}
	private JButton getBtnRegister() {
		if (btnRegister == null) {
			btnRegister = new JButton("Register");
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Register register = new Register(connectionSocket);
					dispose();
				}
			});
			btnRegister.setBounds(197, 163, 106, 23);
		}
		return btnRegister;
	}
	private JButton getBtnNastaviBezNaloga() {
		if (btnNastaviBezNaloga == null) {
			btnNastaviBezNaloga = new JButton("Nastavi bez naloga");
			btnNastaviBezNaloga.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					serverOutput.println("GUEST");
					ClientWindow client = new ClientWindow(null, null,connectionSocket);
					dispose();
				}
			});
			btnNastaviBezNaloga.setBounds(131, 206, 157, 23);
		}
		return btnNastaviBezNaloga;
	}
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setBounds(131, 95, 157, 20);
		}
		return passwordField;
	}
	
}
