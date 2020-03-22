package HttpRequests;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import groovy.json.internal.IO;
import org.json.JSONObject;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebRequests {
    private static final String APPLICATION_NAME = "Gmail WebRequests";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = WebRequests.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        // the client secrets are parsed as an inputstreamreader, then it forms the
        // GoogleClientSecrets class object
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    // function that will return a Gmail instance to the caller
    public static Gmail getServiceExternally() {
        Gmail service = null;

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return service;
    }


    public static Gmail getService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
        /*
        String user = "me";

        ListMessagesResponse m = service.users().messages().list(user).execute();

        List<Message> messages = m.getMessages();
        Vector<String> messageIds = new Vector<>();

        for (Message msg : messages) {
            messageIds.add(msg.getId());

        }*/
    }

    // function that will return the entire encoded string
    // from the Message.getRaw() function.
    public static String getRawString(Gmail service, String userId, String messageId) throws IOException {
        Message msg = service.users().messages().get(userId, messageId).setFormat("raw").setAlt("json").execute();

        return StringUtils.newStringUtf8(Base64.decodeBase64(msg.getRaw()));

    }

    // function that transforms the message into a mimeMessage,
    // used in order to carry out processing
    // and extract the body
    public static MimeMessage getMimeMessage(Message message) throws MessagingException {
        byte[] emailBytes = Base64.decodeBase64(message.getRaw());
        Properties properties = new Properties();
        Session s = Session.getDefaultInstance(properties, null);

        return new MimeMessage(s, new ByteArrayInputStream(emailBytes));
    }

    // function that returns the
    // text from the message (body of email)
    public static String getTextFromMessage(MimeMessage message) throws IOException, MessagingException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    //function that will obtain the text
    // from a mime-multipart message
    // called by the 'getTextFromMessage()' function
    public static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws IOException, MessagingException {

        int numParts = mimeMultipart.getCount();
        // multipart mime message required to have body parts
        if (numParts == 0)
            throw new MessagingException("Multipart with no body parts not supported.");

        boolean multipartAlt = new ContentType(mimeMultipart.getContentType()).match("multipart/alternative");
        if (multipartAlt)

            return getTextFromBodyPart(mimeMultipart.getBodyPart(numParts - 1));
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numParts; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            result.append(getTextFromBodyPart(bodyPart));
        }
        return result.toString();
    }

    public static String getTextFromBodyPart(
            BodyPart bodyPart) throws IOException, MessagingException {

        String result = "";
        if (bodyPart.isMimeType("text/plain")) {
            result = (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            result = org.jsoup.Jsoup.parse(html).text();
        } else if (bodyPart.getContent() instanceof MimeMultipart) {
            result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return result;
    }

    //method that converts a json-formatted string to a json object
    public static String convertToJSONString(String str) {
        JSONObject jsonObj = new JSONObject(str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObj);
    }

    //method that converts a message to a json object
    public static String convertToJSONString(Message message) {
        String messageStr = message.toString();
        JSONObject jsonObj = new JSONObject(messageStr);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(jsonObj);
    }

    public static JSONObject convertToJSON(Message message) {
        String messageStr = message.toString();
        return new JSONObject(messageStr);
    }

    public static JSONObject convertToJSON(String str) {
        return new JSONObject(str);
    }

    //method that will return a list of all the messages.
    public static List<Message> getMessagesFromInbox(Gmail service, String userId) throws IOException {
        ListMessagesResponse lmr = service.users().messages().list(userId).execute();
        return lmr.getMessages();
    }

    // method that will return a vector containing the Ids of all the messages in the user's inbox.
    public static Vector<String> getIds(List<Message> messages) {
        Vector<String> toReturn = new Vector<>();
        for (Message m : messages) {
            toReturn.add(m.getId());
        }
        return toReturn;
    }


    // gets a message , fully processes it and the
    // 'raw' component will have all the text inside it
    public static JSONObject processMessageFully(Gmail service, String userId, String messageId) throws IOException {
        Message m = service.users().messages().get(userId, messageId).setFormat("raw").execute();
        JSONObject json = convertToJSON(m);
        MimeMessage mime = null;

        String messageText = "";
        try {
            mime = getMimeMessage(m);
            messageText = getTextFromMessage(mime);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        json.put("raw", messageText);
        return json;
    }

    // function that will return the
    // user's entire inbox as a vector of json objects, for easy search
    //
    public static Vector<JSONObject> fullInboxAsJSON(Gmail service, String userId) {
        Vector<JSONObject> toReturn = new Vector<>();

        try {
            // get all the messages from the inbox
            List<Message> messages = getMessagesFromInbox(service, userId);
            Vector<String> Ids = getIds(messages);
            // get the fully processed JSONobject for each message in the inbox
            for (String s : Ids) {
                JSONObject toAdd = processMessageFully(service, userId, s);
                toReturn.add(toAdd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return all the messages in the user's inbox as vector of json objects
        return toReturn;
    }

    // will check if the mail body has a URL, and if it does, return the URL contained within the body, else
    // return the "No Url Found" message;
    public static String hasURL(JSONObject mail) {
        Pattern p = Pattern.compile("(https?://(www.)?(\\w+)(\\.\\w+))");
        String mailBody = mail.getString("body");
        Matcher matcher = p.matcher(mailBody);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        else
        {
            return "No Url Found";
        }
    }


}