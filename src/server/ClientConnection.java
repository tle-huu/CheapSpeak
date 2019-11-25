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

// PUBLIC METHODS
	
	// Constructor
    public ClientConnection(final VocalServer vocalServer, final Socket socket) throws IOException
    {
        socket_ = socket;
        vocalServer_ = vocalServer;
        uuid_ = UUID.randomUUID();

        currentRoom_ = vocalServer_.defaultRoomName();
        
        try
        {
            outputStream_ = new ObjectOutputStream(socket_.getOutputStream());
            inputStream_ = new ObjectInputStream(socket_.getInputStream());
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "ClientConnection Construction error: " + e.getMessage());
            close();
            throw e;
        }

        Log.LOG(Log.Level.INFO, "New ClientConnection: " + uuid_.toString());
    }

    public void run()
    {
        Log.LOG(Log.Level.INFO, "Starting client [" + uuid_.toString() + "]");

        running_ = true;

        boolean accepted = handshake();

        if (!accepted)
        {
            Log.LOG(Log.Level.ERROR, "Error while performing the handshake");
            close();
            return;
        }

        // Notify others
        {
            ConnectionEvent myConnectionEvent = new ConnectionEvent(uuid_, userName_);
            broadcast(myConnectionEvent);
        }
        try
        {
            while (running_ && vocalServer_.running())
            {
                try
                {
                    if (!alive())
                    {
                        close();
                        break;
                    }

                    Event event = (Event) inputStream_.readObject();

                    // Setting emitter uuid
                    event.uuid(uuid_);

                    // Process the event
                    handleEvent(event);
                }
                catch (Exception e)
                {
                    Log.LOG(Log.Level.INFO, "CLientConnection [" + userName_ + "] socket closed");
                    
                    // Sending to others that we disconnected due to a fatal error
                    handleDisconnection(new DisconnectionEvent(uuid_, userName_));
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
            outputStream_.writeObject(event);
            return true;
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "[ClientConnection] (" + userName_
            		+ ") Error in sending event of type " + event.type());
            return false;
        }
    }

    @Override
    public boolean handleConnection(final ConnectionEvent event)
    {
        Log.LOG(Log.Level.INFO, "handleConnection");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleDisconnection(final DisconnectionEvent event)
    {
        Log.LOG(Log.Level.INFO, "handleDisconnectionEvent: " + event.userName() + " disconnected");
        close();
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleEnterRoom(final EnterRoomEvent event)
    {

        // Updating the rooms
        boolean res = vocalServer_.updateRoom(uuid_, currentRoom_, event.roomName());
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
    public boolean handleNewRoom(final NewRoomEvent event)
    {
        Log.LOG(Log.Level.INFO, "HandleNewRoom triggered");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleRemoveRoom(final RemoveRoomEvent event)
    {
        Log.LOG(Log.Level.INFO, "HandleNewRoom triggered");
        broadcast(event);

        return true;
    }

    @Override
    public boolean handleVoice(final VoiceEvent event)
    {
        broadcast(event);
        return true;
    }

    @Override
    public boolean handleText(final TextEvent event)
    {
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

    public String userName()
    {
        return userName_;
    }

    public void close()
    {
        if (!running_)
        {
            return;
        }

        try 
        {
            inputStream_.close();
            outputStream_.close();
            socket_.close();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error closing ClientConnection [" + uuid_.toString()
            		+ "]: " + e.getMessage());
        }
        finally
        {
            vocalServer_.removeClient(uuid_);
            running_ = false;
        }
    }

// PRIVATE METHODS

    private Event read()
    {
        Event event = null;

        try
        {
            event = (Event) inputStream_.readObject();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error in read: " + e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.INFO, "Read a Non-Event Object: " + e.getMessage());
        }

        return event;
    }

    private void broadcast(final Event event)
    {
        event.uuid(uuid_);
        
        boolean isBroadcasted = vocalServer_.addToBroadcast(event);
        if (!isBroadcasted)
        {
            Log.LOG(Log.Level.ERROR, "ClientConnection broadcast error");
        }
    }

    private boolean handshake()
    {
        HandshakeEvent event = new HandshakeEvent();

        // Send empty shell of HandshakeEvent to be filled by client
        boolean res = send(event);

        if (!res)
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

        if (vocalServer_.isPresent(uuid_, event.userName()))
        {
            // Setting Closing state and stoping handshake
            event.state(HandshakeEvent.State.OTHERNAME);
            res = send(event);
            return false;
        }

        // Setting userName;
        userName_ = event.userName();
        res = send(event);

        // Read Client answer
        event = (HandshakeEvent) read();

        if (event.state() == HandshakeEvent.State.LISTENING)
        {
            sendServerCurrentStatus();
        }
        else
        {
            res = false;
        }

        return res;
    }

    private boolean sendServerCurrentStatus()
    {
        Vector<ServerRoom> rooms = vocalServer_.roomsVector();
        for (ServerRoom room: rooms)
        {
            if (room.name().equals(vocalServer_.defaultRoomName()))
            {
                continue;
            }
            NewRoomEvent newRoomEvent = new NewRoomEvent(null, room.name());
            send(newRoomEvent);
        }

        for (ClientConnection clientConn: vocalServer_.clients().values())
        {
            if (clientConn == this)
            {
                continue;
            }
            EnterRoomEvent connectionEvent = new EnterRoomEvent(uuid_, clientConn.userName(), clientConn.currentRoom());
            send(connectionEvent);
        }

        // Updating Lobby
        boolean res = vocalServer_.updateRoom(uuid_, null, currentRoom_);
        if (!res)
        {
            Log.LOG(Log.Level.ERROR, "send server current status: error: could not update lobby room");
            return false;
        }

        return true;
    }

// PRIVATE ATTRIBUTES

    // Name gotten from handshake
    private String userName_;
    
    // Current room
    private String currentRoom_;

    // socket connection to the client
    private final Socket socket_;

    // Reference to the server
    private final VocalServer vocalServer_;

    // Unique uuid
    private final UUID uuid_;

    // Input Stream
    private ObjectInputStream inputStream_ ;

    // Output Stream
    private ObjectOutputStream outputStream_;

    private boolean running_ = false;

}
