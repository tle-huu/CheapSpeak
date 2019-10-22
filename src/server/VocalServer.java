package server;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.*;

import utilities.Datagram;
import utilities.RingBuffer;
import utilities.events.Event;
import utilities.infra.Log;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

/*
 * Server class listening for new connections and handling network architecture
 *
 * The architecture is made of several type of threads:
 *
 *	- Main 				 thread (1): Listening and accepting new connections
 *	- Broacasting 		 thread (1): "Listening" from a shared ringbuffer for new packets
 *	- Client Connections threads(n): Represents the connetion with the clients.
 *									 Listens for new events and pushing them in the shared ringbuffer
 * 
 */
public class VocalServer
{
// PRIVATE CONST

	final int THREADS_NUMBER = 10;

// PUBLIC
	public VocalServer(int port) throws Exception
	{
		port_ = port;

		Log.LOG(Log.Level.INFO, "Server listening to port " + port);
		listening_socket_ = new ServerSocket(port_);

		// TODO Move into an init function
        ServerRoom lobby = new ServerRoom("Lobby", this);
        rooms_.put(lobby.name(), lobby);

        add_room(new ServerRoom("La chambre d'Elie", this));
        add_room(new ServerRoom("La chambre d'Oket", this));
        add_room(new ServerRoom("La chambre de Crepel", this));
        add_room(new ServerRoom("Le harem de Victor Robin", this));
        add_room(new ServerRoom("Le Cabinet", this));

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
		running_ .getAndSet(true);

		// Starting Broadcasting thread
		Broadcaster broadcaster = new Broadcaster(this);
		broadcaster.start();

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
		running_ .getAndSet(false);

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

        // Adding the client to his new room
        ServerRoom newRoom = rooms_.get(newRoomName);
		if (newRoom == null)
		{
			Log.LOG(Log.Level.ERROR, "Updating room error: new room [" + newRoomName + "] does not exist");
			return false;
		}

        newRoom.add_client(clientUUID);
		return true;
	}

    public Vector<ServerRoom> rooms_vector()
    {
        Vector<ServerRoom> rooms = new Vector<ServerRoom>(rooms_.size());
        for (ServerRoom room : rooms_.values())
        {
            rooms.add(room);
        }
       
       return rooms;
    }

    public Hashtable<String, ServerRoom> rooms()
    {
		return rooms_;
    }

	// TODO: Bad getter. Should disappear and be turned into a proper exposed API
	public Hashtable<UUID, ClientConnection> clients()
	{
		return clients_;
	}

	public boolean remove_client(ClientConnection client_conn)
	{
		ServerRoom client_room = rooms_.get(client_conn.currentRoom());
		client_room.remove_client(client_conn.uuid());
		clients_.remove(client_conn.uuid());
		return true;
	}

	public void shutdown()
	{
		Log.LOG(Log.Level.WARNING, "Server has been shutdown from a child thread");
		running_.getAndSet(false);
	}

	public boolean running()
	{
		return running_.get();
	}

// PRIVATE

	private boolean add_client(ClientConnection client_conn)
	{
		// TODO: Protect this with a better mutex system
		clients_.put(client_conn.uuid(), client_conn);
		return true;
	}


// PRIVATE

	// Hash map to store client connections objects
	private Hashtable<UUID, ClientConnection> clients_ = new Hashtable<UUID, ClientConnection>();

	// Hash map to store client connections objects
	private Hashtable<String, ServerRoom> rooms_ = new Hashtable<String, ServerRoom>();

	// Shared ring buffer for broadcaster and client connections communication
	private RingBuffer<Event> 			broadcast_queue_ = new RingBuffer<Event>();

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
