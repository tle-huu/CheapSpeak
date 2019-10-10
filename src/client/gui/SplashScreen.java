package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class SplashScreen extends JWindow
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9207185183823731551L;

// PUBLIC METHODS
	
	// Constructor
	public SplashScreen()
	{      
		super();
		
		// Set the window
		this.setSize(220, 156);
		this.setLocationRelativeTo(null);
		
		// Set the background
		try
		{
			Image img = ImageIO.read(new File("splash_screen.png"));
			PanelBackground pbg = new PanelBackground(img);
			this.setContentPane(pbg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Set the bar
		bar_ = new JProgressBar();
		bar_.setMinimum(0);
		bar_.setMaximum(LAUNCHTIME * 100);
		bar_.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bar_.setForeground(Color.CYAN);
		this.getContentPane().add(bar_, BorderLayout.SOUTH);
		
		// Create the main frame
		wm_ = new WindowMain();
	    
		// Start the loading
	    Thread t = new Thread(new Loading());
	    t.start();      
	    
	    // Display the splash screen
		this.setVisible(true);
	}
	
// INNER CLASS
	
	class Loading implements Runnable
	{
		@Override
		public void run()
		{
			// Load the bar
			for (int val = 0; val <= bar_.getMaximum(); val++)
			{
				bar_.setValue(val);
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			// Close the splash screen
			setVisible(false);
			// Display the main frame
			wm_.start();
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private WindowMain   wm_;
	private JProgressBar bar_;
	
	private final int LAUNCHTIME = 1; // in seconds
}
