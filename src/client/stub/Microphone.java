package client.stub;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utilities.infra.Log;

public class Microphone
{

// PUBLIC METHODS

	// Constructor
	public Microphone() throws LineUnavailableException
	{
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
		microphone_ = (TargetDataLine) AudioSystem.getLine(info);
	}

	public boolean open()
	{
		try
		{
			microphone_.open(FORMAT);
		}
		catch (LineUnavailableException e)
		{
	    	Log.LOG(Log.Level.ERROR, "Error opening microphone line: " + e.getMessage());
			return false;
		}
		return true;
	}

	public void start()
	{
		microphone_.start();
	}

	public int read(final byte[] buffer, final int offset, final int length)
	{
         return microphone_.read(buffer, offset, length);
	}

	public void stop()
	{
		microphone_.stop();
		microphone_.flush();
	}

// PRIVATE ATTRIBUTES

	private final static float SAMPLE_RATE = 8000.0f;
	private final static int   SAMPLE_ENCODING = 8;
	
	// Format: sample rate, sample encoding (in bits), channels number, signed, big endian
	private final static AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_ENCODING, 1, true, true);
	
	private TargetDataLine microphone_;

}
