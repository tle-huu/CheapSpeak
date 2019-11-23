package server;

import java.io.IOException;

import server.VocalServer;
import utilities.infra.Log;

public class ServerMain
{
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
                return ;
            }
        }
        else if (argv.length > 1)
        {
            Log.LOG(Log.Level.FATAL, "Illegal number of arguments: 0 or 1 argument supported");
            return ;
        }

        // Create the server
        VocalServer server = null;
        try
        {
            server = new VocalServer(port);
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.FATAL, "The server cannot be created");
            return ;
        }

        if (server == null)
        {
            Log.LOG(Log.Level.FATAL, "The server could not be created");
            return ;
        }

        // Start the server
        try
        {
            server.start();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.FATAL, "The server cannot start");
            return ;
        }
    }
    
    private static final int DEFAULT_PORT = 4242;
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT = 65535;

}
