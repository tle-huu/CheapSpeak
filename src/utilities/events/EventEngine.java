package utilities.events;

import utilities.events.ConnectionEvent;
import utilities.events.DisconnectionEvent;
import utilities.events.Event;
import utilities.events.TextEvent;
import utilities.events.VoiceEvent;

public interface EventEngine
{

// DEFAULT METHOD

	default boolean handleEvent(Event event)
	{
		switch (event.type())
		{
			case CONNECTION:
				return handleConnection((ConnectionEvent) event);
				
			case DISCONNECTION:
				return handleDisconnection((DisconnectionEvent) event);
				
			case VOICE:
				return handleVoice((VoiceEvent) event);
				
			case TEXT:
				return handleText((TextEvent) event);
				
			default:
				return false;
		}
	}

// ABSTRACT METHODS
		
	public boolean handleConnection(ConnectionEvent event);

	public boolean handleDisconnection(DisconnectionEvent event);

	public boolean handleVoice(VoiceEvent event);

	public boolean handleText(TextEvent event);
}
