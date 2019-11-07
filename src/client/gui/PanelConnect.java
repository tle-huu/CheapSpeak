package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utilities.infra.Log;

@SuppressWarnings("serial")
public class PanelConnect extends JPanel
{

// PUBLIC METHODS

	// Constructor
	public PanelConnect()
	{
		super();
		
		// Set the positioner
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		// Set the title
		title_ = new JLabel("WELCOME");
		title_.setHorizontalAlignment(JLabel.CENTER);
		title_.setPreferredSize(new Dimension(250, 100));
		title_.setFont(UIManager.getFontResource("FONT_TITLE"));
		
		// Update the constraints and add the title
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(title_, gbc);
		
		// Set the pseudo field
		pseudoPanel_ = new JPanel();
		pseudoPanel_.setLayout(new BorderLayout());
		pseudoLabel_ = new JLabel("Pseudo");
		pseudoLabel_.setHorizontalAlignment(JLabel.CENTER);
		pseudoLabel_.setPreferredSize(new Dimension(250, 60));
		pseudoLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		pseudo_ = new JTextField("xX_Anonymous_Xx");
		pseudo_.setPreferredSize(new Dimension(250, 30));
		pseudo_.setFont(UIManager.getFontResource("FONT_FIELD"));
		pseudoPanel_.add(pseudoLabel_, BorderLayout.NORTH);
		pseudoPanel_.add(pseudo_, BorderLayout.CENTER);
		
		// Update the constraints and add the pseudo panel
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(pseudoPanel_, gbc);
		
		// Set the IP address field
		ipAddressPanel_ = new JPanel();
		ipAddressPanel_.setLayout(new BorderLayout());
		ipAddressLabel_ = new JLabel("IP Address");
		ipAddressLabel_.setHorizontalAlignment(JLabel.CENTER);
		ipAddressLabel_.setPreferredSize(new Dimension(250, 60));
		ipAddressLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		ipAddress_ = new JTextField("127.0.0.1");
		ipAddress_.setPreferredSize(new Dimension(250, 30));
		ipAddress_.setFont(UIManager.getFontResource("FONT_FIELD"));
		ipAddressPanel_.add(ipAddressLabel_, BorderLayout.NORTH);
		ipAddressPanel_.add(ipAddress_, BorderLayout.CENTER);
		
		// Update the constraints and add the IP address panel
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(ipAddressPanel_, gbc);
		
		// Set the port field
		portPanel_ = new JPanel();
		portPanel_.setLayout(new BorderLayout());
		portLabel_ = new JLabel("Port");
		portLabel_.setHorizontalAlignment(JLabel.CENTER);
		portLabel_.setPreferredSize(new Dimension(250, 60));
		portLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		port_ = new JTextField("4242");
		port_.setPreferredSize(new Dimension(250, 30));
		port_.setFont(UIManager.getFontResource("FONT_FIELD"));
		portPanel_.add(portLabel_, BorderLayout.NORTH);
		portPanel_.add(port_, BorderLayout.CENTER);
		
		// Update the constraints and add the port panel
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(portPanel_, gbc);
		
		// Set the connect button
		buttonPanel_ = new JPanel();
		buttonPanel_.setLayout(new BorderLayout());
		buttonPanel_.setPreferredSize(new Dimension(200, 100));
		connectButton_ = new JButton("CONNECT");
		connectButton_.setPreferredSize(new Dimension(200, 50));
		connectButton_.setFont(UIManager.getFontResource("FONT_LABEL"));
		connectButton_.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel_.add(connectButton_, BorderLayout.SOUTH);
		
		// Update the constraints and add the connect button
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(buttonPanel_, gbc);
		
		// Set theme UI
		setThemeUI();
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
	
	public void setThemeUI()
	{
		// Main panel
		this.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		
		// Title
		title_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_LABEL_COLOR"));
		
		// Pseudo field
		pseudoPanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		pseudoLabel_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_LABEL_COLOR"));
		pseudo_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_FIELD_COLOR"));
		
		// IP address field
		ipAddressPanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		ipAddressLabel_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_LABEL_COLOR"));
		ipAddress_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_FIELD_COLOR"));
		
		// Port field
		portPanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		portLabel_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_LABEL_COLOR"));
		port_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_FIELD_COLOR"));
		
		// Connect button
		buttonPanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		connectButton_.setForeground(UIManager.getColorResource("PANEL_CONNECT_FONT_FIELD_COLOR"));
	}
	
// PRIVATE ATTRIBUTES
	
	private JPanel pseudoPanel_,
				   ipAddressPanel_,
				   portPanel_,
				   buttonPanel_;
	
	private JLabel title_, 
				   pseudoLabel_, 
				   ipAddressLabel_, 
				   portLabel_;
	
	private JTextField pseudo_, 
					   ipAddress_, 
					   port_;
	
	private JButton connectButton_;
	
}
