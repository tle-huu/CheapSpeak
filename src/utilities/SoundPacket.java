package utilities;

import java.io.Serializable;
import javax.sound.sampled.AudioFormat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * some sound
 * @author dosse
 */
public class SoundPacket implements Serializable{
	// Format: sample rate, sample encoding (in bits), channels number, signed, big endian
	// 11.025khz, 8bit, mono, signed, big endian (changes nothing in 8 bit) ~8kb/s
    public final static AudioFormat DEFAULT_FORMAT = new AudioFormat(11025f, 8, 1, true, true);
    public final static int DEFAULT_DATA_LENGTH = 4096;
    private byte[] data_; // Actual PCM data. If null, random noise should be played.

    public SoundPacket(byte[] data) {
        this.data_ = data;
    }

    public byte[] data() {
        return data_;
    }
    
}
