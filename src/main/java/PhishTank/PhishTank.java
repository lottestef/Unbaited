package PhishTank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class PhishTank 
{
	private static HttpURLConnection connection;
	private static String apikey = "9650e72637eb9f8af1ff88d14bc7dc00d9aa5cf7998e9fbcf0b191c18be373fb";
	
    public static ArrayList<String> PhishTankDb() throws Exception
    {
    	BufferedReader reader;
    	String line;
    	StringBuffer responseContent = new StringBuffer();
    	
    	// Method 1: java.net.HttpURLConnection
    	try
    	{
    		URL url = new URL("https://data.phishtank.com/data/9650e72637eb9f8af1ff88d14bc7dc00d9aa5cf7998e9fbcf0b191c18be373fb/online-valid.json");
    		connection = (HttpURLConnection) url.openConnection();
    		
    		//Request setup
    		connection.setRequestMethod("GET");
    		connection.setConnectTimeout(5000);
    		connection.setReadTimeout(5000);
    		
    		int status = connection.getResponseCode();

    		
    		if(status > 299)
    		{   System.out.println(status);
    			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
    			
    			while((line = reader.readLine()) != null)
    			{
    				responseContent.append(line);
    			}
    			reader.close();
    		}
    		
    		else
    		{
    			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    			
    			while((line = reader.readLine()) != null)
    			{
    				responseContent.append(line);
    			}
    			reader.close();
    		}
    		
    		//System.out.println(responseContent.toString());
        	ArrayList<String> PhishTankDataBase = parse(responseContent.toString());
        	return PhishTankDataBase;
    	}
    	catch (MalformedURLException e)
    	{
    		e.printStackTrace();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		connection.disconnect();
    	}
    	
    	return null;
    }
    
    public static ArrayList<String> parse(String responseBody)
    {
    	JSONArray database = new JSONArray(responseBody);
    	ArrayList<String> PhishTankDataBase = new ArrayList<>();
    	
    	for(int i = 0; i < database.length(); i++)
    	{
    		JSONObject data = database.getJSONObject(i);
    		String url = data.getString("url");
    		PhishTankDataBase.add(url);
    			
    		//System.out.println(url);
    	}
		return PhishTankDataBase;  	
    }
}
