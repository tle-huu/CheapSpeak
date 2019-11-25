package server;

import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;

/*
 * Simple ServerRoom class storing clients uuid
 */
public class ServerRoom
{

// PUBLIC METHODS

	// Constructor
	public ServerRoom(final String name, final VocalServer vocalServer) throws Exception
	{
		name_ = name;
		vocalServer_ = vocalServer;

		if (vocalServer == null)
		{
			throw new Exception("Cannot instanciate room with null server reference");
		}
	}

	// Return true if the set did not contain the given uuid
	public boolean addClient(final UUID uuid)
	{
		return clientUuidsSet_.add(uuid);
	}

	// Return true if the set contained the given uuid
	public boolean removeClient(final UUID uuid)
	{
		return clientUuidsSet_.remove(uuid);
	}

	public Vector<ClientConnection> clients()
	{
		Vector<ClientConnection> clients = new Vector<ClientConnection>();

		for (UUID uuid: clientUuidsSet_)
		{
			clients.add(vocalServer_.clients().get(uuid));
		}
		return clients;
	}

	// Simple getters
	public String name()
	{
		return name_;
	}

	public UUID uuid()
	{
		return uuid_;
	}

	@Override
	public String toString()
	{
		return name_;
	}

// PRIVATE ATTRIBUTES

	private TreeSet<UUID> clientUuidsSet_ = new TreeSet<UUID>();

	private final String name_;

	private final VocalServer vocalServer_;

	private final UUID uuid_ = UUID.randomUUID();

}
