package speech;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Audio {

	private static TargetDataLine lineIn;
	private static SourceDataLine lineOut;
	
	public static AudioFormat format=new AudioFormat(8000,16,1,true,false);
	
	public static void init(){
	    try{
	    	lineOut=AudioSystem.getSourceDataLine(format);
	    	lineIn=AudioSystem.getTargetDataLine(format);
	    	lineOut.open(format);
	    	lineIn.open(format);
	    }catch(LineUnavailableException ex){
	        System.out.println("Cannot initialise audio");
	    }
	}
	
	public static Sound getChunk(long ms){
		byte data[]=new byte[(int) (ms/1000.0 * format.getSampleRate())];
		lineIn.read(data,0,data.length);
		return new Sound(data);
	}
	
	public static void play(Sound audio){
		lineOut.start();
		lineOut.write(audio.bytes,0,audio.bytes.length);
		lineOut.drain();
		lineOut.stop();
	}
	
	public static void close(){
		lineOut.stop();
		lineOut.close();
		lineIn.stop();
		lineIn.close();
	}
	
	public static Sound combine(Sound a, Sound b){
		short[] aShorts = new short[a.bytes.length/2];
		ByteBuffer.wrap(a.bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(aShorts);
		
		short[] bShorts = new short[b.bytes.length/2];
		ByteBuffer.wrap(b.bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(bShorts);
		
		int aLength = aShorts.length;
		int bLength = bShorts.length;
		
		float overlaySeconds = 0.2f;
		int overlaySize = Math.min(Math.min(aLength / 2, bLength / 2), (int)(format.getSampleRate() * overlaySeconds));
		int overlayStart = aLength - (overlaySize / 2);
		int overlayEnd = overlayStart + overlaySize;
		
		int totalSize = aLength + bLength;
		
		short[] shorts = new short[totalSize];
		
		for(int i = 0; i < totalSize; i++){
			if(i < overlayStart){
				shorts[i] = aShorts[i];
			}else if(i >= overlayEnd){
				shorts[i] = bShorts[i - aLength];
			}else{
				float overlayAmount = ((float)i - (float)overlayStart) / (float)overlaySize;
				
				int aIndex = i < aLength ? i : overlayStart + (i - aLength) - 1;
				int bIndex = i >= aLength ? i - aLength : (aLength - i);
				
				short newValue = (short)(
						((float)aShorts[aIndex] * (1f - overlayAmount)) + 
						((float)bShorts[bIndex] * overlayAmount));
				
				shorts[i] = newValue;
			}
		}
		
		byte[] bytes = new byte[shorts.length * 2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);
		
		return new Sound(bytes);
	}
}
