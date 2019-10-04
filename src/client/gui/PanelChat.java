package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import utilities.infra.Log;

public class PanelChat extends JPanel
{
	
// PUBLIC METHODS
	
	// Constructor
	public PanelChat()
	{
		super();
		
		// Set the main panel
		this.setLayout(new BorderLayout());
		
		// Set the fonts
		fontText_ = new Font("Arial", Font.PLAIN, 14);
		fontPseudo_ = new Font("Arial", Font.BOLD, 14);
		
		// Set the message panel
		messagePanel_ = new JPanel();
		messagePanel_.setLayout(new BoxLayout(messagePanel_, BoxLayout.Y_AXIS));
		messagePanel_.setBackground(Color.YELLOW);
		JScrollPane jsp = new JScrollPane(messagePanel_);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(jsp, BorderLayout.CENTER);
		
		// Set the text field
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		jta_ = new JTextArea();
		jta_.setMargin(new Insets(5, 5, 5, 5));
		jta_.setFont(fontText_);
		jta_.setLineWrap(true);
		jta_.setWrapStyleWord(true);
		JScrollPane jspText = new JScrollPane(jta_);
		jspText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		south.add(jspText, BorderLayout.CENTER);
		
		// Set the send button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
		sendButton_ = new JButton("Send");
		sendButton_.setPreferredSize(new Dimension(80, 45));
		sendButton_.setFont(fontText_);
		sendButton_.addActionListener(new SendListener());
		buttonPanel.add(sendButton_, BorderLayout.CENTER);
		south.add(buttonPanel, BorderLayout.EAST);
		
		// Add south panel
		this.add(south, BorderLayout.SOUTH);
	}
	
	public JPanel messagePanel()
	{
		return messagePanel_;
	}
	
	public void pushMessage(String txt, Timestamp ts, String pseudo)
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
		
		// Set pseudo label
		pseudoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pseudoLabel.setFont(fontPseudo_);
		pseudoLabel.setText(pseudo);
		
		// Set message label
		message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setEditable(false);
		message.setOpaque(false);
		message.setFont(fontText_);
		message.setText(txt);
		
		// Set timestamp label
		String time = SDF.format(ts);
		timestamp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		timestamp.setFont(fontText_);
		timestamp.setText(time);
		
		// Set panel
		panel.setLayout(new BorderLayout());
		if (!pseudo.equals(WindowMain.Pseudo()))
		{
			panel.setBackground(Color.WHITE);
			panel.setAlignmentX(LEFT_ALIGNMENT);
			panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.YELLOW));
		}
		else
		{
			panel.setBackground(Color.GREEN);
			panel.setAlignmentX(RIGHT_ALIGNMENT);
			panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.YELLOW));
		}
		panel.add(pseudoLabel, BorderLayout.NORTH);
		panel.add(message, BorderLayout.CENTER);
		panel.add(timestamp, BorderLayout.EAST);
		/*
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		int textWidth = (int)(fontText_.getStringBounds(txt, frc).getWidth());
		int width = Math.max(117, Math.min(this.getWidth() - 120, textWidth));
		*/
		int width = Math.max((int) ((float) messagePanel_.getWidth() * 0.75f), 117);
		int height = (int) panel.getPreferredSize().getHeight();
		Log.LOG(Log.Level.INFO, String.valueOf(height));
		panel.setMaximumSize(new Dimension(width, height));
		
		// Add panel on the window
		messagePanel_.add(panel);
		messagePanel_.revalidate();
	}
	
// INNER CLASS
	
	class SendListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Reset the focus on the text area
			jta_.grabFocus();
			// Push the message on the panel
			String txt = jta_.getText();
			jta_.setText("");
			Date date = new Date();
			Timestamp ts = new Timestamp(date.getTime());
			pushMessage(txt, ts, WindowMain.Pseudo());
			// Send the message to the server
			// ...
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private JTextArea jta_;
	private JButton sendButton_;
	private JPanel messagePanel_;
	
	private Font fontText_,
				 fontPseudo_;
	
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
}
