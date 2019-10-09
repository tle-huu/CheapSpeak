package utilities.events;

import java.util.UUID;

// Text message
public class TextEvent extends Event
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2870663892989407774L;
	
// PUBLIC METHODS

	// Constructor
	public TextEvent(UUID uuid, String userName, String textPacket)
	{
		super(EventType.TEXT, uuid);
		userName_ = userName;
		textPacket_ = textPacket;
	}
	
	public String userName()
	{
		return userName_;
	}
	
	public String textPacket()
	{
		return textPacket_;
	}

// PRIVATE ATTRIBUTES
	
	private final String userName_;
	private final String textPacket_;
}