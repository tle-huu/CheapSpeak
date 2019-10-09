package utilities.events;

import java.util.UUID;

// A user has been disconnected
public class DisconnectionEvent extends Event
{
	public DisconnectionEvent(UUID uuid)
	{
		super(EventType.DISCONNECTION, uuid);
	}
}