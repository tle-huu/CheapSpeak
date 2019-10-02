import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


/*
 *	Wrapper
 *
 */
public class Microphone
{

// PRIVATE CONST
	static private final float 	SAMPLE_RATE = 8000.0f;
	static private final int 	SAMPLE_SIZE = 8;

// PUBLIC

	public Microphone()
	{
		try
		{
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format_);
			microphone_ = (TargetDataLine) AudioSystem.getLine(info);
	    }
	    catch (LineUnavailableException err)
	    {
	    	Log.LOG(Log.Level.ERROR, "Error instanciating Microphone: " + err.getMessage());
	    	assert false : "Impossible to construct microphone";
	    }
	}

	public boolean open()
	{
		try
		{
			microphone_.open(format_);
		}
		catch (LineUnavailableException err)
		{

	    	Log.LOG(Log.Level.ERROR, "Error opening microphone line: " + err.getMessage());
			return false;
		}
		return true;
	}

	public void start()
	{
		microphone_.start();
	}

	public int read(byte[] buffer, int offset, int length)
	{
         return microphone_.read(buffer, 0, length);

	}

	public void stop()
	{
		microphone_.stop();
		microphone_.flush();
	}

// PRIVATE

	private final AudioFormat 		format_ = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, /* channels */ 1, true, true);
	private TargetDataLine 			microphone_;

}