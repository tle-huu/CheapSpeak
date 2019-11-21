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
		for (String font: fonts)
		{
			System.out.println(font);
		}
	}
	
}
