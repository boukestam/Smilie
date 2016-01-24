package main;

import hearing.Hearing;
import hearing.HearingListener;
import modules.ActivityModule;
import modules.GmailModule;
import modules.Module;
import modules.ModuleManager;
import modules.PomodoroModule;
import modules.SleepModule;
import speech.Smiley;
import speech.Speech;

public class Assistent implements Runnable, HearingListener{
	
	public final static String WELCOME = "Hello, i am smiley, your personal assistent";
	
	private Window window;
	private Speech speech;
	private Smiley smiley;
	private Hearing hearing;
	
	private ModuleManager moduleManager;
	
	public Assistent(){
		smiley = new Smiley(1, 1, Window.FRAME_WIDTH - 3, Window.FRAME_HEIGHT - 3);
		
		window = new Window(smiley);
		
		speech = new Speech(smiley);
		
		hearing = new Hearing();
		hearing.addListener(this);
		
		moduleManager = new ModuleManager();
		
		startModule(new GmailModule(moduleManager, speech));
		startModule(new ActivityModule(moduleManager, speech));
		startModule(new SleepModule(moduleManager, speech));
		
		new Thread(this).start();
		
		speech.speak(WELCOME);
	}
	
	public void run(){
		while(true){
			//speech.speak(randomComments[(int)(Math.random() * randomComments.length)]);
			
			try{
				Thread.sleep(10000);
			}catch(Exception e){
				
			}
		}
	}
	
	public void startModule(Module module){
		moduleManager.addModule(module);
		hearing.addListener(module);
	}

	public void textReceived(String text) {
		if(text.matches("pomodoro")){
			startModule(new PomodoroModule(moduleManager, speech));
		}
	}
}
