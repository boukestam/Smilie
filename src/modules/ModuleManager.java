package modules;

import java.util.ArrayList;

import main.Pair;

public class ModuleManager {

	private ArrayList<Module> modules;
	
	public ModuleManager(){
		modules = new ArrayList<Module>();
	}
	
	public void addModule(Module module){
		modules.add(module);
	}
	
	public void sendMessage(String senderId, String recipientId, String message){
		for(Module module : modules){
			if(module.getId().matches(recipientId)){
				module.receiveMessage(senderId, message);
			}
		}
	}
}
