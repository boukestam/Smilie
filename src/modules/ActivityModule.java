package modules;

import java.awt.MouseInfo;
import java.awt.Point;

import speech.Speech;

public class ActivityModule extends Module{
	
	private final static String WELCOME_BACK = "Welcome back";
	
	private final static int TIME_BEFORE_UNACTIVE = 1000 * 60 * 5;
	
	public final static String REQUEST_STATUS = "requestStatus";
	public final static String ACTIVE = "active";
	public final static String INACTIVE = "inactive";
	
	public final static String ID = "activity";

	public ActivityModule(ModuleManager manager, Speech speech) {
		super(ID, manager, speech);
	}
	
	private boolean isActive = true;

	public void run() {
		Point lastMousePosition = MouseInfo.getPointerInfo().getLocation();
		long lastActivity = System.currentTimeMillis();
		
		while(true){
			long now = System.currentTimeMillis();
			
			if(now - lastActivity >= TIME_BEFORE_UNACTIVE){
				isActive = false;
			}
			
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			if(!mousePosition.equals(lastMousePosition)){
				if(now - lastActivity >= TIME_BEFORE_UNACTIVE){
					speech.speak(WELCOME_BACK);
				}
				
				lastMousePosition = mousePosition;
				lastActivity = now;
				
				isActive = true;
			}
			
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				
			}
		}
	}

	@Override
	public void textReceived(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(String senderId, String message) {
		if(message.matches(REQUEST_STATUS)){
			String answer = isActive ? ACTIVE : INACTIVE;
			sendMessage(senderId, answer);
		}
	}

	
}
