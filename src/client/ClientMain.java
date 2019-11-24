package client;

import client.gui.SplashScreen;
import utilities.infra.Log;

public class ClientMain
{

// MAIN
	
	public static void main(String[] args)
	{
		// Setting logger
        Log.id(Log.CLIENT_ID);

        // Launch the app
		new SplashScreen();
	}
	
}
