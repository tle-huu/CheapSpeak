package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utilities.Datagram;
import utilities.events.*;
import utilities.infra.Log;

import javax.sound.sampled.SourceDataLine;

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

		System.out.println("new client: " + uuid_.toString());
	}

	public void run()
	{
		Log.LOG(Log.Level.INFO, "Starting client [" + uuid_.toString() + "]");

		running_ = true;

		boolean accepted = handshake();

		// notify others
		{
			ConnectionEvent my_connection_event = new ConnectionEvent(uuid_, user_name_);
			broadcast(my_connection_event);
		}

		if (accepted == false)
		{
			Log.LOG(Log.Level.ERROR, "Error while performing the handshake");
			return ;
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

					// Pushing to broadcaster thread
					broadcast(event);

				}
				catch (java.io.EOFException e)
				{
					Log.LOG(Log.Level.INFO, "CLientConnection [" + user_name_ + "] socket closed");
					break;
				}
				catch (Exception e)
				{
					System.out.println("ClientConnection error reading input: " + e);
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
			return false;
		}
	}

	@Override
	public boolean handleConnection(ConnectionEvent event)
	{
		Log.LOG(Log.Level.INFO, "handleConnection");
		return true;
	}

	@Override
	public boolean handleDisconnection(DisconnectionEvent event)
	{
		Log.LOG(Log.Level.INFO, "handleDisconnectionEvent: " + event.userName() + " disconnected");
		close();
		return true;
	}

	@Override
	public boolean handleEnterRoom(EnterRoomEvent event)
    {
        // currentRoom_ = event.roomName();
        // vocal_server_.update_room(event.roomName(), event.userName());
        return true;
    }

	@Override
	public boolean handleLeaveRoom(LeaveRoomEvent event)
    {
        currentRoom_ = null;
        // vocal_server_.update_room(event.roomName(), event.userName());
        return true;
    }

	@Override
	public boolean handleNewRoom(NewRoomEvent event)
	{
		// vocal_server_.add_room(new ServerRoom(event.room(), vocal_server_));
		return true;
	}

	@Override
	public boolean handleRemoveRoom(RemoveRoomEvent event)
	{
		// vocal_server_.remove_room(event.room());
		return true;
	}

	@Override
	public boolean handleVoice(VoiceEvent event)
	{
		Log.LOG(Log.Level.INFO, "handleVoice");
		return true;
	}

	@Override
	public boolean handleText(TextEvent event)
	{
		Log.LOG(Log.Level.INFO, "handleText from " + event.userName() + ": " + event.textPacket());
		return true;
	}
	
	@Override
	public boolean handleChangePseudo(ChangePseudoEvent event)
	{
		Log.LOG(Log.Level.INFO, "handleChangePseudo from " + event.oldPseudo() + " to " + event.newPseudo());
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
			vocal_server_.remove_client(this);
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
        for (ClientConnection client_conn : vocal_server_.clients().values())
        {
			ConnectionEvent connection_event = new ConnectionEvent(uuid_, client_conn.user_name_);
            send(connection_event);
        }
        return true;
	}

// PRIVATE

	// Name gotten from handshake
	private String user_name_;

	// Current room
	private ServerRoom currentRoom_;

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
