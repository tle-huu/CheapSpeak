package client.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import utilities.infra.Log;

public abstract class UIResources
{

// PROTECTED METHOD
	
	// Constructor
	protected UIResources()
	{
		Log.LOG(Log.Level.WARNING, "A UIResources object is being instanciated !");
	}
	
// PUBLIC ATTRIBUTES
	
	// File name
	public static final String SPLASHSCREEN_FILE_NAME = "assets/splash_screen.png";
	
	// Icons
	public static final Icon ROOM_ICON = new ImageIcon("assets/room_icon.png"),
							 CLIENT_ICON = new ImageIcon("assets/client_icon.png"),
							 SPEAKER_ICON = new ImageIcon("assets/speaker_icon.png");
	
	// Fonts
	private static final String FONT_NAME = "Arial";
	
	public static final Font FONT_MESSAGE = new Font(FONT_NAME, Font.PLAIN, 14),
							 FONT_PSEUDO = new Font(FONT_NAME, Font.BOLD, 14),
							 FONT_TITLE = new Font(FONT_NAME, Font.BOLD, 36),
		 	   				 FONT_LABEL = new Font(FONT_NAME, Font.BOLD, 24),
		 	   				 FONT_FIELD = new Font(FONT_NAME, Font.PLAIN, 18),
		 	   				 FONT_TREE_ROOM = new Font(FONT_NAME, Font.BOLD, 16),
		 	   				 FONT_TREE_CLIENT = new Font(FONT_NAME, Font.PLAIN, 16);
	
	// Color
	public static final Color LOADING_BAR_COLOR = Color.CYAN;
	
}
