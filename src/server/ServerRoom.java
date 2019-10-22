package server;

import utilities.events.TextEvent;

import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;

/*
 * Simple ServerRoom class storing clients uuid
 */
public class ServerRoom
{

// PUBLIC METHODS

	public ServerRoom(final String name, final VocalServer vocal_server)
	{
		name_ = name;
		vocal_server_ = vocal_server;
		assert vocal_server_ != null : "Cannot instanciate room with null server reference";
	}

	// Return true if the set did not contain the given uuid
	public boolean add_client(final UUID uuid)
	{
		return client_uuids_set_.add(uuid);
	}

	// Return true if the set contained the given uuid
	public boolean remove_client(final UUID uuid)
	{
		return client_uuids_set_.remove(uuid);
	}

	public String name()
	{
		return name_;
	}

	public UUID uuid()
	{
		return uuid_;
	}

	public Vector<ClientConnection> clients()
	{
		Vector<ClientConnection> clients = new Vector<ClientConnection>();

		for (UUID uuid : client_uuids_set_)
		{
			clients.add(vocal_server_.clients().get(uuid));
		}
		return clients;
	}

	public void add_to_history(final TextEvent text_event)
	{
		history_.add(text_event);
	}

	public void clear_history()
	{
		history_.clear();
	}

	public Vector<TextEvent> history()
	{
		return history_;
	}

	@Override
	public String toString()
	{
		return name_;
	}

// PRIVATE ATTRIBUTES

	private TreeSet<UUID> client_uuids_set_ = new TreeSet<UUID>();

	final private String name_;

	final private VocalServer vocal_server_;

	final private UUID uuid_ = UUID.randomUUID();

	private Vector<TextEvent> history_ = new Vector<TextEvent>();

}