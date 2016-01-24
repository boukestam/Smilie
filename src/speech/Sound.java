package speech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Sound {
	
	public byte[] bytes;
	
	public Sound(String path){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try{
			AudioInputStream in = AudioSystem.getAudioInputStream(new File(path));
	
			int read;
			byte[] buff = new byte[1024];
			
			while ((read = in.read(buff)) > 0){
			    out.write(buff, 0, read);
			}
			
			out.flush();
			
			in.close();
		}catch(Exception e){
			System.err.println("Error loading wav file: " + path);
		}
		
		this.bytes = out.toByteArray();
	}
	
	public Sound(byte[] bytes){
		this.bytes = bytes;
	}
	
	public int getMillisecondLength(){
		return (int) ((((float)this.bytes.length / 2f) / (float)Audio.format.getSampleRate()) * 1000.0f);
	}
	
	public void save(String filename){
		AudioFormat frmt = new AudioFormat(Audio.format.getSampleRate(), 16, 1, true, false);
		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(bytes), frmt, bytes.length / frmt.getFrameSize());

		try {
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
