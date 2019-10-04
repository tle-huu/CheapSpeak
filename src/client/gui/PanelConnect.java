package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.gui.WindowMain.ConnectListener;

public class PanelConnect extends JPanel
{
	
// PUBLIC METHODS

	// Constructor
	public PanelConnect(ConnectListener connectListener)
	{
		super();
		
		// Set the main panel
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());
		
		// Set the fonts
		Font fontTitle = new Font("Arial", Font.BOLD, 36);
		Font fontLabel = new Font("Arial", Font.BOLD, 24);
		Font fontField = new Font("Arial", Font.PLAIN, 18);
		
		// Set the positioner
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		// Set the title
		title_ = new JLabel("WELCOME");
		title_.setHorizontalAlignment(JLabel.CENTER);
		title_.setPreferredSize(new Dimension(250, 100));
		title_.setFont(fontTitle);
		
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(title_, gbc);
		
		// Set the pseudo field
		JPanel pseudoPanel = new JPanel();
		pseudoPanel.setBackground(Color.WHITE);
		pseudoPanel.setLayout(new BorderLayout());
		pseudoLabel_ = new JLabel("Pseudo");
		pseudoLabel_.setHorizontalAlignment(JLabel.CENTER);
		pseudoLabel_.setPreferredSize(new Dimension(250, 60));
		pseudoLabel_.setFont(fontLabel);
		pseudo_ = new JTextField();
		pseudo_.setPreferredSize(new Dimension(250, 30));
		pseudo_.setFont(fontField);
		pseudoPanel.add(pseudoLabel_, BorderLayout.NORTH);
		pseudoPanel.add(pseudo_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(pseudoPanel, gbc);
		
		// Set the IP address field
		JPanel ipAddressPanel = new JPanel();
		ipAddressPanel.setBackground(Color.WHITE);
		ipAddressPanel.setLayout(new BorderLayout());
		ipAddressLabel_ = new JLabel("IP Address");
		ipAddressLabel_.setHorizontalAlignment(JLabel.CENTER);
		ipAddressLabel_.setPreferredSize(new Dimension(250, 60));
		ipAddressLabel_.setFont(fontLabel);
		ipAddress_ = new JTextField();
		ipAddress_.setPreferredSize(new Dimension(250, 30));
		ipAddress_.setFont(fontField);
		ipAddressPanel.add(ipAddressLabel_, BorderLayout.NORTH);
		ipAddressPanel.add(ipAddress_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(ipAddressPanel, gbc);
		
		// Set the port field
		JPanel portPanel = new JPanel();
		portPanel.setBackground(Color.WHITE);
		portPanel.setLayout(new BorderLayout());
		portLabel_ = new JLabel("Port");
		portLabel_.setHorizontalAlignment(JLabel.CENTER);
		portLabel_.setPreferredSize(new Dimension(250, 60));
		portLabel_.setFont(fontLabel);
		port_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
		port_.setPreferredSize(new Dimension(250, 30));
		port_.setFont(fontField);
		portPanel.add(portLabel_, BorderLayout.NORTH);
		portPanel.add(port_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(portPanel, gbc);
		
		// Set the connect button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(200, 100));
		connectButton_ = new JButton("CONNECT");
		connectButton_.setPreferredSize(new Dimension(200, 50));
		connectButton_.setFont(fontLabel);
		connectButton_.setBorder(BorderFactory.createRaisedBevelBorder());
		connectButton_.addActionListener(connectListener);
		buttonPanel.add(connectButton_, BorderLayout.SOUTH);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(buttonPanel, gbc);
	}
	
	public String pseudo()
	{
		return pseudo_.getText();
	}
	
	public String host()
	{
		return ipAddress_.getText();
	}
	
	public int port()
	{
		return Integer.valueOf(port_.getText()).intValue();
	}
	
// PRIVATE ATTRIBUTES
	
	private JLabel title_, pseudoLabel_, ipAddressLabel_, portLabel_;
	private JTextField pseudo_, ipAddress_, port_;
	private JButton connectButton_;
}
