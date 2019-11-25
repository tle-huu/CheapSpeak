package client.stub;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.LineUnavailableException;

import client.gui.TreeRoom;
import client.stub.Client;
import utilities.infra.Log;
import utilities.SoundPacket;
import utilities.events.VoiceEvent;

public class AudioProcessor
{
	
// PUBLIC METHODS
	
	// Constructor
	public AudioProcessor(final Client client, final String userName, final boolean isMuted, final double amplification)
	{
		client_ = client;
		userName_ = userName;
        isMuted_ = new AtomicBoolean(isMuted);
        amplification_ = amplification;
	}
	
    public void shutdown()
    {
        // Stop the threads
    	running_.set(false);
    	
    	// Close microphone
    	microphone_.close();
    }

	public void mute()
	{
    	// Set the atomic boolean isMuted to true
    	isMuted_.set(true);
    	
    	Log.LOG(Log.Level.INFO, "Muting microphone");
	}
	
	public void unmute() 
	{
        // Restart the microphone thread
		lock_.lock();
        cond_.signal();
        lock_.unlock();

        // Set the atomic boolean isMuted to false 
		isMuted_.set(false);

		Log.LOG(Log.Level.INFO, "Unmuting microphone");
	}
	
	public boolean startMicrophone()
	{
		// Instantiate microphone
		try
		{
			microphone_ = new Microphone();
		}
		catch (LineUnavailableException e)
		{
			Log.LOG(Log.Level.ERROR, "Error instanciating Microphone: " + e.getMessage());
			return false;
		}
		
		// Open microphone
		boolean isMicrophoneOpen = microphone_.open();
        if (!isMicrophoneOpen)
        {
        	return false;
        }
        
        // Start microphone
        microphone_.start();
        
        // Set the atomic boolean running to true
        running_.set(true);
        
        // Start microphone thread
        startMicrophoneThread();
        
        // Start is talking thread
        startIsTalkingThread();
        
        return true;
	}
	
	public void playSoundPacket(final VoiceEvent event)
	{
        // Find the channel associated to the datagramn client uuid
        AudioChannel channel = audioChannels_.get(event.uuid());

        // If none exists, create one
        if (channel == null)
        {
            channel = new AudioChannel(event.uuid());
            audioChannels_.put(event.uuid(), channel);
            channel.start();
        }

        // Amplification and speaker icon
        if (event.soundPacket() != null)
        {
        	// Amplification
	        event.soundPacket().amplify(amplification_);
	        
	        // Trigger the speaker icon
	        isTalking_.put(event.userName(), SPEAKER_ICON_MIN_TIME);
        	tree_.repaint(50L);
        }
        
        // Play the sound
        channel.push(event);
	}
	
	public void remove(final String userName, final UUID uuid)
	{
		audioChannels_.remove(uuid);
		isTalking_.remove(userName);
	}
	
	public void setTree(final TreeRoom tree)
	{
		tree_ = tree;
	}
	
	public void setAmplification(final double amplification)
	{
		amplification_ = amplification;
	}
	
	public boolean isTalking(final String userName)
	{
		if (!isTalking_.containsKey(userName))
		{
			return false;
		}
		return isTalking_.getOrDefault(userName, 0) > 0;
	}
	
// PRIVATE METHODS
	
	private void startMicrophoneThread() 
	{
		// Create microphone thread
		Thread thread = new Thread(new Runnable()
			{
				@Override
                public void run()
                {
                    while (running_.get())
                    {
                        while (running_.get() && !isMuted_.get())
                        {
                            // Reading audio data from the microphone and writing it to data[]
                            byte[] data = new byte[SoundPacket.DEFAULT_DATA_LENGTH];
                            microphone_.read(data, 0, data.length);

                            // Calculating absolute value mean to decide whether or not send the packet
                            int sum = 0;
                            for (int x: data)
                            {
                            	sum += Math.abs(x);
                            }
                            
                            SoundPacket soundPacket = null;

                            // Sending a null packet if the average sample is too low
                            if (sum / data.length >= 1)
                            {
                            	soundPacket = new SoundPacket(data);
                            	isTalking_.put(userName_, SPEAKER_ICON_MIN_TIME);
                            	tree_.repaint(50L);
                            }

                            VoiceEvent voiceEvent = new VoiceEvent(null, userName_, soundPacket);
                            client_.sendEvent(voiceEvent);
                        }

                        // Pause the thread
                        lock_.lock();
                        cond_.awaitUninterruptibly();
                        lock_.unlock();
                    }
                    Log.LOG(Log.Level.INFO, "The microphone thread stopped running");
                }
			}
		);
		
		// Start microphone thread
        thread.start();
	}
	
	private void startIsTalkingThread() 
	{
		Thread thread = new Thread(new Runnable()
			{
				@Override
	            public void run()
	            {
					while (running_.get())
					{
						isTalking_.forEachKey(Long.MAX_VALUE, key ->
							{
								int value = Math.max(-1, isTalking_.getOrDefault(key, 0) - 1);
								if (value == 0)
								{
									tree_.repaint(50L);
								}
								isTalking_.put(key, value);
							}
						);
						try
						{
							Thread.sleep(100L);
						}
						catch (InterruptedException e)
						{
							Log.LOG(Log.Level.ERROR, "The talking thread cannot sleep");
						}
					}
	            }
			}
		);
		
		thread.start();
	}
    
// PRIVATE ATTRIBUTES
	
    private final String userName_;
    private final Client client_;

    private AtomicBoolean running_ = new AtomicBoolean(false);
	private AtomicBoolean isMuted_;

    private Microphone microphone_;
    
    private double amplification_ = 1.0d;

    private Hashtable<UUID, AudioChannel>      audioChannels_ = new Hashtable<UUID, AudioChannel>();
    private ConcurrentHashMap<String, Integer> isTalking_ = new ConcurrentHashMap<String, Integer>();
	
    private final Lock      lock_ = new ReentrantLock();
    private final Condition cond_ = lock_.newCondition();
    
    private TreeRoom  tree_;
    private final int SPEAKER_ICON_MIN_TIME = 6; // in ds (deciseconds)
 
}
