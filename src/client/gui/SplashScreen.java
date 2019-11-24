package client.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import utilities.infra.Log;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow
{

// PUBLIC METHODS
	
	// Constructor
	public SplashScreen()
	{      
		super();
		
		// Set the window
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		
		// Set the background
		try
		{
			Image img = ImageIO.read(new File(UIManager.getStringResource("SPLASHSCREEN_FILE_NAME")));
			PanelBackground panelBackground = new PanelBackground(img);
			this.setContentPane(panelBackground);
		}
		catch (IOException e)
		{
			Log.LOG(Log.Level.ERROR, "Cannot load the splash screen image");
		}
		
		// Set the bar
		bar_ = new JProgressBar();
		bar_.setMinimum(0);
		bar_.setMaximum(LAUNCH_TIME * 100);
		bar_.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bar_.setForeground(UIManager.getColorResource("LOADING_BAR_COLOR"));
		this.getContentPane().add(bar_, BorderLayout.SOUTH);
		
		// Create the main frame
		windowMain_ = new WindowMain();
	    
		// Start the loading
	    Thread loadingThread = new Thread(new Loading());
	    loadingThread.start();      
	    
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
					Thread.sleep(10L);
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
	
	private final int WIDTH = 220;
	private final int HEIGHT = 156;
	private final int LAUNCH_TIME = 1; // in seconds
	
}
