package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utilities.infra.Log;

public class PanelConnect extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 926182585022582028L;

// PUBLIC METHODS

	// Constructor
	public PanelConnect()
	{
		super();
		
		// Set the main panel
		this.setBackground(backgroundColor_);
		this.setLayout(new GridBagLayout());
		
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
		title_.setFont(fontTitle_);
		
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(title_, gbc);
		
		// Set the pseudo field
		JPanel pseudoPanel = new JPanel();
		pseudoPanel.setBackground(backgroundColor_);
		pseudoPanel.setLayout(new BorderLayout());
		pseudoLabel_ = new JLabel("Pseudo");
		pseudoLabel_.setHorizontalAlignment(JLabel.CENTER);
		pseudoLabel_.setPreferredSize(new Dimension(250, 60));
		pseudoLabel_.setFont(fontLabel_);
		pseudo_ = new JTextField("xX_Anonymous_Xx");
		pseudo_.setPreferredSize(new Dimension(250, 30));
		pseudo_.setFont(fontField_);
		pseudoPanel.add(pseudoLabel_, BorderLayout.NORTH);
		pseudoPanel.add(pseudo_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(pseudoPanel, gbc);
		
		// Set the IP address field
		JPanel ipAddressPanel = new JPanel();
		ipAddressPanel.setBackground(backgroundColor_);
		ipAddressPanel.setLayout(new BorderLayout());
		ipAddressLabel_ = new JLabel("IP Address");
		ipAddressLabel_.setHorizontalAlignment(JLabel.CENTER);
		ipAddressLabel_.setPreferredSize(new Dimension(250, 60));
		ipAddressLabel_.setFont(fontLabel_);
		ipAddress_ = new JTextField("127.0.0.1");
		ipAddress_.setPreferredSize(new Dimension(250, 30));
		ipAddress_.setFont(fontField_);
		ipAddressPanel.add(ipAddressLabel_, BorderLayout.NORTH);
		ipAddressPanel.add(ipAddress_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(ipAddressPanel, gbc);
		
		// Set the port field
		JPanel portPanel = new JPanel();
		portPanel.setBackground(backgroundColor_);
		portPanel.setLayout(new BorderLayout());
		portLabel_ = new JLabel("Port");
		portLabel_.setHorizontalAlignment(JLabel.CENTER);
		portLabel_.setPreferredSize(new Dimension(250, 60));
		portLabel_.setFont(fontLabel_);
		port_ = new JTextField("4242");
		port_.setPreferredSize(new Dimension(250, 30));
		port_.setFont(fontField_);
		portPanel.add(portLabel_, BorderLayout.NORTH);
		portPanel.add(port_, BorderLayout.CENTER);
		
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(portPanel, gbc);
		
		// Set the connect button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor_);
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(200, 100));
		connectButton_ = new JButton("CONNECT");
		connectButton_.setPreferredSize(new Dimension(200, 50));
		connectButton_.setFont(fontLabel_);
		connectButton_.setBorder(BorderFactory.createRaisedBevelBorder());
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
		int port;
		try
		{
			port = Integer.valueOf(port_.getText()).intValue();
			Log.LOG(Log.Level.INFO, "Port used by the client: " + port);
		}
		catch(NumberFormatException e)
		{  
			return -1;  
		}
		return port;
	}
	
	public JButton connectButton()
	{
		return connectButton_;
	}
	
// PRIVATE ATTRIBUTES
	
	private JLabel title_, 
				   pseudoLabel_, 
				   ipAddressLabel_, 
				   portLabel_;
	
	private JTextField pseudo_, 
					   ipAddress_, 
					   port_;
	
	private JButton connectButton_;
	
	private Font fontTitle_ = new Font("Arial", Font.BOLD, 36),
				 fontLabel_ = new Font("Arial", Font.BOLD, 24),
				 fontField_ = new Font("Arial", Font.PLAIN, 18);
	
	private Color backgroundColor_ = new Color(0xFFFCBA);
}
