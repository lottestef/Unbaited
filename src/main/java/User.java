import Email.Email;
import Email.EmailAddressInfo;
import PhishTank.PhishTank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

    private ArrayList<Email> safeMail;
    private ArrayList<Email> phishMail;

    private ArrayList<EmailAddressInfo> whiteList;
    private ArrayList<EmailAddressInfo> blackList;
    private ArrayList<String> phishingDB;

    public User() {
        this.blackList = new ArrayList<>();
        this.whiteList = new ArrayList<>();
        this.safeMail = new ArrayList<>();
        this.phishMail = new ArrayList<>();
        try {
            this.phishingDB = PhishTank.PhishTankDb();

        } catch (Exception e) {
            this.phishingDB = new ArrayList<>();
        }
    }

    public ArrayList<EmailAddressInfo> getBlackList() {
        return blackList;
    }

    public ArrayList<EmailAddressInfo> getWhiteList() {
        return whiteList;
    }

    public ArrayList<Email> getPhishMail() {
        return phishMail;
    }

    public ArrayList<Email> getSafeMail() {
        return safeMail;
    }

    //Write to File
    public void writeToFile(String fileName, List<EmailAddressInfo> information) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (int i = 0; i < information.size(); i++) {
            writer.write(information.get(i).getemailAddress());
        }

        writer.close();
    }

    // function that will take an email address and check if it is
    // contained in the whitelist

    public boolean inWhiteList(String address) {
        for (EmailAddressInfo e : this.whiteList) {
            if (e.getemailAddress().equals(address))
                return true;
        }
        return false;
    }

    public boolean inBlackList(String address) {
        for (EmailAddressInfo e : this.blackList) {
            if (e.getemailAddress().equals(address)) {
                return true;
            }

        }
        return false;
    }


    // function that adds an email address to the blacklist
    public void addToBlackList(String address) {
        // check first if the email address is in the whitelist.
        // if it is, then it will be remove from the whitelist first
        if (inWhiteList(address)) {

            removeFromWhiteList(address);
        }

        if (!inBlackList(address))
        {
            EmailAddressInfo toAdd = new EmailAddressInfo(address);
            toAdd.setIsBlackListedAddr(true);
            this.blackList.add(toAdd);
        }

    }

    // function that adds the address to the whitelist
    public void addToWhiteList(String address) {
        // function that checks if the address is in the blacklist
        // if so , it will be removed from the blacklist.
        if (inBlackList(address)) {
            removeFromBlackList(address);
        }
        if (!inWhiteList(address))
        {
            EmailAddressInfo toAdd = new EmailAddressInfo(address);
            toAdd.setIsBlackListedAddr(false);
            this.whiteList.add(toAdd);
        }
    }

    public void removeFromWhiteList(String address) {
        this.whiteList.removeIf(e -> e.getemailAddress().equals(address));
    }

    public void removeFromBlackList(String address) {
        this.blackList.removeIf(e -> e.getemailAddress().equals(address));
    }

    public void setWhiteList(ArrayList<EmailAddressInfo> whiteList) {
        this.whiteList = whiteList;
    }

    public void setBlackList(ArrayList<EmailAddressInfo> blackList)
    {
        this.blackList = blackList;
    }

    public void setSafeMail (ArrayList<Email> safeMail)
    {
        this.safeMail = safeMail;
    }

    public void setPhishMail (ArrayList<Email> phishMail)
    {
        this.phishMail = phishMail;
    }




}
