package utilities.events;

import java.util.UUID;

import utilities.SoundPacket;

// Voice packet message
public class VoiceEvent extends Event
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3130208610591615987L;

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