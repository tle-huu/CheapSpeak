
public class Log
{
	public enum Level
	{
		INFO,
		WARNING,
		ERROR
	};

	// TODO: Should know where to log the message
	public static void LOG(Level level, String message)
	{
		System.out.println(prefixed[level.ordinal()]  + " " + message);
	}

	final static private String[] prefixed = {
		"[INFO]",
		"[WARNING]",
		"[ERROR]",
	} ;

	private final static String client_log_file_ = "";
	private final static String server_log_file_ = "";

}