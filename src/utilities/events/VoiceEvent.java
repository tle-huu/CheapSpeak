package utilities.events;

import java.util.UUID;

import utilities.SoundPacket;

/*
 * A voice packet message
 * 
 */
@SuppressWarnings("serial")
public class VoiceEvent extends Event
{

// PUBLIC METHODS
	
	// Constructor
	public VoiceEvent(final UUID uuid, final String userName, final SoundPacket soundPacket)
	{
		super(EventType.VOICE, uuid);
		userName_ = userName;
		soundPacket_ = soundPacket;
	}
	
	public String userName()
	{
		return userName_;
	}

	public SoundPacket soundPacket()
	{
		return soundPacket_;
	}

// PRIVATE ATTRIBUTE
	
	private final String      userName_;
	private final SoundPacket soundPacket_;
	
}
