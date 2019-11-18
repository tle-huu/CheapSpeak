package client.stub;

import java.io.IOException;
import utilities.infra.Log;

import java.util.HashMap;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import utilities.SoundPacket;
import utilities.events.VoiceEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AudioProcessor{
	
	
	//CONSTRUCTOR
	public AudioProcessor(Client client)
	{
		Client client_ = client;
		
	}
	
	//MICROPHONE 
	// runnable or not ? 
	public void mute()
	{		
    //close the microphone when client exit the room 
	// we need to flush the data before entering a new room
	microphone_.stop();
    Log.LOG(Log.Level.INFO, "Muting microphone");
	
	
	// modify the atomic boolean muted to true 
	muted_.compareAndSet(false, true);
	
	//
	}
	
	public void unmute() 
	{
		
		muted_.compareAndSet(true, false);
		Log.LOG(Log.Level.INFO, "Unmuting microphone");

	}
	
	
	// in client ?
	public void start_microhone_thread()  throws Exception  
	{		// using runnable 
		try 
		{
			Thread thread = new Thread(new Runnable())
					{
					@Override
						
				
					}
			
		}
	}
	
		
	// SPEAKER
	public void reset()
	{
		
	} 
	
	/*
	public AudioChannel create_Audio_channel()
	{
		return new AudioChannel() 
	}
	*/
	
	public void play_sound_packet(VoiceEvent event)
	{
		
	}
    
    // PRIVATE
    private AtomicBoolean running_ = new AtomicBoolean(true);
    private Microphone microphone_ = new Microphone();
	private AtomicBoolean muted_ = new AtomicBoolean(false);
    private static ReentrantLock lock  =  new ReentrantLock();
    private Hashtable<UUID, AudioChannel> audio_channels_ = new Hashtable<UUID, AudioChannel>();
	
	//PUBLIC 
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

