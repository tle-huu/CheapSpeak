package utilities.events;

import java.util.UUID;

// A new user is connected
public class ConnectionEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 224199952016058665L;
	
// PUBLIC METHODS
	
	// Constructor
	public ConnectionEvent(UUID uuid, String userName)
	{
		super(EventType.CONNECTION, uuid);
		userName_ = userName;
	}
	
	public String userName()
	{
		return userName_;
	}
	
// PRIVATE ATTRIBUTE
	
	private String userName_;
}