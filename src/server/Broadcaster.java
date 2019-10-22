package server;

import java.util.concurrent.atomic.AtomicBoolean;

import utilities.Datagram;
import utilities.events.Event;
import utilities.infra.Log;

import java.util.HashMap;
import java.util.Vector;
import java.util.UUID;

/*
 *
 * Broadcasting thread getting message from server broadcasting queue
 * and handing them out to the clients connections sockets for them
 * to be sent over the network
 *
 */
public class Broadcaster extends Thread
{

// PUBLIC

    public Broadcaster(VocalServer vocal_server)
    {
        vocal_server_ = vocal_server;
    }

    @Override
    public void run()
    {
        Log.LOG(Log.Level.INFO, "Starting Broadcasting thread");

        running_.getAndSet(true);

        // Main loop reading from the shared ringbuffer for new events
        // The loop is tied to the main thread status
        while (running_.get() && vocal_server_.running())
        {
            try
            {
                Event new_event = get_new_event();
                if (new_event != null)
                {
                    broadcast(new_event);
                }
            }
            catch (Exception e)
            {
                Log.LOG(Log.Level.FATAL, "Error in Broadcaster thread: " + e);
                e.printStackTrace();
                break ;
            }
        }

        Log.LOG(Log.Level.INFO, "Shutting down Broadcaster thread");
        running_.getAndSet(false);

        // Shutting down main server
        if (vocal_server_.running())
        {
            Log.LOG(Log.Level.INFO, "Shutting down Server from Broadcaster Thread");
            vocal_server_.shutdown();
        }
    }


// PRIVATE
    private Event get_new_event()
    {
        return vocal_server_.pop_from_broadcast();
    }

    private void broadcast(Event event)
    {
        // Only sending to people in the room
        if (event.type() == Event.EventType.VOICE || event.type() == Event.EventType.TEXT)
        {

            ClientConnection client = vocal_server_.get_client(event.uuid());

            if (client == null)
            {
                Log.LOG(Log.Level.ERROR, "Error broadcasting message from client " + event.uuid().toString()+ " : client does not exist");
                return ;
            }
            String room_name = client.currentRoom();

            if (room_name == null)
            {
                Log.LOG(Log.Level.ERROR, "Error broadcasting into a room: room null");
                return ;
            }

            ServerRoom room = vocal_server_.rooms().get(room_name);

            for (ClientConnection client_conn : room.clients())
            {
                if (event.type() == Event.EventType.VOICE && event.uuid() == client_conn.uuid())
                    continue;
                client_conn.send(event);
            }
        }
        else
        {
            // Sending the packet to remaining connected clients
            for (ClientConnection client_conn : vocal_server_.clients().values())
            {
                client_conn.send(event);
            }

        }
    }

// PRIVATE

    private VocalServer vocal_server_ = null;

    private AtomicBoolean running_ = new AtomicBoolean(false);

}
