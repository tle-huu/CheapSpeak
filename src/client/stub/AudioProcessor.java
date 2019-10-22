package client.stub;

import java.io.IOException;
import utilities.infra.Log;


public class AudioProcessor{
	
	
	//constructor
	public AudioProcessor(Client client)
	{
	}

	public void start_microphone_thread() throws Exception
    {
        try
        {
            Thread thread = new Thread(new Runnable()) {
                @Override
                public void run(){
                	
                	public void start_microphone_Thread(){}
                }

                    
            });
            thread.start();
        }
        catch (Exception e)
        {
            Log.LOG(Log.Level.ERROR, "Error recording thread: " + e);
        }
    }

	
	public void mute(){}
	public void unmute(){}
	public void reset(){} 
	
}