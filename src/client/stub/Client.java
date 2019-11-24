package client.stub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;

import utilities.events.Event;
import utilities.events.HandshakeEvent;
import utilities.FixedVector;
import utilities.infra.Log;

/*
 * The Client class represents the low-level interface for network communication
 *
 * An independant thread is reading from the socket and enqueueing messages in cache
 * The enqueued message can then be consumed by higher level classes
 */
public class Client
{
    
// PUBLIC ENUM

	public enum ConnectState
	{
		OK,
		CHANGE_PSEUDO,
		BYE;
	};
	
// PUBLIC METHODS
	
	// Constructor
	public Client(final String host, final int port) throws UnknownHostException, IOException
    {
        host_ = host;
        port_ = port;

        try
        {
        	socket_ = new Socket(InetAddress.getByName(host), port);
        	inputStream_ = new ObjectInputStream(socket_.getInputStream());
        	outputStream_ = new ObjectOutputStream(socket_.getOutputStream());
        
        	Log.LOG(Log.Level.INFO, "Connected to " + host + " on port " + port);
        }
        catch (UnknownHostException e)
        {
        	Log.LOG(Log.Level.ERROR, "The host " + host + " is unknown");
        	throw e;
        }
        catch (IOException e)
        {
        	Log.LOG(Log.Level.FATAL, "The client cannot be created: " + e.getMessage());
        	throw e;
        }
    }

    public ConnectState connect(final String userName)
    {
       return handshake(userName);
    }
    
    public boolean alive()
    {
    	return running_.get();
    }

    public void disconnect()
    {
        close();
    }

    public Event getEvent()
    {
        return eventQueue_.pop();
    }

    public void sendEvent(final Event event)
    {
    	try
        {
            outputStream_.writeObject((Event) event);
        }
        catch (IOException e)
        {
        	if (running_.get())
            {
        		Log.LOG(Log.Level.ERROR, "Sending an event of type " + event.type().name() + " failed");
            }
        }
    }
    
    public String host()
    {
    	return host_;
    }
    
    public int port()
    {
    	return port_;
    }

// PRIVATE METHODS

    private void startListening()
    {
        Thread thread = new Thread(new Runnable()
        	{
                @Override
                public void run()
                {
                    while (running_.get())
                    {
                    	Event event = read();

                        if (event == null)
                        {
                        	if (running_.get())
                            {
                        		Log.LOG(Log.Level.WARNING, "Cannot read new event in listening loop");
                            }
                            continue;
                        }

                        boolean isPushed = eventQueue_.push(event);

                        if (!isPushed)
                        {
                            Log.LOG(Log.Level.ERROR, "Cannot push event into event queue");
                        }
                    }
                }
            }
        );
        
        running_.set(true);
        
        thread.start();
        
        Log.LOG(Log.Level.INFO, "Listening thread is running");
    }

    private Event read()
    {
        Event event = null;

        try
        {
            event = (Event) inputStream_.readObject();
        }
        catch (IOException e)
        {
            if (running_.get())
            {
            	Log.LOG(Log.Level.ERROR, "Client error in read: " + e.getMessage());
            	running_.set(false);
            }
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.WARNING, "Client read a Non-Event Object: " + e.getMessage());
        }

        return event;
    }

    @SuppressWarnings("unused")
	private Event readNoblock()
    {
        Event event = null;

        try
        {
            if (socket_.getInputStream().available() > 0)
            {
                event = (Event) inputStream_.readObject();
            }
            else
            {
                Log.LOG(Log.Level.WARNING, "Client can't read: stream unavailable");
            }
        }
        catch (IOException e)
        {
        	if (running_.get())
            {
        		Log.LOG(Log.Level.ERROR, "Client error in read: " + e.getMessage());
        		running_.set(false);
            }
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.INFO, "Client read a Non-Event Object: " + e.getMessage());
        }

        return event;
    }

    // Implements the connection handshake with the server
    private ConnectState handshake(final String name)
    {
        HandshakeEvent event = null;

        int tryCounter = 0;
        while (event == null && tryCounter <= 5)
        {
            event = (HandshakeEvent) read();
            ++tryCounter;
        }
        
        if (tryCounter == 5)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed: could not get first HandshakeEvent");
            return ConnectState.BYE;
        }
        
        // Get event state
        HandshakeEvent.State state = event.state();
        
        // Waiting for an waiting state from server
        if (state != HandshakeEvent.State.WAITING)
        {
            Log.LOG(Log.Level.ERROR, "Handshake event received is in an unexpected state: " + event.state().name());
            return ConnectState.BYE;
        }

        // Setting the name and the state to set and sending the event to the server
        int magicWord = new Random().ints(0, 42133742).findFirst().getAsInt();

        event.userName(name);
        event.magicWord(magicWord);
        event.state(HandshakeEvent.State.NAMESET);
        
        sendEvent(event);
        event = null;
        
        while (event == null && tryCounter <= 5)
        {
            event = (HandshakeEvent) read();
            ++tryCounter;
        }
        
        if (tryCounter == 5)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed: could not get second HandshakeEvent");
            return ConnectState.BYE;
        }
        
        // Get state
        state = event.state();
        
        // Check if the pseudo is available
        if (state == HandshakeEvent.State.OTHERNAME)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed: the pseudo is already taken");
            return ConnectState.CHANGE_PSEUDO;
        }
        
        // Waiting for the response. If OK, we good to go, otherwise, handhshake failed
        if (state != HandshakeEvent.State.OK)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed");
            return ConnectState.BYE;
        }

        // Check the magic word
        if (event.magicWord() != magicWord)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed: magic word has been changed");
            return ConnectState.BYE;
        }

        if (tryCounter != 2)
        {
            Log.LOG(Log.Level.WARNING, "Handshake did not receive a HandshakeEvent at first: {" + Integer.toString(tryCounter) + "}");
        }

        // Starting listening thread
        startListening();

        // Notifying server that we are ready to listen for incoming events
        event.state(HandshakeEvent.State.LISTENING);
        sendEvent(event);

        return ConnectState.OK;
    }

    private boolean close()
    {
    	running_.set(false);
    	
    	try
        {
            outputStream_.close();
            socket_.close();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error closing socket: " + e.getMessage());
            return false;
        }
        
        return true;
    }

// PRIVATE ATTRIBUTES

    private FixedVector<Event> eventQueue_ = new FixedVector<Event>();

    // Private Attributes
    private String host_;
    private int    port_;
    private Socket socket_;

    // Set to true for testing
    private AtomicBoolean running_ = new AtomicBoolean(false);

	// Input Stream
	private ObjectInputStream inputStream_;

	// Output Stream
	private ObjectOutputStream outputStream_;

}
