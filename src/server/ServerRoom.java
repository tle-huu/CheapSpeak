package server;

import java.util.Set;
import java.util.UUID;

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

	@Override
	public String toString()
	{
		return name_;
	}

// PRIVATE ATTRIBUTES

	private Set<UUID> client_uuids_set_;

	final private String name_;

	final private VocalServer vocal_server_;

	final private UUID uuid_ = UUID.randomUUID();

}