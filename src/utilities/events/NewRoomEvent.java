package utilities.events;

import java.util.UUID;

public class NewRoomEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9024506760159173383L;

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

	final private String roomName_;
	
}
