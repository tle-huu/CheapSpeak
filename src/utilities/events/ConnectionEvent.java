import java.util.UUID;

// A new user is connected
public class ConnectionEvent extends Event
{

	public ConnectionEvent(UUID uuid)
	{
		super(EventType.CONNECTION, uuid);
	}
}