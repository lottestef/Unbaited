package PhishTank;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PhishTankDriver {


    private static HttpURLConnection connection;
    private static String apikey = "9650e72637eb9f8af1ff88d14bc7dc00d9aa5cf7998e9fbcf0b191c18be373fb";
    private static String link = "https://data.phishtank.com/data/9650e72637eb9f8af1ff88d14bc7dc00d9aa5cf7998e9fbcf0b191c18be373fb/online-valid.json";
    public static JSONObject getDB() throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        JSONObject jsonPhishingItems = new JSONObject();
        // Method 1: java.net.HttpURLConnection
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();

            //Request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            System.out.println(status);
            // status needs to be 200 in order to know that there
            // has been no errors occurring

            if (status > 299) { // get error stream for data to troubleshoot
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
                // no connection issues, will use the connection as an input stream
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            //System.out.println(responseContent.toString());
           ArrayList<PhishItem> database =  parse(responseContent.toString());

             jsonPhishingItems = phishItemsAsJSON(database);

            //exportToFile("phishTankDB.json",jsonPhishingItems);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return jsonPhishingItems;
    }

    // parse the entire list of the response body as an
    // arraylist of phishItem objects .
    public static ArrayList<PhishItem> parse(String responseBody) {
        JSONArray database = new JSONArray(responseBody);
        ArrayList<PhishItem> phishItemDatabase = new ArrayList<>();

        for (int i = 0; i < database.length(); i++) {
            JSONObject data = database.getJSONObject(i);
            String url = data.getString("url");
            String verified = data.getString("verified");
            String target = data.getString("target");
            PhishItem information = new PhishItem(url, verified, target);
            phishItemDatabase.add(information);

           // System.out.println(url + "  " + verified);
        }
        return phishItemDatabase;
    }


    // convert phishitem to json object
    public static JSONObject phishItemToJson(PhishItem p) {
        JSONObject j = new JSONObject();
        j.put("url", p.geturl());
        j.put("verified", p.getverified());
        j.put("target", p.getTarget());
        return j;
    }


    public static JSONObject phishItemsAsJSON(ArrayList<PhishItem> database)
    {   JSONObject jso = new JSONObject();
        jso.put("Title: " , "Phishing Data");

        JSONArray array = new JSONArray();
        for (PhishItem p : database)
        {
            JSONObject j = phishItemToJson(p);
            array.put(j);
        }
        jso.put("Data", array);
        return jso;
    }
    //function that exports the entire phishtank database to a JSON file
    public static void exportToFile (String fileName, JSONObject itemsToExport)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            String itemsAsStr = gson.toJson(itemsToExport);
            FileWriter fw = new FileWriter(fileName);
            fw.write(itemsAsStr);
            fw.close();

        }catch (IOException e)
        {
            System.out.println("Failed to write to " + fileName + "!");
            e.printStackTrace();
        }

    }
    public static boolean inDatabase (String url, String database)
    {
        return database.contains(url);
    }


}
