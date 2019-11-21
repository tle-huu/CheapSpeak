package utilities.events;

import java.util.UUID;

// Text message
@SuppressWarnings("serial")
public class TextEvent extends Event
{
	
// PUBLIC METHODS

	// Constructor
	public TextEvent(final UUID uuid, final String userName, final String textPacket)
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
