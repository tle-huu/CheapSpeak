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

                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        int bytesRead = 0;
                        int numBytesRead;
                        
                        Log.LOG(Log.Level.INFO, "Microphone starting");
                        while (running_.get())
                        {
                            byte[] data = new byte[1024];
                            numBytesRead = microphone_.read(data, 0, data.length);

                            int sum = 0;
                            for (int x : data)
                            {
                                sum += Math.abs(x);
                            }
                            if (sum < 15000)
                            {
                                continue;
                            }
                            System.out.println("Sum microphone : [" + Integer.toString(sum) + "]");


                            bytesRead += numBytesRead;
                            out.write(data, 0, data.length);

                            SoundPacket packet = new SoundPacket(data);

                            Datagram datagram = new Datagram(UUID.randomUUID(), packet);
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

                        boolean res = speaker_.open();

                        if (!res)
                        {
                            Log.LOG(Log.Level.ERROR, "Error trying to open microphone");
                            return;
                        }

                        speaker_.start();

                        while (running_.get())
                        {
                            try
                            {
                                if (socket_.getInputStream().available() > 0)
                                {
                                    Datagram datagram = (Datagram) input_stream_.readObject();

                                    SoundPacket sound_packet = (SoundPacket) datagram.data();
                                    speaker_.write(sound_packet.data(), 0, sound_packet.data().length);
                                    // speakers.drain();

                                }
                            }
                            catch (Exception e)
                            {
                                Log.LOG(Log.Level.ERROR, "ClientConnection error reading input: " + e);
                            }
                        } 

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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

/*
    public void start_send_message() throws IOException
    {
		input_stream_ = new ObjectInputStream(socket_.getInputStream());
		output_stream_ = new ObjectOutputStream(socket_.getOutputStream());

        System.out.println("Write messages to send...");
        Scanner scanner = new Scanner(System.in);

        while (socket_.isConnected())
        {
            String message = scanner.nextLine();
            // message += '\n';
            byte[] buffer = message.getBytes();

            try
            {
                System.out.println("Trying to send : " + new String(buffer));

            	Datagram datagram = new Datagram(UUID.randomUUID(), new String(buffer));
                output_stream_.writeObject(datagram);
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
                continue ;
            }
        }
        scanner.close();
        try
        {
            socket_.close();
        }
        catch (IOException e )
        {
            Log.LOG(Log.Level.ERROR, "error closing: " + e.getMessage());
        }
    }
*/

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
