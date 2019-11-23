package client.stub;

import java.lang.Math; 
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.LineUnavailableException;

import utilities.SoundPacket;
import utilities.events.VoiceEvent;
import utilities.infra.Log;

public class AudioChannel extends Thread
{

// PUBLIC METHODS
	
	public AudioChannel(UUID clientUuid)
	{
		uuid_ = clientUuid;
	}

	@Override
	public void run()
	{
		// Start speaker
		try
        {
            startSpeaker();
        }
        catch (LineUnavailableException e)
        {
        	Log.LOG(Log.Level.ERROR, "Error instantiating speaker line: " + e.getMessage());
        	return;
        }
		
		// Set the atomic boolean running to true
        running_.set(true);
		
        while (running_.get())
        {
        	// No more message
        	try
        	{
				// Nothing to read from the buffer
            	if (queue_.isEmpty())
            	{
            		Thread.sleep(20L);
            		continue;
            	}
        	}
        	catch (InterruptedException e)
        	{
        		continue;
        	}

            try
            {
				// Poping out from the queue
                VoiceEvent voiceEvent = queue_.get(0);
                queue_.remove(0);

                // Get sound packet
                SoundPacket soundPacket = voiceEvent.soundPacket();

                // Playing random noise if the packet does not contain any data
                if (soundPacket == null)
                {
                    byte[] noise = new byte[SoundPacket.DEFAULT_DATA_LENGTH];

                    for (int i = 0; i < noise.length; ++i)
                    {
                        noise[i] = (byte) ((Math.random() * 3) - 1);
                    }
                    speaker_.write(noise, 0, noise.length);
                }
                else
                {
                    speaker_.write(soundPacket.data(), 0, soundPacket.data().length);
                }
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                Log.LOG(Log.Level.WARNING, "AudioChannel error: cannot get a voice event from the queue");
            }
        }
	}

	public void push(final VoiceEvent voiceEvent)
	{
		queue_.add(voiceEvent);
	}
	
	public void shutdown()
    {
        // Stop the threads
    	running_.set(false);
    }

	public final UUID uuid()
	{
		return uuid_;
	}
	
	private void startSpeaker() throws LineUnavailableException
	{
		// Instantiate speaker
        speaker_ = new Speaker();
        
        // Open speaker
        boolean isOpen = speaker_.open();
        if (!isOpen)
        {
            Log.LOG(Log.Level.ERROR, "Error trying to open speaker");
            return;
        }

        // Start speaker
        speaker_.start();
        
        Log.LOG(Log.Level.INFO, "Audio channel for [" + uuid_.toString() + "] running");
	}

// PRIVATE ATTRIBUTES
	
	private Vector<VoiceEvent> queue_ = new Vector<VoiceEvent>();

    private Speaker speaker_;

    private AtomicBoolean running_ = new AtomicBoolean(false);
    
    private final UUID uuid_;

}
