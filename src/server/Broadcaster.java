package server;

import java.util.concurrent.atomic.AtomicBoolean;

import utilities.events.Event;
import utilities.infra.Log;

/*
 *
 * Broadcasting thread getting message from server broadcasting queue
 * and handing them out to the clients connection sockets for them
 * to be sent over the network
 *
 */
public class Broadcaster extends Thread
{

// PUBLIC METHODS

	// Constructor
    public Broadcaster(final VocalServer vocalServer)
    {
        vocalServer_ = vocalServer;
    }

    @Override
    public void run()
    {
        Log.LOG(Log.Level.INFO, "Starting Broadcasting thread");

        running_.set(true);

        // Main loop reading from the shared ringbuffer for new events
        // The loop is tied to the main thread status
        while (running_.get() && vocalServer_.running())
        {
            try
            {
                Event new_event = getNewEvent();
                if (new_event != null)
                {
                    broadcast(new_event);
                }
            }
            catch (Exception e)
            {
                Log.LOG(Log.Level.FATAL, "Error in Broadcaster thread: " + e.getMessage());
                break;
            }
        }

        Log.LOG(Log.Level.INFO, "Shutting down Broadcaster thread");
        running_.set(false);

        // Shutting down main server
        if (vocalServer_.running())
        {
            Log.LOG(Log.Level.INFO, "Shutting down Server from Broadcaster Thread");
            vocalServer_.shutdown();
        }
    }

// PRIVATE METHODS
    
    private Event getNewEvent()
    {
        return vocalServer_.popFromBroadcast();
    }

    private void broadcast(final Event event)
    {
        // Only sending to people in the room
        if (event.type() == Event.EventType.VOICE || event.type() == Event.EventType.TEXT)
        {
            ClientConnection client = vocalServer_.getClient(event.uuid());

            if (client == null)
            {
                Log.LOG(Log.Level.ERROR, "Error broadcasting message from client " + event.uuid().toString()
                		 + " : client does not exist");
                return;
            }
            String roomName = client.currentRoom();

            if (roomName == null)
            {
                Log.LOG(Log.Level.ERROR, "Error broadcasting into a room: room null");
                return;
            }

            ServerRoom room = vocalServer_.rooms().get(roomName);

            for (ClientConnection clientConn: room.clients())
            {
                if (event.type() == Event.EventType.VOICE && event.uuid() == clientConn.uuid())
                {
                    continue;
                }
                clientConn.send(event);
            }
        }
        else
        {
            // Sending the packet to remaining connected clients
            for (ClientConnection clientConn: vocalServer_.clients().values())
            {
                clientConn.send(event);
            }

        }
    }

// PRIVATE ATTRIBUTES

    private VocalServer vocalServer_ = null;

    private AtomicBoolean running_ = new AtomicBoolean(false);

}
