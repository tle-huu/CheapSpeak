package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PanelConnect extends JPanel implements ThemeUI
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
		title_ = new JLabel("Welcome");
		title_.setHorizontalAlignment(JLabel.CENTER);
		title_.setPreferredSize(TITLE_DIMENSION);
		title_.setFont(UIManager.getFontResource("FONT_TITLE"));
		
		// Update the constraints and add the title
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(title_, gbc);
		
		// Set the pseudo field
		pseudoPanel_ = new JPanel();
		pseudoPanel_.setLayout(new BorderLayout());
		pseudoLabel_ = new JLabel("Pseudo");
		pseudoLabel_.setHorizontalAlignment(JLabel.CENTER);
		pseudoLabel_.setPreferredSize(LABEL_DIMENSION);
		pseudoLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		pseudo_ = new JTextField(DEFAULT_PSEUDO);
		pseudo_.setPreferredSize(FIELD_DIMENSION);
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
		ipAddressLabel_.setPreferredSize(LABEL_DIMENSION);
		ipAddressLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		ipAddress_ = new JTextField(DEFAULT_IP_ADDRESS);
		ipAddress_.setPreferredSize(FIELD_DIMENSION);
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
		portLabel_.setPreferredSize(LABEL_DIMENSION);
		portLabel_.setFont(UIManager.getFontResource("FONT_LABEL"));
		port_ = new JTextField(DEFAULT_PORT);
		port_.setPreferredSize(FIELD_DIMENSION);
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
		buttonPanel_.setPreferredSize(BUTTON_PANEL_DIMENSION);
		connectButton_ = new JButton("CONNECT");
		connectButton_.setPreferredSize(BUTTON_DIMENSION);
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
		return pseudo_.getText().trim();
	}
	
	public String host()
	{
		return ipAddress_.getText().trim();
	}
	
	public int port()
	{
		// Convert the string into an integer
		int port;
		try
		{
			port = Integer.valueOf(port_.getText()).intValue();
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
	
	@Override
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
	
	private final String[] ANIMALS = {"Squirrel", "Wolverine", "Magpie", "Carp", "Swordfish", "Emu", "Swallow", 
			"Koala", "Rattlesnake", "Ostrich", "Grasshopper", "Owl", "Buffalo", "Toad", "Camel", "Fox", "Teddybear", 
			"Wolf", "Kitten", "Doggie", "Badger", "Beaver", "Mammoth", "Bigfoot", "Chameleon", "Dragonfly", 
			"Pheasant", "Cougar", "Starfish", "Sea lion", "Seal", "Dinosaur", "Kingfisher", "Zebra", "Human"};
	
	private final Random rand = new Random();
	
	private final String DEFAULT_PSEUDO = "Anonymous " + ANIMALS[rand.nextInt(ANIMALS.length)];
	private final String DEFAULT_IP_ADDRESS = "127.0.0.1";
	private final String DEFAULT_PORT = "4242";
	
	private final Dimension TITLE_DIMENSION = new Dimension(250, 100);
	private final Dimension LABEL_DIMENSION = new Dimension(250, 60);
	private final Dimension FIELD_DIMENSION = new Dimension(250, 30);
	private final Dimension BUTTON_PANEL_DIMENSION = new Dimension(200, 100);
	private final Dimension BUTTON_DIMENSION = new Dimension(200, 50);
	
}
