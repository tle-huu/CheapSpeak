package utilities.events;

import java.util.UUID;

public class RemoveRoomEvent extends Event
{

// PUBLIC METHODS

	public RemoveRoomEvent(final UUID uuid, final String name)
	{
		super(EventType.REMOVE_ROOM, uuid);
		name_ = name;
	}

	public String name()
	{
		return name_;
	}

// PRIVATE ATTRIBUTES

	final private String name_;

}