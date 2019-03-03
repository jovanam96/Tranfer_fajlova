package main;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ClientWindow extends JFrame {

	private JPanel contentPane;
	private String username;
	private String password;
	private JLabel lblIzaberiteOpciju;
	private JRadioButton rdbtnUpload;
	private JRadioButton rdbtnDownload;
	private JLabel lblUnesiteTekstKoji;
	private JTextField textField_Code;
	private JLabel lblUnesiteKodFajla;
	private ButtonGroup bg;
	public static Socket connectionSocket;
	private JButton btnSend;
	BufferedReader serverInput;
	PrintWriter serverOutput;
	private JScrollPane scrollPane;
	private JTextArea textArea_Text;
	private JLabel lblKodZaVas;
	private JTextField textField;
	private JScrollPane scrollPane_1;
	private JTextArea textArea;
	private JButton btnList;

	public ClientWindow(String username, String password,Socket connectionSocket) {
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
		
		setTitle("Client");
		this.username = username;
		this.password = password;
		this.connectionSocket = connectionSocket;
		setIOStreams();
		createWindow();
		
	}

	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 501, 372);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getLblIzaberiteOpciju());
		contentPane.add(getRdbtnUpload());
		contentPane.add(getRdbtnDownload());
		contentPane.add(getLblUnesiteTekstKoji());
		contentPane.add(gettextField_Code());
		contentPane.add(getLblUnesiteKodFajla());
		bg = new ButtonGroup();
		bg.add(getRdbtnUpload());
		bg.add(getRdbtnDownload());
		contentPane.add(getBtnSend());
		contentPane.add(getScrollPane());
		contentPane.add(getLblKodZaVas());
		contentPane.add(getTextField());
		contentPane.add(getScrollPane_1());
		contentPane.add(getBtnList());
		setVisible(true);
	}
	

	private JLabel getLblIzaberiteOpciju() {
		if (lblIzaberiteOpciju == null) {
			lblIzaberiteOpciju = new JLabel("Izaberite opciju:");
			lblIzaberiteOpciju.setBounds(31, 11, 120, 14);
		}
		return lblIzaberiteOpciju;
	}
	private JRadioButton getRdbtnUpload() {
		if (rdbtnUpload == null) {
			rdbtnUpload = new JRadioButton("Upload");
			rdbtnUpload.setBounds(31, 32, 109, 23);
		}
		
		rdbtnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField_Code.setEnabled(false);
				if(rdbtnUpload.isSelected()) {
					textArea_Text.setEnabled(true);
				}
			}
		});
		if(username == null && password == null) {
			rdbtnUpload.setEnabled(false);
		}
		
		return rdbtnUpload;
	}
	private JRadioButton getRdbtnDownload() {
		if (rdbtnDownload == null) {
			rdbtnDownload = new JRadioButton("Download");
			rdbtnDownload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textArea_Text.setEnabled(false);
					if(rdbtnDownload.isSelected()) {
						textField.setText("");
						textField_Code.setEnabled(true);
					}
				}
			});
			rdbtnDownload.setBounds(31, 57, 109, 23);
		}
		return rdbtnDownload;
	}

	private JLabel getLblUnesiteTekstKoji() {
		if (lblUnesiteTekstKoji == null) {
			lblUnesiteTekstKoji = new JLabel("Unesite tekst koji zelite da konvertujete u fajl:");
			lblUnesiteTekstKoji.setBounds(31, 181, 271, 14);
		}
		return lblUnesiteTekstKoji;
	}
	
	private JLabel getLblUnesiteKodFajla() {
		if (lblUnesiteKodFajla == null) {
			lblUnesiteKodFajla = new JLabel("Unesite kod fajla koji zelite da skinete:");
			lblUnesiteKodFajla.setBounds(31, 101, 229, 14);
		}
		return lblUnesiteKodFajla;
	}
	private JTextField gettextField_Code() {
		if (textField_Code == null) {
			textField_Code = new JTextField();
			textField_Code.setBounds(31, 126, 120, 20);
			textField_Code.setColumns(10);
		}

		return textField_Code;
	}
	
	
	private JButton getBtnSend() {
		if (btnSend == null) {
			btnSend = new JButton("Send");
			btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(rdbtnDownload.isSelected()==false && rdbtnUpload.isSelected()==false) {
						JOptionPane.showMessageDialog(null, "Niste izabrali opciju!", "error", JOptionPane.ERROR_MESSAGE);
					}
					if(rdbtnUpload.isSelected() && textArea_Text.getText().equals("")){
						JOptionPane.showMessageDialog(null, "Niste uneli tekst!", "error", JOptionPane.ERROR_MESSAGE);
					} else if(rdbtnUpload.isSelected() && !textArea_Text.getText().equals("")) {
						String text = null;
						boolean isValid = false;
						try {
							while(!isValid) {
								text = textArea_Text.getText();
								if(text.length()<=500) {
									isValid = true;
									String choice = "upload";
									serverOutput.println(choice);
									sendTextToServer(text);
									textArea_Text.setText("");
									textField.setText(serverInput.readLine());
								} else {
									textArea_Text.setText("");
									JOptionPane.showMessageDialog(null, "Fajl moze da sadrzi najvise 500 karaktera!", "error", JOptionPane.ERROR_MESSAGE);
									break;
								}
							}

						} catch (IOException e) {
						textArea_Text.setText(null);
						}
					}
					
					if(rdbtnDownload.isSelected() && textField_Code.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Niste uneli kod!", "error", JOptionPane.ERROR_MESSAGE);
					} else if(rdbtnDownload.isSelected() && !textField_Code.getText().equals("")) {
						String choice = "download";
						serverOutput.println(choice);
						String code = textField_Code.getText();
						serverOutput.println(code);
						String serverResponse;
						try {
							serverResponse = serverInput.readLine();
							if(serverResponse.equals("true")) {
								receiveFile(code);
								JOptionPane.showMessageDialog(null, "Uspesno ste skinuli fajl!");
							} else {
								JOptionPane.showMessageDialog(null, "Uneli ste nepostojeci kod!", "error", JOptionPane.ERROR_MESSAGE);
								textField_Code.setText("");
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
					
			});
			btnSend.setBounds(374, 304, 89, 23);
		}
		return btnSend;
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setAutoscrolls(true);
			scrollPane.setBounds(20, 206, 443, 87);
			scrollPane.setViewportView(getTextArea_Text());
		}
		return scrollPane;
	}
	private JTextArea getTextArea_Text() {
		if (textArea_Text == null) {
			textArea_Text = new JTextArea();
		}
		return textArea_Text;
	}
	
	private JLabel getLblKodZaVas() {
		if (lblKodZaVas == null) {
			lblKodZaVas = new JLabel("Kod za vas fajl je:");
			lblKodZaVas.setBounds(31, 304, 109, 14);
		}
		return lblKodZaVas;
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEditable(false);
			textField.setBounds(146, 304, 114, 20);
			textField.setColumns(10);
		}
		return textField;
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
	
	private void sendTextToServer(String s)  {
		int counter = 0;
		for (int i = 0; i < s.length(); i++) {
			if(s.charAt(i)=='\n' || s.charAt(i)=='\r') {
				counter++;
			}
		}
		serverOutput.println(counter + "");
		serverOutput.println(s);
	}
	
	private void receiveFile(String fileName) {
		try {
			BufferedInputStream is = new BufferedInputStream(connectionSocket.getInputStream());
			RandomAccessFile raf = new RandomAccessFile("resources/"+fileName+".txt", "rw");
			int n;
			byte[] buffer = new byte[1024];
			while(true) {
				n = is.read(buffer,0,1024);
				raf.write(buffer,0,n);
				if(n<1024) {
					break;
				}
			}
			raf.close();
		//	is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane_1.setBounds(312, 11, 157, 140);
			scrollPane_1.setViewportView(getTextArea_1());
		}
		return scrollPane_1;
	}
	private JTextArea getTextArea_1() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
		}
		return textArea;
	}
	private JButton getBtnList() {
		if (btnList == null) {
			btnList = new JButton("Izlistaj direktorijum");
			if(username == null && password == null) {
				btnList.setEnabled(false);
			}
			btnList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					textArea.setText("");
					String choice = "LIST";
					serverOutput.println(choice);
					serverOutput.println(username);
					try {
						String length = serverInput.readLine();
						if(length.equals(0+"")) {
							JOptionPane.showMessageDialog(null, "Vas direktorijum je prazan!");
						}
						for (int i = 0; i < Integer.parseInt(length); i++) {
							String list = serverInput.readLine();
							textArea.setText(textArea.getText()+"\n"+list);
						}
						 	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			btnList.setBounds(312, 162, 157, 23);
		}
		return btnList;
	}

}
