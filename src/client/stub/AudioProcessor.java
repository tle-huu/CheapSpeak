package client.stub;

import java.io.IOException;
import utilities.infra.Log;

import java.util.HashMap;
import client.stub.Client;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import utilities.SoundPacket;
import utilities.events.Event;
import utilities.events.VoiceEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AudioProcessor{
	
	
	//CONSTRUCTOR
	public AudioProcessor(Client client, final String userName, final boolean muted)
	{
		client_ = client;

        muted_ = new AtomicBoolean(muted);
        userName_ = userName;
		
	}
	

    public void shutdown()
    {
        running_.compareAndSet(false, true);
    }



	//MICROPHONE 
	// runnable or not ? 
	public void mute()
	{		
    //close the microphone when client exit the room 
	// we need to flush the data before entering a new room


        Log.LOG(Log.Level.INFO, "Muting microphone");
    	
    	// modify the atomic boolean muted to true 
    	muted_.compareAndSet(false, true);
    	
	//
	}
	
	public void unmute() 
	{
        lock_.lock();
        cond_.signal();
        lock_.unlock();

		muted_.compareAndSet(true, false);

		Log.LOG(Log.Level.INFO, "Unmuting microphone");
	}
	
	
	// in client ?
	public void startMicrophoneThread() 
	{		// using runnable 
		try 
		{
			Thread thread = new Thread(new Runnable()
				{
					@Override
                    public void run()
                    {
                        microphone_.open();
                        microphone_.start();
                        int numBytesRead;
                        
                        while (running_.get())
                        {
                            while (!muted_.get())
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

                                VoiceEvent voice_event = new VoiceEvent(null, userName_, sound_packet);
                                client_.send_event(voice_event);
                            }

                            lock_.lock();
                            try
                            {
                                cond_.await();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            finally
                            {
                                lock_.unlock();
                            }
                        }
                    }
				}
			);

            running_.set(true);
            thread.start();

		}
        catch (Exception e)
        {
            Log.LOG(Log.Level.ERROR, "Error recording thread: " + e);
        }
	}
	
	public void reset()
	{
		
	} 
	
	/*
	public AudioChannel create_Audio_channel()
	{
		return new AudioChannel() 
	}
	*/
	
	public void playSoundPacket(VoiceEvent event)
	{
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
    
    // PRIVATE ATTRIBUTES
	
    private final String userName_;
    private final Client client_;

    private AtomicBoolean running_ = new AtomicBoolean(false);

	private AtomicBoolean muted_;

    private Microphone microphone_ = new Microphone();

    private Hashtable<UUID, AudioChannel> audio_channels_ = new Hashtable<UUID, AudioChannel>();
	
    private final Lock      lock_ = new ReentrantLock();
    private final Condition cond_ = lock_.newCondition();
 
}
