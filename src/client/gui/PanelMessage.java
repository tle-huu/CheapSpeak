package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class PanelMessage extends JPanel implements ThemeUI
{

// PUBLIC METHODS
	
	// Constructor
	public PanelMessage(final String pseudo, final String textMessage, final int width, final boolean self)
	{
		super();
		
		// Set the attributes
		pseudo_ = pseudo;
		textMessage_ = textMessage;
		self_ = self;
		
		// Set the border
		this.setLayout(new BorderLayout());
		
		// Set the pseudo label
		pseudoLabel_ = new JLabel();
		pseudoLabel_.setBorder(BORDER_LABEL);
		pseudoLabel_.setFont(UIManager.getFontResource("FONT_PSEUDO"));
		pseudoLabel_.setText(pseudo_);
		
		// Set the message label
		messageArea_ = new JTextArea();
		messageArea_.setBorder(BORDER_LABEL);
		messageArea_.setLineWrap(true);
		messageArea_.setWrapStyleWord(true);
		messageArea_.setEditable(false);
		messageArea_.setOpaque(false);
		messageArea_.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		messageArea_.setText(textMessage_);
		
		// Set the timestamp label
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String time = SDF.format(timestamp);
		timestampLabel_ = new JLabel();
		timestampLabel_.setBorder(BORDER_LABEL);
		timestampLabel_.setFont(UIManager.getFontResource("FONT_MESSAGE"));
		timestampLabel_.setText(time);
		
		// Add the labels
		this.add(pseudoLabel_, BorderLayout.NORTH);
		this.add(messageArea_, BorderLayout.CENTER);
		this.add(timestampLabel_, BorderLayout.EAST);

		// Set the size
		setWidth(width);
	}
	
	public int getPadding()
	{
		return (int) ((float) width_ * PADDING_PERCENT_MESSAGE);
	}
	
	public int getHeight()
	{
		return (int) this.getPreferredSize().getHeight();
	}
	
	public void setWidth(final int width)
	{
		// Set the new width
		width_ = width;
		
		// Update the padding
		setThemeUI();
		
		// Set the size
		this.setMaximumSize(new Dimension(width_, getHeight()));
	}
	
	@Override
	public void setThemeUI()
	{
		if (self_)
		{
			// User message
			this.setBackground(UIManager.getColorResource("USER_MESSAGE_COLOR"));
			this.setBorder(BorderFactory.createMatteBorder(
					DEFAULT_MARGIN_MESSAGE, 
					DEFAULT_MARGIN_MESSAGE + getPadding(), 
					DEFAULT_MARGIN_MESSAGE, 
					DEFAULT_MARGIN_MESSAGE, 
					UIManager.getColorResource("BACKGROUND_COLOR")));
		}
		else
		{
			// Other message
			this.setBackground(UIManager.getColorResource("OTHER_MESSAGE_COLOR"));
			this.setBorder(BorderFactory.createMatteBorder(
					DEFAULT_MARGIN_MESSAGE, 
					DEFAULT_MARGIN_MESSAGE, 
					DEFAULT_MARGIN_MESSAGE, 
					DEFAULT_MARGIN_MESSAGE + getPadding(), 
					UIManager.getColorResource("BACKGROUND_COLOR")));
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private JLabel    pseudoLabel_;
	private JTextArea messageArea_;
	private JLabel    timestampLabel_;
	
	private String  pseudo_;
	private String  textMessage_;
	private int     width_;
	private boolean self_;
	
	private final int              DEFAULT_MARGIN_MESSAGE = 10;
	private final float            PADDING_PERCENT_MESSAGE = 0.25f;
	private final Border           BORDER_LABEL = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");

}
