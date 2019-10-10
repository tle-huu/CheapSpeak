package utilities.events;

import java.util.UUID;

public class LeaveRoomEvent extends Event
{

// PUBLIC

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

// PRIVATE

    final String userName_;
    final String roomName_;
}
