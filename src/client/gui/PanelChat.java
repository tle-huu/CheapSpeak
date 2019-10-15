package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class PanelChat extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 394479055230289860L;

// PUBLIC METHODS
	
	// Constructor
	public PanelChat()
	{
		super();
		
		// Set the main panel
		this.setLayout(new BorderLayout());
		
		// Set the message panel
		messagePanel_ = new JPanel();
		messagePanel_.setLayout(new BoxLayout(messagePanel_, BoxLayout.Y_AXIS));
		messagePanel_.setBackground(backgroundColor_);
		JScrollPane jsp = new JScrollPane(messagePanel_);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(jsp, BorderLayout.CENTER);
		
		// Set the send panel
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		
		// Set the send text area
		sendTextArea_ = new JTextArea();
		sendTextArea_.setMargin(new Insets(5, 5, 5, 5));
		sendTextArea_.setFont(fontText_);
		sendTextArea_.setLineWrap(true);
		sendTextArea_.setWrapStyleWord(true);
		JScrollPane jspText = new JScrollPane(sendTextArea_);
		jspText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		south.add(jspText, BorderLayout.CENTER);
		
		// Set the send button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
		sendButton_ = new JButton("Send");
		sendButton_.setPreferredSize(new Dimension(80, 45));
		sendButton_.setFont(fontText_);
		buttonPanel.add(sendButton_, BorderLayout.CENTER);
		south.add(buttonPanel, BorderLayout.EAST);
		
		// Add south panel
		this.add(south, BorderLayout.SOUTH);
	}
	
	public JPanel messagePanel()
	{
		return messagePanel_;
	}
	
	public JTextArea sendTextArea()
	{
		return sendTextArea_;
	}
	
	public JButton sendButton()
	{
		return sendButton_;
	}
	
	public Color backgroundColor()
	{
		return backgroundColor_;
	}
	
	public Color otherMessageColor()
	{
		return otherMessageColor_;
	}
	
	public void pushMessage(String txt, String pseudo)
	{
		// Break if the message is empty
		if (txt.isEmpty())
		{
			return ;
		}
		
		// Create the panels for the message
		JPanel panel = new JPanel();
		JLabel pseudoLabel = new JLabel();
		JTextArea message = new JTextArea();
		JLabel timestamp = new JLabel();
		
		// Set the pseudo label
		pseudoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pseudoLabel.setFont(fontPseudo_);
		pseudoLabel.setText(pseudo);
		
		// Set the message label
		message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setEditable(false);
		message.setOpaque(false);
		message.setFont(fontText_);
		message.setText(txt);
		
		// Set the timestamp label
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String time = SDF.format(ts);
		timestamp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		timestamp.setFont(fontText_);
		timestamp.setText(time);
		
		// Set panel
		panel.setLayout(new BorderLayout());
		int width = messagePanel_.getWidth();
		int padding = (int) ((float) width * 0.25f);
		if (!pseudo.equals(WindowMain.Pseudo()))
		{
			panel.setBackground(otherMessageColor_);
			panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10 + padding, backgroundColor_));
		}
		else
		{
			panel.setBackground(userMessageColor_);
			panel.setBorder(BorderFactory.createMatteBorder(10, 10 + padding, 10, 10, backgroundColor_));
		}
		panel.add(pseudoLabel, BorderLayout.NORTH);
		panel.add(message, BorderLayout.CENTER);
		panel.add(timestamp, BorderLayout.EAST);

		// Set the size
		int height = (int) panel.getPreferredSize().getHeight();
		panel.setMaximumSize(new Dimension(width, height));
		
		// Add the panel to the window
		messagePanel_.add(panel);
		messagePanel_.revalidate();
	}
	
// PRIVATE ATTRIBUTES
	
	private JTextArea sendTextArea_;
	private JButton   sendButton_;
	private JPanel    messagePanel_;
	
	private Font fontText_ = new Font("Arial", Font.PLAIN, 14),
				 fontPseudo_ = new Font("Arial", Font.BOLD, 14);
	
	private Color backgroundColor_ = new Color(0xfffcba),
				  userMessageColor_ = new Color(0x78f054),
				  otherMessageColor_ = new Color(0xf5ffe8);
	
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
}