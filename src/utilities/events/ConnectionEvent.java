package utilities.events;

import java.util.UUID;

/*
 * A new user is connected
 * 
 */
@SuppressWarnings("serial")
public class ConnectionEvent extends Event
{
	
// PUBLIC METHODS
	
	// Constructor
	public ConnectionEvent(final UUID uuid, final String userName)
	{
		super(EventType.CONNECTION, uuid);
		userName_ = userName;
	}
	
	public String userName()
	{
		return userName_;
	}
	
// PRIVATE ATTRIBUTE
	
	private final String userName_;
	
}
