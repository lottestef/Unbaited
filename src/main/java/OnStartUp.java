import Email.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import HttpRequests.WebRequests;
import PhishTank.PhishTank;
import com.google.api.services.gmail.Gmail;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class OnStartUp 
{
	public static void main(String[] args) throws Exception
    {
		/* Request User Credentials */ // Can be a function
        //gets a 'Gmail' object which will be used to obtain the mails
        //from the gmail api.
        Gmail gmail = WebRequests.getServiceExternally("me");

		User UserObj = new User();

		// pull the email inbox as json for convenience
        Vector<JSONObject> JSInbox = WebRequests.fullInboxAsJSON(gmail,"me");

		//arraylist of email objects created first
		ArrayList<Email> inbox = new ArrayList<>();
        String sender = "";
        String body = "";

        //process the emails
        //create the email objects for the various email
        for (JSONObject j : JSInbox)
        {
            body = j.getString("raw");
            sender = j.getString("from");
            if (hasAngleBrackets(sender))
            {
                sender = removeBrackets(sender);
            }
            EmailAddressInfo info = new EmailAddressInfo(sender);
            Email toAdd = new Email(info,body);
            inbox.add(toAdd);
        }
		/* Check if local file exist */  // IF YES don't run below  // Can be a function
		
        /* Get PhishTankDatabase */
        ArrayList<String> phishTankDataBase = PhishTank.PhishTankDb();
        ArrayList<Email> safeMail = new ArrayList<>();
        ArrayList<Email> phishMail = new ArrayList<>();
        ArrayList<EmailAddressInfo> whiteList = new ArrayList<>();
        ArrayList<EmailAddressInfo> blackList = new ArrayList<>();

        for (int i = 0; i < inbox.size();i++)
        {
            String emailBody = inbox.get(i).getmessageBody();
            if (!(hasURL(emailBody).equals("NoUrl")))
            {
                String testUrl = hasURL(emailBody);

                if (inbox.get(i).getSenderAddressInfo().getIsBlackListedAddr())
                {
                    phishMail.add(inbox.get(i));
                }
                else if (phishTankDataBase.contains(testUrl))
                {
                    phishMail.add(inbox.get(i));
                    inbox.get(i).getSenderAddressInfo().setIsBlackListedAddr(true);
                    blackList.add(inbox.get(i).getSenderAddressInfo());

                }
                else
                {
                    safeMail.add(inbox.get(i));
                    whiteList.add(inbox.get(i).getSenderAddressInfo());
                }
            }
            else
            {
                if (inbox.get(i).getSenderAddressInfo().getIsBlackListedAddr())
                {
                    phishMail.add(inbox.get(i));
                }
                else
                {
                    safeMail.add(inbox.get(i));
                    whiteList.add(inbox.get(i).getSenderAddressInfo());
                }
            }




        }



        UserObj.setSafeMail(safeMail);
        UserObj.setPhishMail(phishMail);
        UserObj.setWhiteList(whiteList);
        UserObj.setBlackList(blackList);

		/* Allow User to manually add WhiteList */ // Should be a function
					
		/* Display Inbox */
        System.out.println("Safe mail");

        for (Email e : safeMail)
        {   System.out.println("------------------------------------");
            System.out.println("From : " +e.getSenderAddress());
            System.out.println("Body: " + e.getmessageBody());
        }
        System.out.println("Phish mail");
        System.out.println("------------------------------------");
        for (Email e : phishMail)
        {
            System.out.println("From : " +e.getSenderAddress());
            System.out.println("Body: " + e.getmessageBody());
            System.out.println("------------------------------------");
        }
		/* Display UI Stuff */
    }

    // use to check if the 'from' header has angle brackets.
    // for example " From: Google <noreply@google.com>"
    // we only want the 'noreply@google.com'
    public static boolean hasAngleBrackets (String fromHeader) {
        Pattern p = Pattern.compile("<.*>");
        Matcher m = p.matcher(fromHeader);
        if (m.find())
        {
            return true;

        }
        else
        {
            return false;
        }
    }

    public static String removeBrackets(String s)
    {
        return StringUtils.substringBetween(s,"<",">");

    }

    public static String hasURL(String mailBody) {
        Pattern p = Pattern.compile("(https?://(www.)?(\\w+)(\\.\\w+))");

        Matcher matcher = p.matcher(mailBody);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        else
        {
            return "NoUrl";
        }
    }

}
