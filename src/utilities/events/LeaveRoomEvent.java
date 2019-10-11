package utilities.events;

import java.util.UUID;

// TO BE REMOVED
public class LeaveRoomEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8340936016340909151L;
	
// PUBLIC METHODS
	
	// Constructor
    public LeaveRoomEvent(final UUID uuid, final String userName, final String roomName)
    {
        super(EventType.LEAVE_ROOM, uuid);
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

    final String userName_;
    final String roomName_;
}
