package utilities.infra;

import java.io.Writer;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

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

// PUBLIC METHOD
	
	public static void LOG(Level level, Object message)
	{
		String formatted_msg = level + " " + message.toString();
		if (logFile == null)
		{
			System.out.println(formatted_msg);
			return;
		}

		// if logFile is not null, it means that we have created a printWriter
	    printWriter.println(formatted_msg);
	    printWriter.flush();
	}

	public static void id(int id)
	{
		if (printWriter != null)
		{
			printWriter.close();
		}

		switch (id)
		{
			case SERVER_ID:
				logFile = LOG_FILE_SERVER;
				break;
			case CLIENT_ID:
				logFile = LOG_FILE_CLIENT;
				break;
			case STD_OUT_ID:
				logFile = null;
				break;
			default:
				logFile = DEFAUT_LOG_FILE;
		}

		try
		{
			file = new File(logFile);
		    file.getParentFile().mkdirs();

		    fileWriter = new FileWriter(file, true);
		    printWriter = new PrintWriter(fileWriter);

		}
		catch (IOException e)
		{
			System.out.println("Logger internal fatal error: " + e);

			// Still printing in standard output
			logFile = null;
		}

	}

// PUBLIC CONST

	static public final int SERVER_ID = 0;
	static public final int CLIENT_ID = 1;
	static public final int STD_OUT_ID = 2;

// PRIVATE

	static private final String DEFAUT_LOG_FILE = "/tmp/cheap_speak/default.log";
	static private final String LOG_FILE_SERVER = "/tmp/cheap_speak/server.log";
	static private final String LOG_FILE_CLIENT = "/tmp/cheap_speak/client.log";

	static private String logFile = null;


	static private File file = null;
	static private FileWriter fileWriter = null;
	static private PrintWriter printWriter = null;

}
