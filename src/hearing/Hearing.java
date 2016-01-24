package hearing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;
import com.darkprograms.speech.recognizer.GoogleResponse;

public class Hearing implements Runnable{

	private List<HearingListener> listeners;
	
	public Hearing(){
		listeners = new CopyOnWriteArrayList<HearingListener>();
		
		new Thread(this).start();
	}
	
	public synchronized void addListener(HearingListener listener){
		listeners.add(listener);
	}
	
	public void run(){
		Scanner s = new Scanner(System.in);
		while(true){
			String text = s.nextLine();
			
			for(HearingListener listener : listeners){
				listener.textReceived(text);
			}
		}
	}
	
//	public void run(){
//		MicrophoneAnalyzer mic = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
//		mic.setAudioFile(new File("AudioTestNow.flac"));
//		mic.open();
//		
//		while (true) {
//			final int THRESHOLD = 8;
//			int volume = getMicVolume(mic);
//			boolean isSpeaking = (volume > THRESHOLD);
//			
//			if (isSpeaking) {
//				try {
//					System.out.println("RECORDING...");
//					mic.captureAudioToFile(mic.getAudioFile());// Saves audio to
//																// file.
//					do {
//						Thread.sleep(1000);// Updates every second
//					} while (getMicVolume(mic) > THRESHOLD);
//					
//					System.out.println("Recording Complete!");
//					System.out.println("Recognizing...");
//					Recognizer rec = new Recognizer(Recognizer.Languages.AUTO_DETECT, "AIzaSyB0IReUC-bxD73Fs-TSL4gFLLa8DDjdZuU");
//					GoogleResponse response = rec.getRecognizedDataForFlac(mic.getAudioFile(), 3);
//					displayResponse(response);// Displays output in Console
//					System.out.println("Looping back");// Restarts loops
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					System.out.println("Error Occured");
//				}
//			}
//		}
//	}
	
	public int getMicVolume(MicrophoneAnalyzer mic){
		byte[] buffer = new byte[mic.getTargetDataLine().getFormat().getFrameSize()];
        mic.getTargetDataLine().read(buffer, 0, buffer.length);
        return getLevel(buffer);
	}
	
	public int getLevel(byte[] chunk){
		short[] shorts = new short[chunk.length/2];
		ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		
		long totalVolume = 0;
		
		for(short s : shorts){
			totalVolume += Math.abs(s);
		}
		
		return (int) ((totalVolume / shorts.length) / 100);
    }
	
	private void displayResponse(GoogleResponse gr){
	    if(gr.getResponse() == null){
	        System.out.println((String)null);
	        return;
	    }
	    System.out.println("Google Response: " + gr.getResponse());
	    System.out.println("Google is " + Double.parseDouble(gr.getConfidence())*100 + "% confident in"
	            + " the reply");
	    System.out.println("Other Possible responses are: ");
	    for(String s: gr.getOtherPossibleResponses()){
	        System.out.println("\t" + s);
	    }
	}   
}
