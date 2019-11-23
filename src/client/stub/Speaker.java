package client.stub;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import utilities.infra.Log;

public class Speaker
{

// PUBLIC METHODS

	// Constructor
	public Speaker() throws LineUnavailableException
	{
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, FORMAT);
		speaker_ = (SourceDataLine) AudioSystem.getLine(info);
	}

	public boolean open() throws LineUnavailableException
	{
		try
		{
			speaker_.open(FORMAT);
		}
		catch (LineUnavailableException e)
		{
	    	Log.LOG(Log.Level.ERROR, "Error opening speaker line: " + e.getMessage());
			return false;
		}
		return true;
	}

	public void start()
	{
		speaker_.start();
	}

	public int write(final byte[] buffer, final int offset, final int length)
	{
         return speaker_.write(buffer, offset, length);
	}

	public void stop()
	{
		speaker_.stop();
		speaker_.flush();
	}

// PRIVATE ATTRIBUTES

	private final static float SAMPLE_RATE = 8000.0f;
	private final static int   SAMPLE_ENCODING = 8;
	
	// Format: sample rate, sample encoding (in bits), channels number, signed, big endian
	private final static AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_ENCODING, 1, true, true);
	
	private SourceDataLine speaker_;

}
