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

// PUBLIC METHODS
	
	// Constructor
	public VocalServer(final int port) throws IOException, Exception
	{
		port_ = port;

		Log.LOG(Log.Level.INFO, "Server listening to port " + port);
		listeningSocket_ = new ServerSocket(port_);

		boolean success = initRooms();
		if (!success)
		{
			throw new Exception("Server cannot run without rooms initialization");
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
		running_.set(true);

		// Starting Broadcasting thread
		Broadcaster broadcaster = new Broadcaster(this);
        executor_.execute(broadcaster);

		// Starting main loop of accepting new connections
		while (running_.get())
		{
            Socket newClientSocket = listeningSocket_.accept();
            Log.LOG(Log.Level.INFO, "New client: [" + ((ThreadPoolExecutor) executor_).getActiveCount()
            		+ " < " + THREADS_NUMBER + "]");

            try
            {
	            ClientConnection newConnectionClient = new ClientConnection(this, newClientSocket);
	            addClient(newConnectionClient);

	            if (((ThreadPoolExecutor) executor_).getActiveCount() < THREADS_NUMBER)
	            {
		            Log.LOG(Log.Level.INFO, "Using fixed thread pool thread: ["
		            		+ ((ThreadPoolExecutor) executor_).getActiveCount() + " < " + THREADS_NUMBER + "]");
		            executor_.execute(newConnectionClient);
	            }
	            else
	            {
		            Log.LOG(Log.Level.INFO, "FixedThreadPool full, creating a new thread from fallthrough threadpool");
		            fallthroughExecutor_.execute(newConnectionClient);
	            }
            }
            catch (IOException e)
            {
            	newClientSocket.close();
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
	public boolean addToBroadcast(final Event event)
	{
		return broadcastQueue_.push(event);
	}

	public Event popFromBroadcast()
	{
		return broadcastQueue_.pop();
	}

	public void addRoom(final ServerRoom room)
	{
		rooms_.put(room.name(), room);
	}

	public void removeRoom(final String roomName)
	{
		rooms_.remove(roomName);
	}

	// Move client from an old room to a new room.
	// If oldRoomName is null, just update the new room with the client id
	public boolean updateRoom(final UUID clientUUID, final String oldRoomName, final String newRoomName)
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

			oldRoom.removeClient(clientUUID);
		}

        // Adding the client to the new room
        ServerRoom newRoom = rooms_.get(newRoomName);
		if (newRoom == null)
		{
			Log.LOG(Log.Level.ERROR, "Updating room error: new room [" + newRoomName + "] does not exist");
			return false;
		}

        newRoom.addClient(clientUUID);
		return true;
	}

	public boolean removeClient(UUID clientUuid)
	{
		ServerRoom clientRoom = rooms_.get(clients_.get(clientUuid).currentRoom());
		clientRoom.removeClient(clientUuid);
		clients_.remove(clientUuid);
		return true;
	}

	public void shutdown()
	{
		Log.LOG(Log.Level.WARNING, "Server has been shutdown from a child thread");
		running_.set(false);
	}

	public boolean isPresent(final UUID currentClientUuid, final String userName)
	{
		for (ClientConnection client: clients_.values())
		{
			if (currentClientUuid != client.uuid() && client.userName().equals(userName))
			{
				return true;
			}
		}
		return false;
	}

	// Returns the rooms as a vector not to interfeere with the hashtable
    public Vector<ServerRoom> roomsVector()
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

	public ClientConnection getClient(final UUID clientUuid)
	{
		return clients_.get(clientUuid);
	}

	public boolean running()
	{
		return running_.get();
	}
	
	public String defaultRoomName()
	{
		return DEFAULT_ROOM_NAME;
	}

// PRIVATE METHODS

	// Initializes rooms in the server
	private boolean initRooms()
	{
		try
		{
			// Lobby room is the default room for new comer
	        ServerRoom lobby = new ServerRoom(DEFAULT_ROOM_NAME, this);
	        rooms_.put(lobby.name(), lobby);

	        // Add rooms
	        for (String roomName: ROOM_NAMES)
	        {
	        	addRoom(new ServerRoom(roomName, this));
	        }

	        return true;
		}
		catch (Exception e)
		{
			Log.LOG(Log.Level.ERROR, "Error initialization of rooms in Vocal Server");
			return false;
		}
	}

	// Adds client to the hashmap
	private boolean addClient(final ClientConnection clientConn)
	{
		clients_.put(clientConn.uuid(), clientConn);
		return true;
	}

// PRIVATE ATTRIBUTES

	// Room names
    private final String   DEFAULT_ROOM_NAME = "Lobby";
    private final String[] ROOM_NAMES = {"General", 
			"Students", 
			"Random", 
			"BDE", 
			"ADR", 
			"BDA", 
			"Forum", 
			"ViaRezo", 
			"Iris", 
			"Raid",
			"Centrale 7s"};
    
	// Starting number of threads in the thread pool
	private final int THREADS_NUMBER = 11;

	// Hash map to store client connections objects
	private ConcurrentHashMap<UUID, ClientConnection> clients_ = new ConcurrentHashMap<UUID, ClientConnection>();

	// Hash map to store client rooms objects
	private ConcurrentHashMap<String, ServerRoom> rooms_ = new ConcurrentHashMap<String, ServerRoom>();

	// Shared ring buffer for broadcaster and client connections communication
	private FixedVector<Event> broadcastQueue_ = new FixedVector<Event>();

	/* 
	 * Server infras
	 */
	private int             port_;

	private ServerSocket    listeningSocket_;

	private AtomicBoolean   running_ = new AtomicBoolean(false);

	// ThreadPools for client connection thread
	private ExecutorService executor_ = Executors.newFixedThreadPool(THREADS_NUMBER);

	// Back up thread pool used when the main threadpool capacity is reached
	private ExecutorService fallthroughExecutor_ = Executors.newCachedThreadPool();

}
