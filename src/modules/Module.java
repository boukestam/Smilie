package modules;

import hearing.HearingListener;
import speech.Speech;

public abstract class Module implements Runnable, HearingListener{
	
	protected Speech speech;
	
	private ModuleManager manager;
	
	private String id;
	
	public Module(String id, ModuleManager manager, Speech speech){
		this.speech = speech;
		this.manager = manager;
		this.id = id;
		
		new Thread(this).start();
	}
	
	public String getId(){
		return id;
	}
	
	public void sendMessage(String recipientId, String message){
		manager.sendMessage(id, recipientId, message);
	}
	
	public abstract void receiveMessage(String senderId, String message);
}
