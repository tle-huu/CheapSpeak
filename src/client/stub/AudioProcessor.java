package client.stub;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.gui.TreeRoom;
import client.stub.Client;
import utilities.infra.Log;
import utilities.SoundPacket;
import utilities.events.VoiceEvent;

public class AudioProcessor
{
	
// PUBLIC METHODS
	
	// Constructor
	public AudioProcessor(final Client client, final String userName, final boolean isMuted)
	{
		client_ = client;
		userName_ = userName;
        isMuted_ = new AtomicBoolean(isMuted);
	}
	
    public void shutdown()
    {
        running_.set(false);
    }

	//MICROPHONE 
	// runnable or not ? 
	public void mute()
	{		
		// Close the microphone when client exit the room 
		// We need to flush the data before entering a new room
        Log.LOG(Log.Level.INFO, "Muting microphone");
    	
    	// modify the atomic boolean isMuted to true 
    	isMuted_.set(true);
	}
	
	public void unmute() 
	{
        lock_.lock();
        cond_.signal();
        lock_.unlock();

		isMuted_.set(false);

		Log.LOG(Log.Level.INFO, "Unmuting microphone");
	}
	
	public void startMicrophoneThread() 
	{
		Thread thread = new Thread(new Runnable()
			{
				@Override
                public void run()
                {
                    boolean res = microphone_.open();
                    microphone_.start();
                    int numBytesRead;
                    
                    while (running_.get())
                    {
                        while (!isMuted_.get())
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
                            Log.LOG(Log.Level.DEBUG, "Sum microphone: [" + Integer.toString(sum) + "]");
                            
                            SoundPacket soundPacket = null;

                            // Sending a null packet if the average sample is too low
                            if (sum / data.length >= 1)
                            {
                            	soundPacket = new SoundPacket(data);
                            	isTalking_.put(userName_, SPEAKER_ICON_MIN_TIME);
                            }

                            VoiceEvent voiceEvent = new VoiceEvent(null, userName_, soundPacket);
                            client_.send_event(voiceEvent);
                        }

                        lock_.lock();
                        cond_.awaitUninterruptibly();
                        lock_.unlock();
                    }
                }
			}
		);
		
        running_.set(true);
        thread.start();
        
        startIsTalkingThread();
	}
	
	public void startIsTalkingThread() 
	{
		Thread thread = new Thread(new Runnable()
			{
				@Override
	            public void run()
	            {
					while (true)
					{
						isTalking_.forEachKey(Long.MAX_VALUE, key ->
							{
								int value = Math.max(-1, isTalking_.getOrDefault(key, 0) - 1);
								if (value == 0 || value == SPEAKER_ICON_MIN_TIME)
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
	
	// useful ?
	public void reset()
	{
		
	} 
	
	/*
	public AudioChannel createAudioChannel()
	{
		return new AudioChannel() 
	}
	*/
	
	public void playSoundPacket(final VoiceEvent event)
	{
        // Find the channel associated to the datagramn client uuid
        AudioChannel channel = audioChannels_.get(event.uuid());

        // If none exists, create one
        // TODO: Add a thread pool to the client
        if (channel == null)
        {
            channel = new AudioChannel(event.uuid());
            audioChannels_.put(event.uuid(), channel);
            channel.start();
        }

        channel.push(event);
        
        if (event.soundPacket() != null)
        {
        	isTalking_.put(event.userName(), SPEAKER_ICON_MIN_TIME);
        }
	}
	
	public void remove(final String userName, final UUID uuid)
	{
		audioChannels_.remove(uuid);
		isTalking_.remove(userName);
	}
	
	public void setTree(TreeRoom tree)
	{
		tree_ = tree;
	}
	
	public boolean isTalking(final String userName)
	{
		return isTalking_.getOrDefault(userName, 0) > 0;
	}
    
// PRIVATE ATTRIBUTES
	
    private final String userName_;
    private final Client client_;

    private AtomicBoolean running_ = new AtomicBoolean(false);
	private AtomicBoolean isMuted_;

    private Microphone microphone_ = new Microphone();

    private Hashtable<UUID, AudioChannel>      audioChannels_ = new Hashtable<UUID, AudioChannel>();
    private ConcurrentHashMap<String, Integer> isTalking_ = new ConcurrentHashMap<String, Integer>();
	
    private final Lock      lock_ = new ReentrantLock();
    private final Condition cond_ = lock_.newCondition();
    
    private TreeRoom  tree_;
    private final int SPEAKER_ICON_MIN_TIME = 6; // in ds (deciseconds)
 
}
