package Archive;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * The URLSearcher takes in the criteria for your search, and returns an index with the
 * needed information to retrieve the image URL associated with the search.
 */

public class URLSearcher
{	
	public URLSearcher()
	{

	}
	
	public String[][] search(String key, ArrayList<String> tags, int numPerPage, int page)
	{
		String searchURL = getSearchURL(key, tags, numPerPage, page);
		URL infoURL = null;
		try
		{
			infoURL = new URL(searchURL);
		} catch (Exception e) { System.out.println("Search URL creation failed."); }
		Scanner infoScanner = null;
		try
		{
			infoScanner = new Scanner(infoURL.openStream());
		} catch (Exception e) { System.out.println("Search info scanner creation failed."); }
		return parseInfo(infoScanner, numPerPage);
	}
	
	public String getSearchURL(String key, ArrayList<String> tags, int numPerPage, int page)
	{
		String s = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
		s += "&api_key=";
		s += key;
		s += "&tags=";
		s += tags.get(0);
		for (int i = 1; i < tags.size(); i++)
		{
			s += "+" + tags.get(i);
		}
		s += "&per_page=";
		s += numPerPage;
		s += "&page=";
		s += page;
		s += "&format=json&jsoncallback=1";
		System.out.println(s);
		return s;
	}
	
	public String[][] parseInfo(Scanner sc, int numPerPage)
	{
		String[][] info = new String[numPerPage][4];
		String infoString = sc.nextLine();
		int startIndex = 0;
		while (infoString.charAt(startIndex) != '[') startIndex++;
		int endIndex = startIndex;
		while (infoString.charAt(endIndex) != ']') endIndex++;
		endIndex++;
		infoString = infoString.substring(startIndex, endIndex);
		//System.out.println(infoString);
		JSONArray jsonArray = null;
		try
		{
			jsonArray = new JSONArray(infoString);
		} catch (Exception e) { System.out.println("Creation of JSONArray failed."); }
		
		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject jsonObject = null;
			try
			{
				jsonObject = jsonArray.getJSONObject(i);
			} catch (Exception e) { System.out.println("Creation of the " + i + "th JSONObject failed."); }
			String[] infoNames = {"farm", "server", "id", "secret"};
			try
			{
				info[i][0] = "" + jsonObject.getInt(infoNames[0]);
			} catch (Exception e) { System.out.println("Parsing category " + infoNames[0] + " at the " + i + "th object failed."); }
			for (int j = 1; j < infoNames.length; j++)
			{
				try
				{
					info[i][j] = jsonObject.getString(infoNames[j]);
				} catch (Exception e) { System.out.println("Parsing category " + infoNames[j] + " at the " + i + "th object failed."); }
			}
		}
		return info;
	}
}
