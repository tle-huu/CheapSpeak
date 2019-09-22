import java.util.concurrent.atomic.AtomicBoolean;
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
                Datagram new_datagram = get_new_datagram();
                if (new_datagram == null)
                {
                    // Log.LOG(Log.Level.ERROR, "Broadcaster Error trying to get new datagram");
                    continue;
                }

                // Removing dead clients (broken pipes)
                // TODO: Need to put a mutex on this scope to avoid java.util.ConcurrentModificationException
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
                    if (client_conn.uuid() != new_datagram.client_uuid())
                    {
                        client_conn.send_datagram(new_datagram);
                    }
                }
            }
            catch (Exception e)
            {
                // Log.LOG(Log.Level.ERROR, "Broadcaster FATAL ERROR: " + e.getMessage());
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
    private Datagram get_new_datagram()
    {
        return vocal_server_.pop_from_broadcast();
    }

    // private boolean merge_packets(ArrayList<Datagram> packets)
    // {
    //     return true;
    // }

// PRIVATE

    private VocalServer vocal_server_ = null;

    private AtomicBoolean running_ = new AtomicBoolean(false);

}
