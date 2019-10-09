package utilities.events;

import java.util.UUID;

// A user has been disconnected
public class DisconnectionEvent extends Event
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4325144575299946329L;

// PUBLIC METHODS
	
	// Constructor
	public DisconnectionEvent(UUID uuid, String userName)
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