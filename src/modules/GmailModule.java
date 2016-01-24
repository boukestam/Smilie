package modules;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import speech.Speech;

import com.google.api.services.gmail.Gmail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.lang.Thread;

public class GmailModule extends Module{
	
	public final static String NEW_EMAIL = "You have received a new email";
	
	public final static String ID = "gmail";
	
	
    /** Application name. */
    private static final String APPLICATION_NAME = "Smilie";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/smilie");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList(GmailScopes.GMAIL_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(new File("res/client_secret.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

	public GmailModule(ModuleManager manager, Speech speech) {
		super(ID, manager, speech);
	}
    
	public static List<Message> listMessagesWithLabels(Gmail service, String userId, List<String> labelIds)
			throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).setLabelIds(labelIds).execute();

		List<Message> messages = new ArrayList<Message>();
		messages.addAll(response.getMessages());

		return messages;
	}
	
	public static Message getMessage(Gmail service, String userId, String messageId) throws IOException {
		Message message = service.users().messages().get(userId, messageId).execute();

		return message;
	}
	
	boolean isActive = true;
	
	boolean previousIsActive = isActive;

	public void run() {
		try {
			// Build a new authorized API client service.
			Gmail service = getGmailService();

			// Print the labels in the user's account.
			String user = "me";
			
			List<String> labels = new ArrayList<String>();
			labels.add("UNREAD");
			labels.add("INBOX");
			
			List<Message> messages = listMessagesWithLabels(service, user, labels);
			List<String> handledMessages = new ArrayList<String>();
			
			for(Message m : messages){
				handledMessages.add(m.getId());
			}
			
			List<Message> unhandledMessages = new ArrayList<Message>();
			
			while(true){
				if(!previousIsActive && isActive){
					if(unhandledMessages.size() > 0){
						speech.speak("You received " + unhandledMessages.size() + " messages while you were away");
						
						int i = 1;
						for(Message m : unhandledMessages){
							speech.speak("Message number " + i);
							speech.speak(m.getSnippet(), "auto");
							
							handledMessages.add(m.getId());
							
							i++;
						}
						
						unhandledMessages.clear();
					}
				}
				
				List<Message> currentMessages = listMessagesWithLabels(service, user, labels);
				
				for(Message currentMessage : currentMessages){
					boolean found = false;
					
					for(String messageId: handledMessages){
						if(currentMessage.getId().matches(messageId)){
							found = true;
							break;
						}
					}
					
					if(!found){
						Message m = getMessage(service, user, currentMessage.getId());
						
						//String fullText = new String(Base64.decodeBase64(m.getPayload().getParts().get(0).getBody().getData()));
						
						if(m != null){
							if(isActive){
								speech.speak(NEW_EMAIL);
								speech.speak(m.getSnippet(), "auto");
								handledMessages.add(currentMessage.getId());
							}else{
								boolean unhandledFound = false;
								
								for(Message unhandledMessage : unhandledMessages){
									if(unhandledMessage.getId().matches(m.getId())){
										unhandledFound = true;
									}
								}
								
								if(!unhandledFound){
									unhandledMessages.add(m);
								}
							}
						}
					}
				}
				
				sendMessage("activity", "requestStatus");
				
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void textReceived(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(String senderId, String message) {
		if(senderId.matches(ActivityModule.ID)){
			if(message.matches(ActivityModule.ACTIVE)){
				previousIsActive = isActive;
				isActive = true;
			}else if(message.matches(ActivityModule.INACTIVE)){
				previousIsActive = isActive;
				isActive = false;
			}
		}
	}

}