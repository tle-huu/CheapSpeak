package utilities.events;

import java.util.UUID;

@SuppressWarnings("serial")
public class RemoveRoomEvent extends Event
{

// PUBLIC METHODS

	// Constructor
	public RemoveRoomEvent(final UUID uuid, final String roomName)
	{
		super(EventType.REMOVE_ROOM, uuid);
		roomName_ = roomName;
	}

	public String roomName()
	{
		return roomName_;
	}

// PRIVATE ATTRIBUTE

	private final String roomName_;

}
