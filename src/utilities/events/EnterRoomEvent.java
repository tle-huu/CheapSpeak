package utilities.events;

import java.util.UUID;

/*
 * A user enters in a room
 * 
 */
@SuppressWarnings("serial")
public class EnterRoomEvent extends Event
{
	
// PUBLIC METHODS

	// Constructor
    public EnterRoomEvent(final UUID uuid, final String userName, final String roomName)
    {
        super(Event.EventType.ENTER_ROOM, uuid);
        userName_ = userName;
        roomName_ = roomName;
    }

    public String roomName()
    {
    	return roomName_;
    }

    public String userName()
    {
    	return userName_;
    }

// PRIVATE ATTRIBUTES

    private final String userName_;
    private final String roomName_;

}
