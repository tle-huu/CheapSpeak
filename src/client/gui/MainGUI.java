package client.gui;

import java.awt.GraphicsEnvironment;

public class MainGUI
{

// MAIN
	
	public static void main(String[] args)
	{
		new SplashScreen();
		//getFonts();
	}
	
// PUBLIC METHODS
	
	public static void getFonts()
	{
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < fonts.length; i++)
		{
			System.out.println(fonts[i]);
		}
	}
}
