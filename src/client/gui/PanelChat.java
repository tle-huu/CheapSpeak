package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

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
		JScrollPane jspMessagePanel = new JScrollPane(messagePanel_);
		jspMessagePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(jspMessagePanel, BorderLayout.CENTER);
		
		// Set the send panel
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.setBorder(BORDER_SEND_PANEL);
		
		// Set the send text area
		sendTextArea_ = new JTextArea();
		sendTextArea_.setMargin(TEXT_AREA_MARGIN);
		sendTextArea_.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		sendTextArea_.setLineWrap(true);
		sendTextArea_.setWrapStyleWord(true);
		JScrollPane jspTextArea = new JScrollPane(sendTextArea_);
		jspTextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		south.add(jspTextArea, BorderLayout.CENTER);
		
		// Set the send button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(BORDER_BUTTON);
		sendButton_ = new JButton("Send");
		sendButton_.setPreferredSize(BUTTON_DIMENSION);
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
	
	public List<PanelMessage> messages()
	{
		return messages_;
	}
	
	public void clearMessages()
	{
		messages_.clear();
	}
	
	public void pushMessage(final String textMessage, final String pseudo, final boolean self)
	{
		// Break if the message is empty
		if (textMessage.isEmpty())
		{
			return ;
		}
		
		// Create the message
		PanelMessage message = new PanelMessage(pseudo, textMessage, messagePanel_.getWidth(), self);
		
		// Add the message to the list
		messages_.add(message);
		
		// Put the message in the panel
		messagePanel_.add(message);
		messagePanel_.revalidate();
	}
	
	@Override
	public void setThemeUI()
	{
		// Message panel
		messagePanel_.setBackground(UIManager.getColorResource("BACKGROUND_COLOR"));
		
		// Messages
		for (PanelMessage message: messages_)
		{
			message.setThemeUI();
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private JTextArea sendTextArea_;
	private JButton   sendButton_;
	private JPanel    messagePanel_;
	
	private List<PanelMessage> messages_ = new ArrayList<PanelMessage>();
	
	private final Border    BORDER_SEND_PANEL = BorderFactory.createEmptyBorder(10, 20, 10, 20);
	private final Border    BORDER_BUTTON = BorderFactory.createEmptyBorder(10, 20, 10, 0);
	private final Insets    TEXT_AREA_MARGIN = new Insets(5, 5, 5, 5);
	private final Dimension BUTTON_DIMENSION = new Dimension(80, 45);
		
}
