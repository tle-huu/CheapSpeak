package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow
{

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
			Image img = ImageIO.read(new File(IMAGE_FILE_NAME));
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
		bar_.setMaximum(LAUNCH_TIME * 100);
		bar_.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bar_.setForeground(Color.CYAN);
		this.getContentPane().add(bar_, BorderLayout.SOUTH);
		
		// Create the main frame
		windowMain_ = new WindowMain();
	    
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
			for (int val = 0; val <= bar_.getMaximum(); ++val)
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
			windowMain_.start();
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private final WindowMain   windowMain_;
	private final JProgressBar bar_;
	
	private final String IMAGE_FILE_NAME = "splash_screen.png";
	
	private final int LAUNCH_TIME = 1; // in seconds
	
}
