package client.stub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import utilities.Datagram;
import utilities.events.Event;
import utilities.events.HandshakeEvent;
import utilities.events.VoiceEvent;
import utilities.RingBuffer;
import utilities.SoundPacket;
import utilities.infra.Log;

/*
 * The Client class represents the low-level interface for network communication
 *
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
	public Client(String host, int port) throws UnknownHostException, IOException
    {
        host_ = host;
        port_ = port;

        try
        {
            socket_ = new Socket(InetAddress.getByName(host), port);
            input_stream_ = new ObjectInputStream(socket_.getInputStream());
            output_stream_ = new ObjectOutputStream(socket_.getOutputStream());
            Log.LOG(Log.Level.INFO, "Connected to " + host + " on port " + port);
        }
        catch (UnknownHostException e)
        {
        	e.printStackTrace();
        	throw e;
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error instanciating Client: "  + e.getMessage());
            assert false: "Error constructing Client";
            throw e;
        }
    }

    // WIP
    public ConnectState connect(final String user_name)
    {
       return handshake(user_name);
    }

    public void disconnect()
    {
        close();
    }

    public void start()
    {
        start_listening();
    }

    public Event getEvent()
    {
        return event_queue_.pop();
    }

    public void send_event(Event event)
    {
        try
        {
            output_stream_.writeObject((Event) event);
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Sending an event of type " + event.type().name() + " failed");
        }
    }
/*
    public void start_recording_thread() throws Exception
    {
        try
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run(){
                    try
                    {
                        boolean res = microphone_.open();

                        if (!res)
                        {
                            Log.LOG(Log.Level.ERROR, "Error trying to open microphone");
                            return;
                        }

                        microphone_.start();

                        int numBytesRead;
                        
                        Log.LOG(Log.Level.INFO, "Microphone starting");
                        while (running_.get())
                        {
                            // Reading audio data from the microphone and writing it to data[]
                            byte[] data = new byte[SoundPacket.DEFAULT_DATA_LENGTH];
                            numBytesRead = microphone_.read(data, 0, data.length);

                            // Calculating absolute value mean to decide whether or not send the packet
                            int sum = 0;
                            for (int x : data)
                            {
                                sum += Math.abs(x);
                            }
                            System.out.println("[DEBUG] Sum microphone : [" + Integer.toString(sum) + "]");

                            SoundPacket sound_packet = null;

                            // Sending a null packet if the average sample is too low
                            if ((sum / data.length) >= 1)
                            {
                                sound_packet = new SoundPacket(data);
                            }

                            VoiceEvent voice_event = new VoiceEvent(null, sound_packet);
                            output_stream_.writeObject((Event) voice_event);
                        }
                        microphone_.stop();
                        Log.LOG(Log.Level.INFO, "Stopping microphone");

                        return;
                    }
                    catch (java.net.SocketException err)
                    {
                        Log.LOG(Log.Level.ERROR, "Socket has been closed: " + err);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.LOG(Log.Level.ERROR, "Error in recording thread");
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.ERROR, "Error recording thread: " + e);
        }
    }

    public void start_listening_thread() throws Exception
    {
       try
        {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run(){
                    try
                    {
                        Log.LOG(Log.Level.INFO, "Listening thread is running");
                        while (running_.get())
                        {
                            try
                            {
                                if (socket_.getInputStream().available() > 0)
                                {
                                    VoiceEvent event = (VoiceEvent) read();
                                    if (event == null)
                                    {
                                        continue;
                                    }

                                    // Find the channel associated to the datagramn client uuid
                                    AudioChannel channel = audio_channels_.get(event.uuid());

                                    // If none exists, create one
                                    // TODO: Add a thread pool to the client
                                    if (channel == null)
                                    {
                                        channel = new AudioChannel(event.uuid());
                                        audio_channels_.put(event.uuid(), channel);
                                        channel.start();
                                    }
                                    channel.push(event);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.LOG(Log.Level.ERROR, "Client listening thread error in while loop: " + e);
                                break;
                            }
                        } 

                    }
                    catch (Exception e)
                    {
                        Log.LOG(Log.Level.ERROR, " Client listening thread global error" + e);
                        e.printStackTrace();
                    }
                    finally
                    {
                        Log.LOG(Log.Level.INFO, "Client listening thread shutting down");
                    }
                }
            });
            thread.start();
        }
        catch (Exception e )
        {
            Log.LOG(Log.Level.ERROR, e.getMessage());
        }
    }
*/
// PRIVATE METHODS

    private void start_listening()
    {
        try
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run(){
                    try
                    {
                        Log.LOG(Log.Level.INFO, "Listening thread is running");
                        while (running_.get())
                        {
                            try
                            {
                                Event event = read();

                                if (event == null)
                                {
                                    Log.LOG(Log.Level.WARNING, "Couldnt read new event in listening loop");
                                    continue;
                                }

                                boolean res = event_queue_.push(event);

                                if (res == false)
                                {
                                    Log.LOG(Log.Level.ERROR, "Could not push into event queue");
                                }
                            }
                            catch (Exception e)
                            {
                                Log.LOG(Log.Level.ERROR, "Client listening thread error in while loop: " + e);
                                break;
                            }
                        } 

                    }
                    catch (Exception e)
                    {
                        Log.LOG(Log.Level.ERROR, " Client listening thread global error" + e);
                        e.printStackTrace();
                    }
                    finally
                    {
                        Log.LOG(Log.Level.INFO, "Client listening thread shutting down");
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.ERROR, "Error in starting listening thread: " + e);
        }
    }

    private Event read()
    {
        Event event = null;

        try
        {
            event = (Event) input_stream_.readObject();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Client error in read: " + e);
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.INFO, "Client read a Non-Event Object: " + e);
            e.printStackTrace();
        }

        return event;
    }

    private Event read_noblock()
    {
        Event event = null;

        try
        {
            if (socket_.getInputStream().available() > 0)
            {
                event = (Event) input_stream_.readObject();
            }
            else
            {
                Log.LOG(Log.Level.WARNING, "Client can't read: stream unavailable");
            }
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Client error in read: " + e);
        }
        catch (ClassNotFoundException e)
        {
            Log.LOG(Log.Level.INFO, "Client read a Non-Event Object: " + e);
        }

        return event;
    }

    // [WIP]
    private ConnectState handshake(final String name)
    {
        HandshakeEvent event = null;

        int try_counter = 0;
        while (event == null && try_counter <= 5)
        {
            event = (HandshakeEvent) read();
            ++try_counter;
        }
        
        if (try_counter == 5)
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
        int magic_word = new Random().ints(0, 42133742).findFirst().getAsInt();

        event.userName(name);
        event.magicWord(magic_word);
        event.state(HandshakeEvent.State.NAMESET);
        
        send_event(event);
        event = null;
        
        while (event == null && try_counter <= 5)
        {
            event = (HandshakeEvent) read();
            ++try_counter;
        }
        
        if (try_counter == 5)
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
        if (event.magicWord() != magic_word)
        {
            Log.LOG(Log.Level.ERROR, "Handshake failed: magic word has been changed");
            return ConnectState.BYE;
        }

        if (try_counter != 2)
        {
            Log.LOG(Log.Level.WARNING, "Handshake did not receive a HandshakeEvent at first: {" + Integer.toString(try_counter) + "}");
        }

        // Starting listening thread
        start();

        // Notifying server that we are ready to listen for incoming events
        event.state(HandshakeEvent.State.LISTENING);
        send_event(event);

        return ConnectState.OK;
    }

    private boolean close()
    {
        try
        {
            output_stream_.close();
            socket_.close();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error closing socket: " + e);
            return false;
        }
        finally
        {
            running_.compareAndSet(true, false);
        }
        return true;
    }

// PRIVATE ATTRIBUTES

    private RingBuffer<Event> event_queue_ = new RingBuffer<Event>();

    private HashMap<UUID, AudioChannel> audio_channels_ = new HashMap<UUID, AudioChannel>();

    // Private Attributes
    private String host_;
    private int    port_;
    private Socket socket_;

    // TODO: To be set to true when both threads are running
    //       Set to true for testing
    private AtomicBoolean running_ = new AtomicBoolean(true);

    // Voice IO
    private Microphone microphone_ = new Microphone();
    private Speaker    speaker_ = new Speaker();

	// Input Stream
	private ObjectInputStream input_stream_ ;

	// Output Stream
	private ObjectOutputStream output_stream_;

}
