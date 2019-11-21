package utilities.infra;

/*
 * Single class for logging purpose
 *
 */
public class Log
{
	
// PUBLIC ENUM
	
	public enum Level
	{
	
	// INSTANCES
		
		INFO("[INFO]"),
		WARNING("[WARNING]"),
		ERROR("[ERROR]"),
		FATAL("[FATAL]"),
		DEBUG("[DEBUG]");
		
	// PRIVATE CONSTRUCTOR
		
		private Level(String prefix)
		{
			prefix_ = prefix;
		}
		
	// PUBLIC METHOD
		
		@Override
		public String toString()
		{
			return prefix_;
		}
		
	// PRIVATE ATTRIBUTE
		
		private String prefix_;
		
	};

	// TODO: Should know where to log the message (standard output or file)
	public static void LOG(Level level, Object message)
	{
		System.out.println(level + " " + message.toString());
	}

// PRIVATE

	private final static String client_log_file_ = "";
	private final static String server_log_file_ = "";

}