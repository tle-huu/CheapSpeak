import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;


/*
 *	Wrapper
 *
 */
public class Speaker
{

// PRIVATE CONST
	static private final float  SAMPLE_RATE = 8000.0f;
	static private final int 	SAMPLE_SIZE = 16;

// PUBLIC

	public Speaker()
	{
		try
		{
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format_);
			speaker_ = (SourceDataLine) AudioSystem.getLine(info);
	    }
	    catch (LineUnavailableException err)
	    {
	    	Log.LOG(Log.Level.ERROR, "Error instanciating Speaker: " + err.getMessage());
	    	assert false : "Impossible to construct Speaker";
	    }
	}

	public boolean open()
	{
		try
		{
			speaker_.open(format_);
		}
		catch (LineUnavailableException err)
		{

	    	Log.LOG(Log.Level.ERROR, "Error opening Speaker line: " + err.getMessage());
			return false;
		}
		return true;
	}

	public void start()
	{
		speaker_.start();
	}

	public int write(byte[] buffer, int offset, int length)
	{
         return speaker_.write(buffer, 0, length);

	}

	public void stop()
	{
		speaker_.stop();
		speaker_.flush();
	}

// PRIVATE

	private final AudioFormat 		format_ = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, /* channels */ 1, true, true);
	private SourceDataLine 			speaker_;

}