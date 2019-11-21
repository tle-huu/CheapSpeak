package utilities.events;

import java.util.UUID;

// A user has been disconnected
@SuppressWarnings("serial")
public class DisconnectionEvent extends Event
{

// PUBLIC METHODS
	
	// Constructor
	public DisconnectionEvent(final UUID uuid, final String userName)
	{
		super(EventType.DISCONNECTION, uuid);
		userName_ = userName;
	}
	
	public String userName()
	{
		return userName_;
	}
	
// PRIVATE ATTRIBUTE
	
	private final String userName_;
	
}
