import java.util.UUID;

// Text message
public class TextEvent extends Event
{
	public TextEvent(UUID uuid, String text_packet)
	{
		super(EventType.TEXT, uuid);
		text_packet_ = text_packet;
	}

// PRIVATE
	final private String text_packet_;
}