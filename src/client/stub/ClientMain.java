public class ClientMain
{
	static public void main(String[] argv) throws Exception
	{
        if (argv.length != 2)
        {
            System.out.println("host and port fdp");
            return ;
        }
		
        String host = argv[0];
        String port = argv[1];
    	
        Client client = new Client(argv[0], 4242);
        client.start_listening_thread();
        client.start_recording_thread();
	}
}
