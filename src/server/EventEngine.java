import java.util.Vector;
import java.util.HashMap;

/*
 * Process Events
 */
public class EventEngine
{


// PUBLIC

	public boolean handle_event(Event event)
	{
		switch (event.type())
		{
			case CONNECTION:
				return handle_connection( (ConnectionEvent) event);
			case DISCONNECTION:
				return handle_disconnection( (DisconnectionEvent) event);
			case VOICE:
				return handle_voice( (VoiceEvent) event);
			case TEXT:
				return handle_text( (TextEvent) event);
			default:
				return false;
		}
	}

// PRIVATE

	private boolean handle_connection(ConnectionEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_connection");
		return true;
	}

	private boolean handle_disconnection(DisconnectionEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_disconnection");
		return true;
	}

	private boolean handle_voice(VoiceEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_voice");
		return true;
	}

	private boolean handle_text(TextEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_text");
		return true;
	}

	// CONST
	private final int EVENT_QUEUE_SIZE = 1024;

	Vector<Event> events_queue = new Vector<Event>(EVENT_QUEUE_SIZE);
}