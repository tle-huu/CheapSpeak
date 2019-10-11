package utilities.events;

import utilities.events.ConnectionEvent;
import utilities.events.DisconnectionEvent;
import utilities.events.Event;
import utilities.events.NewRoomEvent;
import utilities.events.RemoveRoomEvent;
import utilities.events.TextEvent;
import utilities.events.VoiceEvent;

public interface EventEngine
{

// DEFAULT METHOD

	default boolean handleEvent(Event event)
	{
		assert event != null : "HandleEvent received a null event";

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

			case CHANGE_PSEUDO:
				return handleChangePseudo((ChangePseudoEvent) event);
				
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
	
	public boolean handleChangePseudo(ChangePseudoEvent event);

}
