package utilities.events;

import java.util.UUID;

public class RemoveRoomEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8474946417409930932L;

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

	final private String roomName_;

}
