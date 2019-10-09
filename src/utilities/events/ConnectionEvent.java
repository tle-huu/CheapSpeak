package utilities.events;

import java.util.UUID;

// A new user is connected
public class ConnectionEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 224199952016058665L;
	
// PUBLIC METHOD
	
	// Constructor
	public ConnectionEvent(UUID uuid, String room, String userName)
	{
		super(EventType.CONNECTION, uuid);
		room_ = room;
		userName_ = userName;
	}
	
	public String room()
	{
		return room_;
	}
	
	public String userName()
	{
		return userName_;
	}
	
// PRIVATE ATTRIBUTES
	
	private String room_;
	private String userName_;
}