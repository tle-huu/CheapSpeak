import server.VocalServer;
import utilities.infra.Log;

public class Main
{
	public static void main(String[] argv) throws Exception
	{
		// Get the port
		int port;
		if (argv.length == 0)
		{
			port = DEFAULT_PORT;
		}
		else if (argv.length == 1)
		{
			port = Integer.valueOf(argv[0]).intValue();
			if (port < MIN_PORT || port > MAX_PORT)
			{
				Log.LOG(Log.Level.WARNING, "The port " + port + " is not supported");
				return ;
			} 
		}
		else
		{
			Log.LOG(Log.Level.WARNING, "Illegal number of arguments: 0 or 1 argument supported");
			return ;
		}
		
		// Create the server
		VocalServer server = new VocalServer(port);

		// Start the server
		server.start();
	}
	
	private static final int DEFAULT_PORT = 4242;
	private static final int MIN_PORT = 1025;
	private static final int MAX_PORT = 65535;

}