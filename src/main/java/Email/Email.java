package Email;

public class Email
{
	//Include whatever infomation needed like timestamp etc i not sure what else is needed
	private EmailAddressInfo senderAddressInfo;
	private String messageBody;

	//To State if email is phishing or not
    // if address isblackListed, then it is automatically blackListed.
	private Boolean markPhish = null;
	
	public Email()
	{
		
	}

	// public constructor.
    //email are not marked as phishing mail by default.
	public Email(EmailAddressInfo senderAddressInfo, String messageBody)
	{
		this.senderAddressInfo = senderAddressInfo;
		this.messageBody = messageBody;
		this.markPhish = false;
	}
	
	public String getSenderAddress()
	{
		return senderAddressInfo.getemailAddress();
	}
	
	public EmailAddressInfo getSenderAddressInfo()
	{
		return this.senderAddressInfo;
	}
	
	public String getmessageBody()
	{
		return messageBody;
	}
	
	public Boolean getMarkPhish()
	{
		return markPhish;
	}
	
	public void setMarkPhish(Boolean markPhish)
	{
		this.markPhish = markPhish;
	}
}
