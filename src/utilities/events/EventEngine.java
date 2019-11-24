package utilities.events;

import utilities.events.ConnectionEvent;
import utilities.events.DisconnectionEvent;
import utilities.events.Event;
import utilities.events.NewRoomEvent;
import utilities.events.RemoveRoomEvent;
import utilities.events.TextEvent;
import utilities.events.VoiceEvent;
import utilities.infra.Log;

public interface EventEngine
{

// DEFAULT METHOD

	default boolean handleEvent(Event event)
	{
		if (event == null)
		{
			Log.LOG(Log.Level.WARNING, "handleEvent has been used with a null event");
			return false;
		}

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

			case NEW_ROOM:
				return handleNewRoom((NewRoomEvent) event);

			case REMOVE_ROOM:
				return handleRemoveRoom((RemoveRoomEvent) event);

			case ENTER_ROOM:
				return handleEnterRoom((EnterRoomEvent) event);

			default:
				return false;
		}
	}

// ABSTRACT METHODS
		
	public boolean handleConnection(ConnectionEvent event);

	public boolean handleDisconnection(DisconnectionEvent event);

	public boolean handleEnterRoom(EnterRoomEvent event);

	public boolean handleNewRoom(NewRoomEvent event);

	public boolean handleRemoveRoom(RemoveRoomEvent event);

	public boolean handleText(TextEvent event);

	public boolean handleVoice(VoiceEvent event);

}
