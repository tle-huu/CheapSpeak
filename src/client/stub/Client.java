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
import java.util.Scanner;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/*
 *
 *
 */
public class Client
{
    public Client(String host, int port) throws UnknownHostException, IOException
    {
        host_ = host;
        port_ = port;

        try
        {
            socket_ = new Socket(InetAddress.getByName(host), port);
            input_stream_ = new ObjectInputStream(socket_.getInputStream());
            output_stream_ = new ObjectOutputStream(socket_.getOutputStream());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.LOG(Log.Level.ERROR, "Error instanciating Client: "  + e.getMessage());
            assert false : "Error constructing Client";
            throw e;
        }
        Log.LOG(Log.Level.INFO, "Connected to " + host + " on the port " + port);
    }

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

                            Datagram datagram = new Datagram(sound_packet);
                            output_stream_.writeObject(datagram);
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
                                    Datagram datagram = (Datagram) input_stream_.readObject();

                                    // Find the channel associated to the datagramn client uuid
                                    AudioChannel channel = audio_channels_.get(datagram.client_uuid());

                                    // If none exists, create one
                                    // TODO: Add a thread pool to the client
                                    if (channel == null)
                                    {
                                        channel = new AudioChannel(datagram.client_uuid());
                                        audio_channels_.put(datagram.client_uuid(), channel);
                                        channel.start();
                                    }
                                    channel.push(datagram);
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

// PRIVATE

    private HashMap<UUID, AudioChannel> audio_channels_ = new HashMap<UUID, AudioChannel>();

    // Private Attributes

    private String host_;
    private int    port_;
    private Socket socket_;

    // TODO: To be set to true when both threads are running
    //       Set to true for testing
    private AtomicBoolean running_ = new AtomicBoolean(true);

    // Voice IO
    Microphone  microphone_ = new Microphone();
    Speaker     speaker_ = new Speaker();

	// Input Stream
	private ObjectInputStream input_stream_ ;

	// Output Stream
	private ObjectOutputStream output_stream_;

}
