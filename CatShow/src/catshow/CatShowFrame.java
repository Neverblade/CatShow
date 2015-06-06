package catshow;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CatShowFrame extends JFrame
{
	private JPanel contentPane;
	private JLabel imageLabel;
	private JLabel forwardLabel;
	private JLabel psLabel;
	
	private BufferedImage image;
	
	private ImageIcon playIcon;
	private ImageIcon stopIcon;
	private ImageIcon forwardIcon;
	
	private CatShow show;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmSaveImage;
	
	/**
	 * Create the frame.
	 */
	public CatShowFrame(final CatShow show)
	{		
		this.show = show;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        setLocation(((int) screenSize.getWidth() - getWidth())/2, ((int) screenSize.getHeight() - getHeight())/2- 30);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmSaveImage = new JMenuItem("Save Image");
		
		mntmSaveImage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				show.setPaused(true);
				psLabel.setIcon(playIcon);
				saveImage();
			}
		});
		
		mnFile.add(mntmSaveImage);
		contentPane = new JPanel();
		contentPane.setMinimumSize(new Dimension(2, 30));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		imageLabel = new JLabel("Loading...");
		imageLabel.setFont(new Font("Courier New", Font.BOLD, 40));
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_imageLabel = new GridBagConstraints();
		gbc_imageLabel.gridwidth = 2;
		gbc_imageLabel.weighty = 1.0;
		gbc_imageLabel.weightx = 1.0;
		gbc_imageLabel.fill = GridBagConstraints.BOTH;
		gbc_imageLabel.insets = new Insets(5, 5, 5, 0);
		gbc_imageLabel.gridx = 0;
		gbc_imageLabel.gridy = 0;
		contentPane.add(imageLabel, gbc_imageLabel);
		
		psLabel = new JLabel("");
		
		psLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				pause();
			}
		});
		
		psLabel.setBounds(0, 0, 40, 40);
		psLabel.setPreferredSize(new Dimension(40, 40));
		psLabel.setMinimumSize(new Dimension(40, 40));
		psLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_psLabel = new GridBagConstraints();
		gbc_psLabel.anchor = GridBagConstraints.EAST;
		gbc_psLabel.weightx = 0.5;
		gbc_psLabel.insets = new Insets(5, 5, 5, 5);
		gbc_psLabel.gridx = 0;
		gbc_psLabel.gridy = 1;
		contentPane.add(psLabel, gbc_psLabel);
		//System.out.println("psLabel Width: " + psLabel.getWidth() + " psLabel Height: " + psLabel.getHeight());
		
		forwardLabel = new JLabel("");
		
		forwardLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				skip();
			}
		});
		
		forwardLabel.setBounds(0, 0, 40, 40);
		forwardLabel.setPreferredSize(new Dimension(40, 40));
		forwardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		forwardLabel.setMinimumSize(new Dimension(40, 40));
		GridBagConstraints gbc_forwardLabel = new GridBagConstraints();
		gbc_forwardLabel.weightx = 0.5;
		gbc_forwardLabel.anchor = GridBagConstraints.WEST;
		gbc_forwardLabel.gridx = 1;
		gbc_forwardLabel.gridy = 1;
		gbc_forwardLabel.insets = new Insets(5, 5, 5, 5);
		contentPane.add(forwardLabel, gbc_forwardLabel);
		//System.out.println("forwardLabel Width: " + forwardLabel.getWidth() + " forwardLabel Height: " + forwardLabel.getHeight());
		
		loadRes();
		psLabel.setIcon(stopIcon);
		forwardLabel.setIcon(forwardIcon);

		addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
            	loadImage();
            }
        });
	}
	
	/*
	 * Set the image.
	 */
	public void setImage(BufferedImage image)
	{
		this.image = image;
		loadImage();
		imageLabel.setText("");
	}
	
	/*
	 * Given a BufferedImage, resize and load it into the imageLabel
	 */
	public void loadImage()
	{
		try
		{
			BufferedImage resizedImage = resizeImage(image);
			imageLabel.setIcon(new ImageIcon(resizedImage));
		} catch (Exception e) { System.out.println("Null image, didn't load image."); }
		
	}
	
	
	/*
	 * Resize the image while keeping the aspect ratio the same
	 */
	public BufferedImage resizeImage(BufferedImage image)
	{
		//debugging checks
		if (image == null)
		{
			System.out.println("Image you're trying to resize is null...");
			return null;
		} else if (imageLabel == null)
		{
			System.out.println("ImageLabel is null during resizing.");
			return null;
		}
		
		//determine the required bounds of the image
		int width;
		int height;
		int xOffset;
		int yOffset;
		
		if (1.0*image.getWidth()/image.getHeight() > 1.0*imageLabel.getWidth()/imageLabel.getHeight()) //use width as constraint
		{
			width = imageLabel.getWidth();
			xOffset = 0;
			height = (int) Math.round(1.0 * image.getHeight() * imageLabel.getWidth() / image.getWidth());
			yOffset = (imageLabel.getHeight() - height) / 2;
		} else //use height as constraint
		{
			width = (int) Math.round(1.0 * image.getWidth() * imageLabel.getHeight() / image.getHeight());
			xOffset = (imageLabel.getWidth() - width) / 2;
			height = imageLabel.getHeight();
			yOffset = 0;
		}
		BufferedImage newImage = new BufferedImage(imageLabel.getWidth(), imageLabel.getHeight(), image.getType());
		Graphics g = newImage.createGraphics();
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, 0, imageLabel.getWidth(), imageLabel.getHeight());
		g.drawImage(image, xOffset, yOffset, width, height, null);
		g.dispose();
		return newImage;
	}
	
	/*
	 * Loads and resizes the play, stop, and forward button for easy access.
	 * Precondition: psLabel and forwardLabel must be initialized.
	 */
	public void loadRes()
	{
		//retrieve the images from the res folder
		BufferedImage playImage = null;
		BufferedImage stopImage = null;
		BufferedImage forwardImage = null;
		try
		{
			playImage = ImageIO.read(getClass().getResource("/res/play.png"));
			stopImage = ImageIO.read(getClass().getResource("/res/stop.png"));
			forwardImage = ImageIO.read(getClass().getResource("/res/forward.png"));
		} catch (Exception e) { System.out.println("Resource image retrieving failed."); }
		
		//resize the images into label sizes
		playImage = resourceResize(playImage, psLabel.getWidth(), psLabel.getHeight());
		stopImage = resourceResize(stopImage, psLabel.getWidth(), psLabel.getHeight());
		forwardImage = resourceResize(forwardImage, forwardLabel.getWidth(), forwardLabel.getHeight());
		
		//load the resized images into icon format
		playIcon = new ImageIcon(playImage);
		stopIcon = new ImageIcon(stopImage);
		forwardIcon = new ImageIcon(forwardImage);
	}
	
	/*
	 * Helper method for resizing resource images.
	 */
	public BufferedImage resourceResize(BufferedImage image, int width, int height)
	{
		BufferedImage newImage = new BufferedImage(width, height, image.getType());
		Graphics g = newImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return newImage;
	}
	
	/*
	 * Pauses the slideshow.
	 */
	public void pause()
	{
		show.setPaused(!show.getPaused());
		if (show.getPaused()) psLabel.setIcon(playIcon);
		else psLabel.setIcon(stopIcon);
	}
	
	/*
	 * Skips to the next image.
	 */
	public void skip()
	{
		show.setNeedForward(true);
	}	
	
	/*
	 * Saves the current image to a chosen directory.
	 */
	public void saveImage()
	{
		JFileChooser fc = new JFileChooser();
		int val = fc.showSaveDialog((JFrame) this);
		if (val == JFileChooser.APPROVE_OPTION)
		{
			//choose initial path and name
			File file = fc.getSelectedFile();
			String fileName = file.getAbsolutePath();
			
			//remove the possible extension
			String extension = "";
			if (fileName.indexOf(".") != -1) //user input their own extension, use that
			{
				extension = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
			} else //user didn't put their own extension, use jpg
			{
				extension = "jpg";
				fileName += ".jpg";
			}
						
			System.out.println("Saving to: " + fileName);
			try
			{
				ImageIO.write(image, extension, new File(fileName));
			} catch (IOException e) { System.out.println("Failed to save image..."); }
			System.out.println("Saving complete!");
		}
	}
}