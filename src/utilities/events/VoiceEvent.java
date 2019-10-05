import java.util.UUID;

// Voice packet message
public class VoiceEvent extends Event
{
	public VoiceEvent(UUID uuid, SoundPacket sound_packet)
	{
		super(EventType.VOICE, uuid);
		sound_packet_ = sound_packet;
	}

	public final SoundPacket sound_packet()
	{
		return sound_packet_;
	}

// PRIVATE
	final private SoundPacket sound_packet_;
}