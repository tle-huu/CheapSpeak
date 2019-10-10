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
                if (new_event == null)
                {
                    continue;
                }

                // Removing dead clients (broken pipes)
                {
                    Vector<ClientConnection> dead_clients = new Vector<ClientConnection>();
                    for (ClientConnection client_conn : vocal_server_.clients().values())
                    {
                        if (!client_conn.alive())
                        {
                            dead_clients.add(client_conn);
                            continue;
                        }
                    }
                    for (ClientConnection client_conn : dead_clients)
                    {
                        vocal_server_.remove_client(client_conn);
                    }
                }

                // Sending the packet to remaining connected clients
                for (ClientConnection client_conn : vocal_server_.clients().values())
                {
                    // TODO: Should push into a ClientConnecton buffer instead of sending them directly
                    if (client_conn.uuid() != new_event.uuid())
                    {
                        client_conn.send(new_event);
                    }
                }
            }
            catch (Exception e)
            {
                Log.LOG(Log.Level.FATAL, "Error in recording thread");
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

// PRIVATE

    private VocalServer vocal_server_ = null;

    private AtomicBoolean running_ = new AtomicBoolean(false);

}
