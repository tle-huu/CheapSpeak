package client.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelBackground extends JPanel
{

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
	
// PRIVATE ATTRIBUTE
	
	private final Image img_;
	
}
