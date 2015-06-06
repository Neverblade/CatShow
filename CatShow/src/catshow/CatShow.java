package catshow;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList; 
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

public class CatShow
{
	private final int N = 15; //number of pics asked for per request
	private final int BUFFER_NUM = 2; //number of list buffers
	
	private ArrayList<ArrayList<BufferedImage>> lists = new ArrayList<ArrayList<BufferedImage>>();
	private ArrayList<Boolean> refreshList = new ArrayList<Boolean>();
	
	private CatShowFrame frame;
	private boolean paused = false;
	private boolean needForward = false;
	
	public CatShow()
	{
		//create the frame
		frame = new CatShowFrame(this);
		frame.setVisible(true);
		
		//init lists, load the first one
		for (int i = 0; i < BUFFER_NUM; i++)
		{
			lists.add(new ArrayList<BufferedImage>());
			refreshList.add(false);
		}
		loadImages(0);
		
		//start the graphics worker
		CatWorker worker = new CatWorker();
		worker.execute();
		
		//loop through the refreshlist - if it's false, we need to load it
		while (true)
		{
			for (int i = 0; i < refreshList.size(); i++)
			{
				if (!refreshList.get(i))
				{
					System.out.println("Refreshing list " + i);
					loadImages(i);
				}
			}
		}
	}
	
	public class CatWorker extends SwingWorker<Void, Void>
	{
		/*
		 * Looping process for the pictures:
		 * 1. While loop to go through pictures, wait 10 seconds in between each one.
		 * 2. Every time you go to the next buffer, reload the buffer you just left
		 */
		@Override
		protected Void doInBackground()
		{
			int currIndex = 0;
			while (true)
			{
				for (int i = 0; i < lists.get(currIndex).size(); i++)
				{
					frame.setImage(lists.get(currIndex).get(i));
					try
					{
						loop:
						//sleep, while simultaneously listening for pause/skip requests from the user
						for (int j = 0; j < 160; j++)
						{
							Thread.sleep(50);
							if (needForward)
							{
								needForward = false;
								break loop;
							}
							while (paused)
							{
								Thread.sleep(50);
								if (needForward)
								{
									needForward = false;
									break loop;
								}
							}
						}
					} catch (Exception e) { System.out.println("Sleeping..failed?"); }
				}
				System.out.println("Finished with " + currIndex + " and setting it to be refreshed.");
				refreshList.set(currIndex, false);
				currIndex = (currIndex + 1) % BUFFER_NUM;
			}			
		}
		
	}
	
	/*
	 *  Overhead process: searches for cat pictures from TheCatApi and turns them into resizable bufferedimages
	 *  They're then loaded into the corresponding list
	 */
	public void loadImages(int index)
	{
		String searchURL = createURLString();
		ArrayList<String> imageURLs = parseSearchURL(searchURL);
		convertURLToImage(imageURLs, index);
		refreshList.set(index, true);
		System.out.println("Loading of " + index + " buffer complete!");
	}
	
	/*
	 * Creates a URL that will grab us n number of random cat pictures frmo the internet
	 */
	public String createURLString()
	{
		System.out.println("Creating search URL string.");
		String s = "http://thecatapi.com/api/images/get?format=xml";
		s += "&results_per_page=";
		s += "" + N;
		return s;
	}
	
	/*
	 * Uses the search URL and parses through the resulting data to grab the individual image URLs
	 */
	public ArrayList<String> parseSearchURL(String searchURL)
	{
		//create the URL and retrieve the info string
		System.out.println("Creating URL...");
		URL infoURL = null;
		try
		{
			infoURL = new URL(searchURL);
		} catch (Exception e) { System.out.println("Creation of infoURL failed."); }
		System.out.println("Requesting info from server...");
		Scanner infoScanner = null;
		try
		{
			infoScanner = new Scanner(infoURL.openStream());
		} catch (Exception e) { System.out.println("Search info scanner creation failed."); }
		ArrayList<String> info = new ArrayList<String>();
		while (infoScanner.hasNextLine())
		{
			info.add(infoScanner.nextLine());
		}
		
		//parse through the info string, pick out the image links that aren't gifs
		System.out.println("Parsing through info...");
		ArrayList<String> urlStrings = new ArrayList<String>();
		for (int i = 0; i < info.size(); i++)
		{
			if (info.get(i).contains("<url>") && info.get(i).indexOf(".gif") == -1)
			{
				urlStrings.add(info.get(i).substring(13, info.get(i).length() - 6));
			}
		}
		//printList(urlStrings);
		return urlStrings;
	}
	
	/*
	 * Uses the image URLs and converts them to actual BufferedImages
	 */
	public void convertURLToImage(ArrayList<String> urlStrings, int index)
	{
		System.out.println("Converting URLs to images...");
		lists.get(index).clear();
		for (int i = 0; i < urlStrings.size(); i++)
		{
			//convert the string to url
			URL url = null;
			try
			{
				url = new URL(urlStrings.get(i));
			} catch (Exception e) { System.out.println("Failed to create image URL for : " + urlStrings.get(i)); }
			
			//convert url to image
			BufferedImage image = null;
			try {
				image = ImageIO.read(url);
			} catch (IOException e) { System.out.println("Image reading failed for: " + urlStrings.get(i)); }
			
			if (image != null) lists.get(index).add(image);
		}
	}
	
	/*
	 * Toggles the pause on and off.
	 */
	public void setPaused(boolean b)
	{
		paused = b;
	}
	
	/*
	 * Get the paused state.
	 */
	public boolean getPaused()
	{
		return this.paused;
	}
	
	/*
	 * Toggles the needForward on and off.
	 */
	public void setNeedForward(boolean b)
	{
		needForward = b;
	}
	
	/*
	 * Get whether or not we need to move forward a picture.
	 */
	public boolean getNeedForward()
	{
		return needForward;
	}
	
	/*
	 * Debugging: prints out the contents of an arraylist
	 */
	public void printList(ArrayList<String> sc)
	{
		for (int i = 0; i < sc.size(); i++)
		{
			System.out.println(sc.get(i));
		}
	}
	
	public static void main(String[] args)
	{
		CatShow show = new CatShow();
	}
}
