/*
 * Single class for logging purpose
 *
 */
public class Log
{
	public enum Level
	{
		INFO,
		WARNING,
		ERROR,
		FATAL,
		DEBUG
	};

	// TODO: Should know where to log the message (standard output or file])
	public static void LOG(Level level, String message)
	{
		System.out.println(prefixed[level.ordinal()]  + " " + message);
	}


// PRIVATE
	final static private String[] prefixed = {
		"[INFO]",
		"[WARNING]",
		"[ERROR]",
		"[FATAL]",
		"[DEBUG]"
	} ;

	private final static String client_log_file_ = "";
	private final static String server_log_file_ = "";

}