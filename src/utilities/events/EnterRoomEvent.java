package utilities.events;

import java.util.UUID;

public class EnterRoomEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8743541814208394763L;
	
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

    final String userName_;
    final String roomName_;

}
