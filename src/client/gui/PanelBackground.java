package client.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class PanelBackground extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5078444344797652105L;

// PUBLIC METHODS

	// Constructor
	public PanelBackground(final Image img)
	{
		img_ = img;
		this.setLayout(new BorderLayout());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.drawImage(img_, 0, 0, null);
	}
	
// PRIVATE ATTRIBUTES
	
	private final Image img_;
}
