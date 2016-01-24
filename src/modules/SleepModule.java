package modules;

import java.util.Calendar;

import speech.Speech;

public class SleepModule extends Module{
	
	private final static String SLEEP_MESSAGE = "You should probably go sleep now, it is getting late";
	private final static String MORNING_MESSAGE = "Goodmorning, did you sleep well tonight? Do you have any ideas on how to improve me further?";
	
	public final static String ID = "sleep";

	public SleepModule(ModuleManager manager, Speech speech) {
		super(ID, manager, speech);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		boolean saidSleepMessageToday = false;
		boolean saidMorningMessageToday = false;
		
		while(true){
			Calendar calendar = Calendar.getInstance();
			
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			
			if(hours == 17 && minutes == 44){
				if(!saidSleepMessageToday){
					speech.speak(SLEEP_MESSAGE);
					saidSleepMessageToday = true;
				}
			}else{
				saidSleepMessageToday = false;
			}
			
			if(hours == 7 && minutes == 0 && !saidMorningMessageToday){
				if(!saidMorningMessageToday){
					speech.speak(MORNING_MESSAGE);
					saidMorningMessageToday = true;
				}
			}else{
				saidMorningMessageToday = false;
			}
			
			try{
				Thread.sleep(1000 * 10);
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
		// TODO Auto-generated method stub
		
	}

}
