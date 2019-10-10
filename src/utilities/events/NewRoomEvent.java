package utilities.events;

import java.util.UUID;

public class NewRoomEvent extends Event
{


// PUBLIC METHODS

	public NewRoomEvent(final UUID uuid, final String name)
	{
		super(EventType.NEW_ROOM, uuid);
		name_ = name;
	}


	public String name()
	{
		return name_;
	}

// PRIVATE ATTRIBUTES

	final private String name_;

}