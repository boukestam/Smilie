package speech;

import java.io.InputStream;
import java.util.ArrayList;

import com.darkprograms.speech.synthesiser.Synthesiser;

import javazoom.jl.player.Player;
import main.Pair;

public class Speech implements Runnable{
	
	private ArrayList<Pair<String, String>> texts;
	
	private Smiley smiley;
	
	private static final String DEFAULT_LANGUAGE = "en-gb";
	
	private static final int WORDS_PER_REQUEST = 30;
	
	Synthesiser synth;
	
	public Speech(Smiley smiley){
		texts = new ArrayList<Pair<String, String>>();
		
		synth = new Synthesiser(DEFAULT_LANGUAGE);
		
		this.smiley = smiley;
		
		new Thread(this).start();
	}
	
	private void speakText(String text, String language){
		try {
			synth.setLanguage(language);
			
		    InputStream is = synth.getMP3Data(text);
		    Player mp3Player = new Player(is);
		    mp3Player.play();
		    
			synth.setLanguage(DEFAULT_LANGUAGE);
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    System.out.println("Error");
		    e.printStackTrace();
		    return;
		}
	}
	
	public void speak(String text){
		texts.add(new Pair<String, String>(text, DEFAULT_LANGUAGE));
	}
	
	public void speak(String text, String language){
		texts.add(new Pair<String, String>(text, language));
	}
	
	public void run(){
		while(true){
			if(texts.size() > 0){
				Pair<String, String> pair = texts.get(0);
				String text = pair.first;
				texts.remove(0);
				
				String[] words = text.split(" ");
				if(words.length > WORDS_PER_REQUEST){
					text = "";
					
					String remaining = "";
					
					for(int i = 0; i < words.length; i++){
						if(i < WORDS_PER_REQUEST){
							text += words[i];
						}else{
							remaining += words[i] + " ";
						}
					}
					
					texts.add(0, new Pair<String, String>(remaining, pair.second));
				}
				
				speakText(text, pair.second);
			}
			
			try{
				Thread.sleep(10);
			}catch(Exception e){
				System.err.println("Error while sleeping run()");
			}
		}
	}
	
}
