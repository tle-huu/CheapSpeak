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
	public VoiceEvent(UUID uuid, SoundPacket soundPacket)
	{
		super(EventType.VOICE, uuid);
		soundPacket_ = soundPacket;
	}

	public final SoundPacket soundPacket()
	{
		return soundPacket_;
	}

// PRIVATE ATTRIBUTE
	
	final private SoundPacket soundPacket_;
}