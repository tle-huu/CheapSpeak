package utilities.events;

import java.util.UUID;

@SuppressWarnings("serial")
public class NewRoomEvent extends Event
{

// PUBLIC METHODS

	// Constructor
	public NewRoomEvent(final UUID uuid, final String roomName)
	{
		super(EventType.NEW_ROOM, uuid);
		roomName_ = roomName;
	}

	public String roomName()
	{
		return roomName_;
	}

// PRIVATE ATTRIBUTE

	private final String roomName_;
	
}
