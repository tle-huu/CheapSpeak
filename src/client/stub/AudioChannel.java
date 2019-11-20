package client.stub;

import java.lang.Math; 
import java.util.UUID;
import java.util.Vector;

import utilities.Datagram;
import utilities.SoundPacket;
import utilities.events.VoiceEvent;
import utilities.infra.Log;

public class AudioChannel extends Thread
{

// PUBLIC
	public AudioChannel(UUID client_uuid)
	{
		uuid_ = client_uuid;
	}

	@Override
	public void run()
	{
		try
        {
            Log.LOG(Log.Level.INFO, "Audio channel for [" + uuid_.toString() + "] running");

            boolean res = speaker_.open();

            if (!res)
            {
                Log.LOG(Log.Level.ERROR, "Error trying to open microphone");
                return;
            }

            speaker_.start();

            while (true)
            {
            	// No more message
            	try
            	{
					// Nothing to read from the buffer
                	if (queue_.isEmpty())
                	{
                		Thread.sleep(20);
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
                    VoiceEvent voice_event = queue_.get(0);
                    queue_.remove(0);

                    SoundPacket sound_packet = voice_event.soundPacket();

                    // Playing random noise if the packet does not contain any data
                    if (sound_packet == null)
                    {
                        Log.LOG(Log.Level.INFO, "xxxxxx bonjour");

                        byte[] noise = new byte[SoundPacket.DEFAULT_DATA_LENGTH];

                        for (int i = 0; i < noise.length; i++)
                        {
                            noise[i] = (byte) ((Math.random() * 3) - 1);
                        }
                        // speaker_.write(noise, 0, noise.length);
                    }
                    else
                    {
                        Log.LOG(Log.Level.INFO, "xxxxxx salut");
                    }
                    speaker_.write(sound_packet.data(), 0, sound_packet.data().length);
                }
                catch (Exception e)
                {
                    Log.LOG(Log.Level.ERROR, "AudioChannel error: " + e);
                }
            } 

        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
	}

// PUBLIC
	public void push(final VoiceEvent voice_event)
	{
		queue_.add(voice_event);
	}

	public final UUID uuid()
	{
		return uuid_;
	}

// PRIVATE
	private Vector<VoiceEvent> queue_ = new Vector<VoiceEvent>();

    private Speaker     	 speaker_ = new Speaker();

    private final UUID 		 uuid_;



}