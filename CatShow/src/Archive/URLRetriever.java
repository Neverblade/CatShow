package Archive;
/*
 * The URLRetriever takes in the specific parameters for an individual image, retrieves the URL,
 * and returns a usable Image for the program to use.
 */

public class URLRetriever
{
	public URLRetriever()
	{
		
	}
	
	public String createImageURLString(String farm, String server, String id, String secret, String size)
	{
		String s = "https://";
		s += "farm";
		s += farm;
		s += ".staticflickr.com/";
		s += server;
		s += "/";
		s += id;
		s += "_";
		s += secret;
		s += "_";
		s += size;
		s += ".jpg";
		return s;
	}
}
