package utilities;

import java.io.Serializable;
import java.util.Date;  
import java.util.UUID;

import utilities.infra.Log;

/*
 * Abstract class representing Event packet sent over the network
 *
 * An event is the main communication system between clients and the server
 * An event implements the Serializable interface to be able to be sent as an object
 */
public class Event implements Serializable
{

// PUBLIC
	public enum EventType
	{
		CONNECTION,
		DISCONNECTION,
		VOICE,
		TEXT,
		HANDSHAKE,
		MAX_EVENT_TYPE
	}

	// Factory function for events creation
	// static public Event create_event(EventType type, UUID uuid, Object data)
	// {
	// 	switch (type)
	// 	{
	// 		case CONNECTION:
	// 			return new ConnectionEvent(uuid);
	// 		case DISCONNECTION:
	// 			return new DisconnectionEvent(uuid);
	// 		case VOICE:
	// 			return new VoiceEvent(uuid, (SoundPacket)data);
	// 		case TEXT:
	// 			return new TextEvent(uuid, data);
	// 		default:
	// 			break;
	// 	}
	// 	return new Event();
	// }

	static public ConnectionEvent create_connection_event(UUID uuid)
	{
		return new ConnectionEvent(uuid);
	}

	static public DisconnectionEvent create_disconnection_event(UUID uuid)
	{
		return new DisconnectionEvent(uuid);
	}

	static public VoiceEvent create_voice_event(UUID uuid, SoundPacket sound_packet)
	{
		return new VoiceEvent(uuid, sound_packet);
	}

	static public TextEvent create_text_event(UUID uuid, String text_packet)
	{
		return new TextEvent(uuid, text_packet);
	}

	static public HandshakeEvent create_handshake_event()
	{
		return new HandshakeEvent();
	}

	public final EventType type()
	{
		return type_;
	}

	public final UUID uuid()
	{
		return uuid_;
	}

	public void uuid(UUID uuid)
	{
		uuid_ = uuid;
	}

// PROTECTED
	// Constructor is private and an Event should be cted with the create_event static function
	protected Event(EventType event_type, UUID uuid)
	{
		type_ = event_type;
		uuid_ = uuid;
	}

// PROTECTED

	// Event type
	final protected EventType type_;

	// Creation timestamp
	final protected Date timestamp_ = new Date();

	// UUID of the event sender
	protected UUID uuid_;


// PRIVATE
	private Event()
	{
		Log.LOG(Log.Level.ERROR, "Creation of an unknown Event type");
		assert false : "Unknown Event Type in Event creation factory function";
		type_ = EventType.MAX_EVENT_TYPE;
		uuid_ = null;
	}

}
