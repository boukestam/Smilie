package speech;

import java.util.ArrayList;

import main.Pair;

public class Speech extends Thread{
	
	/*
	 	[] 	= alleen dat teken
		* 	= alles
		()	= alles behalve
		| 	= of
		
		-	= mouth close
		)	= mouth half open
		o	= mouth open
	 */
	
	private final static String phonetics[][] = new String[][]{
		{"(p)ot", 		"*<p>*",			"-"},
		{"(t)am", 		"*<t>*",			")"},
		{"(k)ar", 		"(n)<k>*",			")"},
		{"(f)iets", 	"*<f>*",			"-"},
		{"(s)amen", 	"*<s>*",			")"},
		{"(ch)ef", 		"*<ch>[e]",			")"},
		{"(ch)aos", 	"*<ch>(e)",			")"},
		{"(b)ot", 		"*<b>*",			"-"},
		{"(d)am", 		"*<d>*",			"-"},
		{"(v)ier", 		"*<v>*",			"-"},
		{"(z)aad", 		"*<z>*",			"-"},
		{"(j)ournaal", 	"*<j>*",			"-"},
		{"(g)at", 		"*<g>*",			"-"},
		{"(h)and", 		"*(sc|s)<h>*",		"-"},
		{"(l)a", 		"*<l>*",			"-"},
		{"(r)ij", 		"*<r>*",			"-"},
		{"(m)aan", 		"*<m>*",			"-"},
		{"(n)a", 		"*<n>(g)",			"-"},
		{"sla(ng)", 	"*<ng>*",			"-"},
		{"(j)as", 		"*<j>*",			"-"},
		{"(w)ie", 		"*<w>*",			"-"},
		{"k(a)t", 		"(a)<a>(a|i|u)",	")"},
		{"p(e)t", 		"(e|o)<e>(e|i|u)",	")"},
		{"p(i)t", 		"(u|o|a)<i>(j)",	")"},
		{"(o)p", 		"*<o>(i|u)",		")"},
		{"p(u)t", 		"(a|o|e)<u>(i)",	")"},
		{"k(aa)s", 		"*<aa>(i)",			"o"},
		{"p(ee)s", 		"*<ee>*",			"o"},
		{"p(ie)t", 		"*<ie>*",			"o"},
		{"h(oo)p", 		"*<oo>(i)",			"o"},
		{"f(uu)t", 		"*<uu>*",			"o"},
		{"r(eu)s", 		"(e)<eu>*",			"o"},
		{"b(oe)k", 		"*<oe>(i)",			"o"},
		{"t(ij)d", 		"*<ij>*",			"o"},
		{"h(ui)s", 		"*<ui>*",			"o"},
		{"k(ou)d", 		"*<ou>*",			"o"},
		{"det(ail)", 	"*<ail>*",			"o"},
		{"h(oi)", 		"*<oi>*",			"o"},
		{"h(aai)", 		"*<aai>*",			"o"},
		{"l(eeuw)", 	"*<eeuw>*",			"o"},
		{"n(ieuw)", 	"*<ieuw>*",			"o"},
		{"m(ooi)", 		"*<ooi>*",			"o"},
		{"m(oei)lijk", 	"*<oei>*",			"o"}
	};
	
	private static final int BIGGEST_COMBINATION = 4;
	
	private ArrayList<Sound> sounds;
	
	private ArrayList<String> texts;
	
	private Smiley smiley;
	
	public Speech(Smiley smiley){
		sounds = new ArrayList<Sound>();
		texts = new ArrayList<String>();
		
		for(String[] phonetic : phonetics){
			sounds.add(new Sound("res/audio/voice8kmono/" + phonetic[0] + ".wav"));
		}
		
		if(sounds.size() != phonetics.length){
			System.err.println("Could not load all voice audio files");
		}else{
			System.out.println("Loaded all voice audio files");
		}
		
		this.smiley = smiley;
		
		this.start();
	}
	
	private boolean matchSurroundPattern(String surroundPattern, char c){
		char s = surroundPattern.charAt(0);
		
		String letters[] = surroundPattern.substring(1, surroundPattern.length() - 1).split("|");
		
		boolean mode = s == '(' ? true : false;
		
		for(String letter : letters){
			if((letter.charAt(0) == c) == mode){
				return false;
			}
		}
		
		return mode;
	}
	
	private boolean match(String pattern, String word, String part, int c){
		String letters = pattern.split("<")[1].split(">")[0];
		
		if(!letters.matches(part)){
			return false;
		}
		
		if(pattern.charAt(0) != '*' && c > 0){
			if(!matchSurroundPattern(pattern.split("<")[0], word.charAt(c - 1))){
				return false;
			}
		}
		
		if(pattern.charAt(pattern.length() - 1) != '*' && c < word.length() - 2){
			if(!matchSurroundPattern(pattern.split(">")[1], word.charAt(c + 1))){
				return false;
			}
		}
		
		return true;
	}
	
	private void speakWord(String word){
		int c = 0;
		
		Sound wordSound = null;
		
		ArrayList<Pair<Integer, String>> pronunciations = new ArrayList<Pair<Integer, String>>();
		
		while(c < word.length()){
			boolean found = false;
			
			outerloop:
			for(int n = Math.min(word.length() - c, BIGGEST_COMBINATION); n > 0; n--){
				String part = word.substring(c, c + n);
				
				for(int i = 0; i < phonetics.length; i++){
					String[] phonetic = phonetics[i];
					
					if(match(phonetic[1], word, part, c)){
						c += n;
						found = true;
						
						Sound sound = sounds.get(i);
						
						if(wordSound == null){
							wordSound = sound;
						}else{
							wordSound = Audio.combine(wordSound, sound);
						}
						
						pronunciations.add(new Pair<Integer, String>(sound.getMillisecondLength(), phonetic[2]));
						
						break outerloop;
					}
				}
			}
			
			if(!found){
				c++;
			}
		}
		
		if(wordSound != null){
			Audio.play(wordSound);
			
			for(Pair<Integer, String> pronunciation : pronunciations){
				switch(pronunciation.second){
				case "-":
					smiley.closeMouth();
					break;
				case ")":
					smiley.halfOpenMouth();
					break;
				case "o":
					smiley.openMouth();
					break;
				}
				
				try{
					Thread.sleep(pronunciation.first);
				}catch(Exception e){
					System.err.println("Error while sleeping speakWord()");
				}
			}
		}
		
		smiley.closeMouth();
	}
	
	private void speakText(String text){
		String words[] = text.split(" ");
		
		for(String word : words){
			speakWord(word);
			
			try{
				Thread.sleep(100);
			}catch(Exception e){
				System.err.println("Error while sleeping speakText()");
			}
		}
	}
	
	public void speak(String text){
		texts.add(text);
	}
	
	public void run(){
		while(true){
			if(texts.size() > 0){
				String text = texts.get(0);
				texts.remove(0);
				speakText(text);
			}
			
			try{
				Thread.sleep(10);
			}catch(Exception e){
				System.err.println("Error while sleeping run()");
			}
		}
	}
	
}
