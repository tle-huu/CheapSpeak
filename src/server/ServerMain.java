package server;

import java.io.IOException;

import server.VocalServer;
import utilities.infra.Log;

public class ServerMain
{
    
// MAIN
	
	public static void main(String[] argv)
    {
        // Setting logger
        Log.id(Log.SERVER_ID);

        // Get the port
        int port = DEFAULT_PORT;
        if (argv.length == 1)
        {
            port = Integer.valueOf(argv[0]).intValue();
            if (port < MIN_PORT || port > MAX_PORT)
            {
                Log.LOG(Log.Level.FATAL, "The port " + port + " is not supported");
                return;
            }
        }
        else if (argv.length > 1)
        {
            Log.LOG(Log.Level.FATAL, "Illegal number of arguments: 0 or 1 argument supported");
            return;
        }

        // Create the server
        VocalServer server = null;
        try
        {
            server = new VocalServer(port);
        }
        catch (IOException e)
        {
        	Log.LOG(Log.Level.FATAL, "The server cannot be created");
        	return;
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.FATAL, e.getMessage());
            return;
        }

        // Start the server
        try
        {
            server.start();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.FATAL, "The server cannot start");
            return;
        }
    }
    
// PRIVATE CONST
	
    private static final int DEFAULT_PORT = 4242;
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT = 65535;

}
