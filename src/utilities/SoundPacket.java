package utilities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SoundPacket implements Serializable
{

// PUBLIC METHODS
	
	// Constructor
    public SoundPacket(byte[] data)
    {
        data_ = data;
    }

    public byte[] data()
    {
        return data_;
    }

// PUBLUC CONST
    
	public final static int DEFAULT_DATA_LENGTH = 4096;
	
// PRIVATE ATTRIBUTE
	
	// Actual PCM data. If null, random noise should be played
	private byte[] data_;
	
}
