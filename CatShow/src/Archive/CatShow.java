package Archive;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CatShow
{
	private final String key = "b03c65671e67045256f2b0c83078e3c3";
	
	private ArrayList<String> possTags = new ArrayList<String>();
	private ArrayList<String> tags = new ArrayList<String>();
	private int numPerPage = 20;
	private int page = 1;
	private String size = "m";
	
	private String[][] info; //contains raw info for the image URLS
	private BufferedImage[][] list; //contains the images; each row is a buffer
	private int listIndex; //the current list that we're running through
	
	public CatShow()
	{
		setTags();
		URLSearcher searcher = new URLSearcher();
		info = searcher.search(key, tags, numPerPage, page);
		URLRetriever retriever = new URLRetriever();
		for (int i = 0; i < info.length; i++)
		{
			String imageURL = retriever.createImageURLString(info[i][0], info[i][1], info[i][2], info[i][3], size);
			System.out.println(imageURL);
		}
	}
	
	public void setTags()
	{
		tags.add("cat");
	}
	
	public void setPossibleTags()
	{
		
	}
	
	public void printInfo()
	{
		for (int i = 0; i < info.length; i++)
		{
			for (int j = 0; j < info[i].length; j++)
			{
				System.out.printf("%15s", info[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		CatShow cat = new CatShow();
	}
}
