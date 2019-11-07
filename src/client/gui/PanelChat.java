package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

@SuppressWarnings("serial")
public class PanelChat extends JPanel implements ThemeUI
{

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
		sendTextArea_.setFont(UIManager.getFontResource("FONT_MESSAGE"));
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
		sendButton_.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		buttonPanel.add(sendButton_, BorderLayout.CENTER);
		south.add(buttonPanel, BorderLayout.EAST);
		
		// Add south panel
		this.add(south, BorderLayout.SOUTH);
		
		// Set theme UI
		setThemeUI();
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
	
	public void pushMessage(final String textMessage, final String pseudo, final boolean self)
	{
		// Break if the message is empty
		textMessage.trim();
		if (textMessage.isEmpty())
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
		pseudoLabel.setFont(UIManager.getFontResource("FONT_PSEUDO"));
		pseudoLabel.setText(pseudo);
		
		// Set the message label
		message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setEditable(false);
		message.setOpaque(false);
		message.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		message.setText(textMessage);
		
		// Set the timestamp label
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String time = SDF.format(ts);
		timestamp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		timestamp.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		timestamp.setText(time);
		
		// Set panel
		panel.setLayout(new BorderLayout());
		int width = messagePanel_.getWidth();
		int padding = (int) ((float) width * 0.25f);
		if (!self)
		{
			panel.setBackground(UIManager.getColorResource("OTHER_MESSAGE_COLOR"));
			panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10 + padding, 
															UIManager.getColorResource("BACKGROUND_COLOR")));
		}
		else
		{
			panel.setBackground(UIManager.getColorResource("USER_MESSAGE_COLOR"));
			panel.setBorder(BorderFactory.createMatteBorder(10, 10 + padding, 10, 10, 
															UIManager.getColorResource("BACKGROUND_COLOR")));
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
	
	@Override
	public void setThemeUI()
	{
		// Message panel
		messagePanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
	}
	
// PRIVATE ATTRIBUTES
	
	private JTextArea sendTextArea_;
	private JButton   sendButton_;
	private JPanel    messagePanel_;
	
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
	
}
