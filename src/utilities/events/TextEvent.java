package utilities.events;

import java.util.UUID;

// Text message
public class TextEvent extends Event
{
	
// PUBLIC
	
	public TextEvent(UUID uuid, String text_packet)
	{
		super(EventType.TEXT, uuid);
		text_packet_ = text_packet;
	}
	
	public String textPacket()
	{
		return text_packet_;
	}

// PRIVATE
	
	final private String text_packet_;
}