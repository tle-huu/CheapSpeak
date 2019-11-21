import server.VocalServer;

public class Main
{
	public static void main(String[] argv) throws Exception
	{
		VocalServer server = new VocalServer(4242);

		server.start();
	}

}