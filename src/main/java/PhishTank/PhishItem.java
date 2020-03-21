package PhishTank;

public class PhishItem {
    private String url;
    private String verified;
    private String target;
    public PhishItem(String url, String verified,String target)
    {
        this.url = url;
        this.verified = verified;
        this.target = target;
    }

    public String geturl()
    {
        return url;
    }

    public String getverified()
    {
        return verified;
    }
    public String getTarget() {return target;};
}
