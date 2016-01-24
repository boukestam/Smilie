package modules;

import speech.Speech;

public class PomodoroModule extends Module{
	
	private static final String POMODORO_START = "Working time starts now, have fun";
	private static final String POMODORO_HALF = "You are half way there, keep up the good work";
	private static final String POMODORO_5MINUTES = "Just five more minutes, almost done";
	private static final String POMODORO_REST = "Well done, you can now rest for 5 minutes";
	private static final String POMODORO_DONE = "Resting time is over";
	
	public static final String ID = "pomodoro";

	public PomodoroModule(ModuleManager manager, Speech speech) {
		super(ID, manager, speech);
	}
	
	private final static int MINUTE = 1000 * 60;

	public void run() {
		while(true){
			speech.speak(POMODORO_START);
			
			try{
				Thread.sleep(MINUTE * 12);
			}catch(Exception e){
				
			}
			
			speech.speak(POMODORO_HALF);
			
			try{
				Thread.sleep(MINUTE * 8);
			}catch(Exception e){
				
			}
			
			speech.speak(POMODORO_5MINUTES);
			
			try{
				Thread.sleep(MINUTE * 5);
			}catch(Exception e){
				
			}
			
			speech.speak(POMODORO_REST);
			
			try{
				Thread.sleep(MINUTE * 5);
			}catch(Exception e){
				
			}
			
			speech.speak(POMODORO_DONE);
		}
	}

	@Override
	public void textReceived(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(String senderId, String message) {
		// TODO Auto-generated method stub
		
	}
}
