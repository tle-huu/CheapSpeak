package client.gui;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

public class Main
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
