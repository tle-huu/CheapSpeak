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
import javax.sound.sampled.SourceDataLine;

/*
 * A ClientConnection represents the connection worker associated to a client
 *
 * Single Thread listening for incoming packets and sending them to the broadcasting thread
 *
 */

public class ClientConnection implements Runnable
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
		try
		{
	        // TODO: Tie this thread to the main server thread
			while (true)
			{
				try
				{
					if (!socket_.isConnected() || socket_.isClosed() || socket_.isInputShutdown())
					{
						close();
						break;
					}

					if (socket_.getInputStream().available() > 0)
					{
						Datagram datagram = (Datagram) input_stream_.readObject();

						// Setting emitter uuid
						datagram.client_uuid(uuid_);

						// Pushing to broadcaster thread
						broadcast(datagram);

					}
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

	public boolean send_datagram(final Datagram datagram) throws Exception
	{
		try
		{
		    output_stream_.writeObject(datagram);
		    return true;
		}
		catch (IOException e)
		{
			return false;
		}
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
		return socket_.isConnected();
	}

	public void close()
	{
		if (!running_)
		{
			return ;
		}

		try 
		{
			vocal_server_.remove_client(this);
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
		running_ = false;

		}
	}

// PRIVATE

	private void broadcast(final Datagram datagram) throws Exception
	{
		try
		{
			boolean res = vocal_server_.add_to_broadcast(datagram);
			// vocal_server_.broadcast(datagram, uuid_);
		}
		catch (Exception e)
		{
			Log.LOG(Log.Level.ERROR, "ClientConnection broadcast error: " + e.getMessage());
		}
	}

// PRIVATE

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