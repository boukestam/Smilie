package main;

import speech.Audio;
import speech.Smiley;
import speech.Speech;

public class Assistent implements Runnable{
	
	private Window window;
	private Speech speech;
	private Smiley smiley;
	
	public Assistent(){
		smiley = new Smiley(0, 0, Window.FRAME_WIDTH - 1, Window.FRAME_HEIGHT - 1);
		
		window = new Window(smiley);
		
		Audio.init();
		speech = new Speech(smiley);
		
		speech.speak("haloo ik ben smailie");
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run(){
		while(true){
			update();
			
			try{
				Thread.sleep(10);
			}catch(Exception e){
				
			}
		}
	}
	
	public void update(){
		window.draw();
	}
}
