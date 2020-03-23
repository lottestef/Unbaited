package Email;

public class EmailAddressInfo
{
	private String emailAddress;
	
	//To Check if email is BlackListed or WhiteListed
	private Boolean isBlackListedAddr = null;
	
	//Constructor
	public EmailAddressInfo()
	{
		
	}
	public EmailAddressInfo (String emailAddress)
    {
        this.emailAddress = emailAddress;
        this.isBlackListedAddr = false;
    }
	
	public String getemailAddress()
	{
		return emailAddress;
	}
	
	public Boolean getIsBlackListedAddr()
	{
		return isBlackListedAddr;
	}
	
	public void setIsBlackListedAddr(Boolean isBlackListedAddr)
	{
		this.isBlackListedAddr = isBlackListedAddr;
	}
}
