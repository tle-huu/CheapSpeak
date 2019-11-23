package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import utilities.FixedVector;
import utilities.events.Event;
import utilities.infra.Log;

/*
 * Server class listening for new connections and handling network architecture
 *
 * The architecture is made of several type of threads:
 *
 *	- Main 				 thread (1): Listening and accepting new connections
 *	- Broacasting 		 thread (1): "Listening" from a shared vector for new packets
 *	- Client Connections threads(n): Represents the connetion with the clients.
 *									 Listens for new events and pushing them in the shared ringbuffer
 * 
 */
public class VocalServer
{
// PRIVATE CONST

	// Starting number of threads in the thread pool
	final int THREADS_NUMBER = 10;

// PUBLIC
	public VocalServer(int port) throws IOException
	{
		port_ = port;

		Log.LOG(Log.Level.INFO, "Server listening to port " + port);
		listening_socket_ = new ServerSocket(port_);

		boolean success = init_rooms();
		if (!success)
		{
			assert false : "Server cannot run without rooms initialization";
		}
	}

	// Starts the broadcasting thread and the main listening loop
	public boolean start() throws IOException
	{
		if (running_.get())
		{
			Log.LOG(Log.Level.WARNING, "Server already running");
			return false;
		}

		Log.LOG(Log.Level.INFO, "Starting Server");
		// Setting running flag
		running_ .set(true);

		// Starting Broadcasting thread
		Broadcaster broadcaster = new Broadcaster(this);
		// broadcaster.start();
        executor_.execute(broadcaster);


		// Starting main loop of accepting new connections
		while (running_.get())
		{
            Socket new_client_socket = listening_socket_.accept();
            Log.LOG(Log.Level.INFO, "New client " + ((ThreadPoolExecutor)executor_).getActiveCount());

            try
            {
	            ClientConnection new_connection_client = new ClientConnection(this, new_client_socket);
	            add_client(new_connection_client);
	            executor_.execute(new_connection_client);
            }
            catch (IOException e)
            {
            	new_client_socket.close();
            }

		}

		Log.LOG(Log.Level.INFO, "Shutting down Server");
		running_ .set(false);

		// Waiting for broadcaster thread to finish broadcasting
		try
		{
			broadcaster.join();

		}
		catch (InterruptedException e)
		{
			Log.LOG(Log.Level.ERROR, "Server error: Impossible to join broadcaster thread:" + e.getMessage());
		}
		return true;
	}

// Exposed interface for server side objects (mainly used by the ConnetionClients)
	public boolean add_to_broadcast(Event event)
	{
		return broadcast_queue_.push(event);
	}

	public Event pop_from_broadcast()
	{
		return broadcast_queue_.pop();
	}

	public void add_room(final ServerRoom room)
	{
		rooms_.put(room.name(), room);
	}

	public void remove_room(final String room_name)
	{
		rooms_.remove(room_name);
	}


	// Move client from an old room to a new room.
	// If oldRoomName is null, just update the new room with the client id
	public boolean update_room(final UUID clientUUID, final String oldRoomName, final String newRoomName)
	{
		if (oldRoomName != null)
		{
	        // Removing client from the current room he was in
			ServerRoom oldRoom = rooms_.get(oldRoomName);

			if (oldRoom == null)
			{
				Log.LOG(Log.Level.ERROR, "Updating room error: old room [" + oldRoomName + "] does not exist");
				return false;
			}

			oldRoom.remove_client(clientUUID);
		}

        // Adding the client to the new room
        ServerRoom newRoom = rooms_.get(newRoomName);
		if (newRoom == null)
		{
			Log.LOG(Log.Level.ERROR, "Updating room error: new room [" + newRoomName + "] does not exist");
			return false;
		}

        newRoom.add_client(clientUUID);
		return true;
	}

	public boolean remove_client(UUID client_uuid)
	{
		ServerRoom client_room = rooms_.get(clients_.get(client_uuid).currentRoom());
		client_room.remove_client(client_uuid);
		clients_.remove(client_uuid);
		return true;
	}

	public void shutdown()
	{
		Log.LOG(Log.Level.WARNING, "Server has been shutdown from a child thread");
		running_.set(false);
	}

	public boolean is_present(final UUID current_client_uuid, final String user_name)
	{
		for (ClientConnection client : clients_.values())
		{
			if (current_client_uuid != client.uuid() && client.user_name().equals(user_name))
			{
				return true;
			}
		}
		return false;
	}

	// Returns the rooms as a vector not to interfeere with the hashtable
    public Vector<ServerRoom> rooms_vector()
    {
        Vector<ServerRoom> rooms = new Vector<ServerRoom>(rooms_.size());
        for (ServerRoom room : rooms_.values())
        {
            rooms.add(room);
        }
       
       return rooms;
    }

    public ConcurrentHashMap<String, ServerRoom> rooms()
    {
		return rooms_;
    }

	public ConcurrentHashMap<UUID, ClientConnection> clients()
	{
		return clients_;
	}

	public ClientConnection get_client(final UUID client_uuid)
	{
		return clients_.get(client_uuid);
	}

	public boolean running()
	{
		return running_.get();
	}



// PRIVATE

	// Initializes rooms in the server
	private boolean init_rooms()
	{
		try
		{
			// Lobby room is the default room for new comer
	        ServerRoom lobby = new ServerRoom("Lobby", this);
	        rooms_.put(lobby.name(), lobby);

            add_room(new ServerRoom("General", this));
            add_room(new ServerRoom("Students", this));
            add_room(new ServerRoom("Random", this));
            add_room(new ServerRoom("ADR", this));
            add_room(new ServerRoom("Forum CentraleSupelec", this));
            add_room(new ServerRoom("VIA", this));

	        return true;
		}
		catch (Exception e)
		{
			Log.LOG(Log.Level.ERROR, "Error initialization of rooms in Vocal Server");
			return false;
		}
	}

	// Adds client to the hashmap
	private boolean add_client(ClientConnection client_conn)
	{
		clients_.put(client_conn.uuid(), client_conn);
		return true;
	}

// PRIVATE

	// Hash map to store client connections objects
	private ConcurrentHashMap<UUID, ClientConnection> clients_ = new ConcurrentHashMap<UUID, ClientConnection>();

	// Hash map to store client connections objects
	private ConcurrentHashMap<String, ServerRoom> rooms_ = new ConcurrentHashMap<String, ServerRoom>();

	// Shared ring buffer for broadcaster and client connections communication
	private FixedVector<Event> 			broadcast_queue_ = new FixedVector<Event>();

	/* 
	 * Server infras
	 */
	private int 							port_;

	private ServerSocket 					listening_socket_;

	private AtomicBoolean 					running_ = new AtomicBoolean(false);

	// ThreadPool for client connection thread
	// TODO: Change it to a dynamic thread pool
	private ExecutorService 				executor_ = Executors.newFixedThreadPool(THREADS_NUMBER);

}
