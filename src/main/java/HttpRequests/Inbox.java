package HttpRequests;
import com.google.api.services.gmail.model.Message;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Vector;

public class Inbox {

    private ArrayList<String> whiteList;
    private ArrayList<String> blackList;

    private ArrayList<JSONObject> safeMails;
    private ArrayList<JSONObject> phishingMails;

    public Inbox()
    {
        this.whiteList = new ArrayList<>();
        this.blackList = new ArrayList<>();
        this.safeMails = new ArrayList<> ();
        this.phishingMails = new ArrayList<> ();
    }

    // function that will take an email address and check if it is
    // contained in the whitelist

    public boolean inWhiteList (String address)
    {
        if (this.whiteList.contains(address))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    //function that will return a boolean if an email address is inside the blacklist
    public boolean inBlackList(String address) {
        if (this.blackList.contains(address)) {
            return true;
        } else
        {
            return false;
        }
    }

    // function that adds an email address to the blacklist
    public  void addToBlackList (String address)
    {
        // check first if the email address is in the whitelist.
        // if it is, then it will be remove from the whitelist first
        if (inWhiteList(address))
        {

            this.whiteList.remove(address);
        }

        this.blackList.add(address);

    }

    // function that adds the address to the whitelist
    public void addToWhiteList(String address)
    {
        // function that checks if the address is in the blacklist
        // if so , it will be removed from the blacklist.
        if (inBlackList(address))
        {
            this.blackList.remove(address);
        }

        this.whiteList.add(address);
    }



}
