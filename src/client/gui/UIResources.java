package client.gui;

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
	
	// Icons
	public static final Icon ROOM_ICON = new ImageIcon("room_icon.png"),
							 CLIENT_ICON = new ImageIcon("client_icon.png"),
							 SPEAKER_ICON = new ImageIcon("speaker_icon.png");
	
	// Fonts
	public static final Font FONT_MESSAGE = new Font("Arial", Font.PLAIN, 14),
							 FONT_PSEUDO = new Font("Arial", Font.BOLD, 14),
							 FONT_TITLE = new Font("Arial", Font.BOLD, 36),
		 	   				 FONT_LABEL = new Font("Arial", Font.BOLD, 24),
		 	   				 FONT_FIELD = new Font("Arial", Font.PLAIN, 18),
		 	   				 FONT_TREE_ROOM = new Font("Arial", Font.BOLD, 16),
		 	   				 FONT_TREE_CLIENT = new Font("Arial", Font.PLAIN, 16);
	
}
