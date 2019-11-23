package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.Vector;

import utilities.events.*;
import utilities.infra.Log;

/*
 * A ClientConnection represents the connection worker associated to a client
 *
 * Single Thread listening for incoming packets and sending them to the broadcasting thread
 *
 */

public class ClientConnection implements Runnable, EventEngine
{

// PUBLIC
    public ClientConnection(VocalServer vocal_server, Socket socket) throws IOException
    {
        socket_ = socket;
        vocal_server_ = vocal_server;
        uuid_ = UUID.randomUUID();

        try
        {
            output_stream_ = new ObjectOutputStream(socket_.getOutputStream());
            input_stream_ = new ObjectInputStream(socket_.getInputStream());
        }
        catch (IOException err)
        {
            Log.LOG(Log.Level.ERROR, "ClientConnection Construction error: " + err);
            close();
            throw err;
        }

        Log.LOG(Log.Level.INFO, "New ClientConnection: " + uuid_.toString());
    }

    public void run()
    {
        Log.LOG(Log.Level.INFO, "Starting client [" + uuid_.toString() + "]");

        running_ = true;

        boolean accepted = handshake();


        if (accepted == false)
        {
            Log.LOG(Log.Level.ERROR, "Error while performing the handshake");
            close();
            return ;
        }

        // notify others
        {
            ConnectionEvent my_connection_event = new ConnectionEvent(uuid_, user_name_);
            broadcast(my_connection_event);
        }
        try
        {
            while (running_ && vocal_server_.running())
            {
                try
                {
                    if (!alive())
                    {
                        close();
                        break;
                    }

                    Event event = (Event) input_stream_.readObject();

                    // Setting emitter uuid
                    event.uuid(uuid_);

                    // Process the event
                    handleEvent(event);

                }
                catch (Exception e)
                {
                    Log.LOG(Log.Level.INFO, "CLientConnection [" + user_name_ + "] socket closed");
                    // Sending to others that we disconnected due to a fatal error
                    handleDisconnection(new DisconnectionEvent(uuid_, user_name_));
                    break;
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }

    }

    // Exposed method to be used by the broadcaster thread
    public boolean send(final Event event)
    {
        try
        {
            output_stream_.writeObject(event);
            return true;
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "[ClientConnection] (" + user_name_ + ") Error in sending event of type " + event.type());
            return false;
        }
    }

    @Override
    public boolean handleConnection(ConnectionEvent event)
    {
        Log.LOG(Log.Level.INFO, "handleConnection");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleDisconnection(DisconnectionEvent event)
    {
        Log.LOG(Log.Level.INFO, "handleDisconnectionEvent: " + event.userName() + " disconnected");
        close();
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleEnterRoom(EnterRoomEvent event)
    {

        // Updating the rooms
        boolean res = vocal_server_.update_room(uuid_, currentRoom_, event.roomName());
        if (res == false)
        {
            Log.LOG(Log.Level.ERROR, "HandleEnterRoom error: could not update rooms");
            return false;
        }

        // Updating internal current room for client
        currentRoom_ = event.roomName();

        broadcast(event);

        return true;
    }

    @Override
    public boolean handleNewRoom(NewRoomEvent event)
    {
        Log.LOG(Log.Level.INFO, "HandleNewRoom triggered");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleRemoveRoom(RemoveRoomEvent event)
    {
        Log.LOG(Log.Level.INFO, "HandleNewRoom triggered");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleVoice(VoiceEvent event)
    {
        broadcast(event);
        return true;
    }

    @Override
    public boolean handleText(TextEvent event)
    {
        ServerRoom current_room = vocal_server_.rooms().get(currentRoom_);

        broadcast(event);
        return true;
    }

    public int port()
    {
        return socket_.getPort();
    }

    public UUID uuid()
    {
        return uuid_;
    }

    public InetAddress InetAddress()
    {
        return socket_.getInetAddress();
    }

    public boolean alive()
    {
        return socket_.isConnected() && !socket_.isInputShutdown();
    }

    public String currentRoom()
    {
        return currentRoom_;
    }

    public String user_name()
    {
        return user_name_;
    }

    public void close()
    {
        if (!running_)
        {
            return ;
        }

        try 
        {
            input_stream_.close();
            output_stream_.close();
            socket_.close();

        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error closing ClientConnection [" + uuid_.toString() + "]: " + e.getMessage());
        }
        finally
        {
            vocal_server_.remove_client(uuid_);
            running_ = false;
        }
    }

// PRIVATE

    private Event read()
    {
        Event event = null;

        try
        {
            event = (Event) input_stream_.readObject();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error in read: " + e);
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.INFO, "Read a Non-Event Object: " + e);
        }

        return event;
    }

    private void broadcast(final Event event)
    {
        try
        {
            event.uuid(uuid_);
            boolean res = vocal_server_.add_to_broadcast(event);
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.ERROR, "ClientConnection broadcast error: " + e.getMessage());
        }
    }

    private boolean handshake()
    {
        HandshakeEvent event = new HandshakeEvent();

        // Send empty shell of HandshakeEvent to be filled by client
        boolean res = send(event);

        if (res == false)
        {
            Log.LOG(Log.Level.ERROR, "Error in handshake to send first handshake event shell");
            return false;
        }

        // Read Client answer
        event = (HandshakeEvent) read();

        if (event == null)
        {
            Log.LOG(Log.Level.ERROR, "Error in handshake, cant read new event");
            return false;
        }

        // The client should have set the state to nameset
        if (event.state() != HandshakeEvent.State.NAMESET)
        {
            event.state(HandshakeEvent.State.BYE);
        }
        else
        {
            event.state(HandshakeEvent.State.OK);
        }

        if (vocal_server_.is_present(uuid_, event.userName()))
        {
            // Setting Closing state and stoping handshake
            event.state(HandshakeEvent.State.OTHERNAME);
            res = send(event);
            return false;
        }

        // Setting userName;
        user_name_ = event.userName();
        res = send(event);

        // Read Client answer
        event = (HandshakeEvent) read();

        if (event.state() == HandshakeEvent.State.LISTENING)
        {
            send_server_current_status();
        }
        else
        {
            res = false;
        }

        return res;
    }

    private boolean send_server_current_status()
    {
        Vector<ServerRoom> rooms = vocal_server_.rooms_vector();
        for (ServerRoom room : rooms)
        {
            if (room.name().equals("Lobby"))
            {
                continue ;
            }
            NewRoomEvent new_room_event = new NewRoomEvent(null, room.name());
            send(new_room_event);
        }

        for (ClientConnection client_conn : vocal_server_.clients().values())
        {
            if (client_conn == this)
            {
                continue;
            }
            EnterRoomEvent connection_event = new EnterRoomEvent(uuid_, client_conn.user_name(), client_conn.currentRoom());
            send(connection_event);
        }

        // Updating Lobby
        boolean res = vocal_server_.update_room(uuid_, null, currentRoom_);
        if (res == false)
        {
            Log.LOG(Log.Level.ERROR, "send server current status: error: could not update lobby room");
            return false;
        }

        return true;
    }

// PRIVATE

    // Name gotten from handshake
    private String user_name_;

    // Current room
    private String currentRoom_ = "Lobby";

    // socket connection to the client
    final private Socket socket_;

    // Reference to the server
    final private VocalServer vocal_server_;

    // Unique uuid
    final private UUID uuid_;

    // Input Stream
    private ObjectInputStream input_stream_ ;

    // Output Stream
    private ObjectOutputStream output_stream_;

    private boolean running_ = false;

}
