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
import utilities.infra.Log;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.HashMap;
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

		Log.LOG(Log.Level.INFO, "Server listening to port " + Integer.toString(port));
		listening_socket_ = new ServerSocket(port_);

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
	public final boolean add_to_broadcast(Event event)
	{
		return broadcast_queue_.push(event);
	}

	public final Event pop_from_broadcast()
	{
		return broadcast_queue_.pop();
	}

	// TODO: Bad getter. Should disappear and be turned into a proper exposed API
	public HashMap<UUID, ClientConnection> clients()
	{
		return clients_;
	}

	public final boolean remove_client(ClientConnection client_conn)
	{
		clients_mutex_.lock();
		clients_.remove(client_conn.uuid());
		clients_mutex_.unlock();
		return true;
	}

	public void shutdown()
	{
		Log.LOG(Log.Level.WARNING, "Server has been shutdown from a child thread");
		running_.getAndSet(false);
	}

	public final boolean running()
	{
		return running_.get();
	}

// PRIVATE

	private boolean add_client(ClientConnection client_conn)
	{
		clients_mutex_.lock();
		// TODO: Protect this with a better mutex system
		clients_.put(client_conn.uuid(), client_conn);
		clients_mutex_.unlock();
		return true;
	}


// PRIVATE

	// Hash map to store client connections objects
	private HashMap<UUID, ClientConnection> clients_ = new HashMap<UUID, ClientConnection>();
	public final ReentrantLock 				clients_mutex_ = new ReentrantLock();

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