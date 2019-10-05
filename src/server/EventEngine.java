import java.util.Vector;
import java.util.HashMap;

/*
 * Abstract class for events processing
 */
public abstract class EventEngine
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
	abstract protected boolean handle_connection(ConnectionEvent event);

	abstract protected boolean handle_disconnection(DisconnectionEvent event);

	abstract protected boolean handle_voice(VoiceEvent event);

	abstract protected boolean handle_text(TextEvent event);

	// CONST
	private final int EVENT_QUEUE_SIZE = 1024;

	Vector<Event> events_queue = new Vector<Event>(EVENT_QUEUE_SIZE);
}