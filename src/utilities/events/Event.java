package utilities.events;

import java.io.Serializable;
import java.util.Date;  
import java.util.UUID;

/*
 * Abstract class representing Event packet sent over the network
 *
 * An event is the main communication system between clients and the server
 * An event implements the Serializable interface to be able to be sent as an object
 */
public abstract class Event implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8464650833482806221L;

// PUBLIC METHODS
	
	public enum EventType
	{
		CONNECTION,
		DISCONNECTION,
		VOICE,
		TEXT,
		HANDSHAKE,
		NEW_ROOM,
		REMOVE_ROOM,
		ENTER_ROOM,
		LEAVE_ROOM,// TO BE REMOVED
		MAX_EVENT_TYPE;
	}

	public final EventType type()
	{
		return type_;
	}
	
	public final Date timestamp()
	{
		return timestamp_;
	}

	public final UUID uuid()
	{
		return uuid_;
	}

	public void uuid(UUID uuid)
	{
		uuid_ = uuid;
	}

// PROTECTED METHOD
	
	// Constructor
	protected Event(EventType event_type, UUID uuid)
	{
		type_ = event_type;
		uuid_ = uuid;
	}

// PROTECTED ATTRIBUTES

	// Event type
	protected final EventType type_;

	// Creation timestamp
	protected final Date timestamp_ = new Date();

	// UUID of the event sender
	protected UUID uuid_;
}
