package utilities.events;

import utilities.infra.Log;

public interface EventEngine
{

// DEFAULT METHOD

	default boolean handleEvent(final Event event)
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
		
	public boolean handleConnection(final ConnectionEvent event);

	public boolean handleDisconnection(final DisconnectionEvent event);

	public boolean handleEnterRoom(final EnterRoomEvent event);

	public boolean handleNewRoom(final NewRoomEvent event);

	public boolean handleRemoveRoom(final RemoveRoomEvent event);

	public boolean handleText(final TextEvent event);

	public boolean handleVoice(final VoiceEvent event);

}
