package client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.stub.Client;
import utilities.Event;
import utilities.infra.Log;

public class WindowMain extends JFrame
{
	
// PUBLIC METHODS
	
	// Constructor
	public WindowMain(List<Room> rooms)
	{
		super();
		
		// Set the rooms
		Rooms_ = rooms;
		
		// Set the window
		this.setTitle("Window Main");
		this.setSize(width_, height_);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		// Set the resize event
		this.addComponentListener(new ResizeEvent());
		
		// Set the menu bar
		initMenu();
		
		// Set the panel
		panelConnect_ = new PanelConnect(new ConnectListener());
		this.setContentPane(panelConnect_);
	}
	
	public static List<Room> getRooms()
	{
		return Rooms_;
	}
	
	public static String Pseudo()
	{
		return Pseudo_;
	}
	
	public void start()
	{
		this.setVisible(true);
		eventListener();
	}
	
// PRIVATE METHODS
	
	private void initMenu()
	{
		// Create the menu bar
		menuBar_ = new MenuBar();
		
		// Connection menu
		menuBar_.connect().addActionListener(new ConnectListener());
		menuBar_.disconnect().addActionListener(new DisconnectListener());
		menuBar_.exit().addActionListener(new ExitListener());
		
		// Appearance menu
		menuBar_.changePseudo().addActionListener(new ChangePseudoListener());
		ThemeListener tl = new ThemeListener();
		menuBar_.light().addActionListener(tl);
		menuBar_.dark().addActionListener(tl);
		menuBar_.fullscreen().addActionListener(new FullscreenListener());
		
		// Help menu
		menuBar_.shortcuts().addActionListener(new ShortcutsListener());
		menuBar_.contribute().addActionListener(new ContributeListener());
		
		// Add the bar to the frame
		this.setJMenuBar(menuBar_);
	}
	
	private void addRoom(String room)
	{
		// Add the room to the list
		Room r = new Room(room);
		Rooms_.add(r);
		// Update the tree
		panelMain_.tree().addRoom(r);
	}
	
	private void removeRoom(String room)
	{
		// Update the room
		for (Room r: Rooms_)
		{
			if (r.name().equals(room))
			{
				Rooms_.remove(r);
				break;
			}
		}
		// Update the tree
		panelMain_.tree().removeRoom(room);
	}
	
	private void addClient(String room, String client)
	{
		// Update the room
		for (Room r: Rooms_)
		{
			if (r.name().equals(room))
			{
				r.addClient(client);
				break;
			}
		}
		// Update the tree
		panelMain_.tree().addClient(room, client);
	}
	
	private void removeClient(String room, String client)
	{
		// Update the room
		for (Room r: Rooms_)
		{
			if (r.name().equals(room))
			{
				r.removeClient(client);
				break;
			}
		}
		// Update the tree
		panelMain_.tree().removeClient(room, client);
	}

	private void eventListener()
	{
		while (true)
		{
			lock_.lock();
			try
			{
				cond_.await();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			finally
			{
				lock_.unlock();
			}
			System.out.println("Start listening");
			Log.LOG(Log.Level.INFO, "Start listening");
			listening_ = true;
			while (listening_)
			{
				//Event event = client_.getEvent();
				//handleEvent(event);
				//Log.LOG(Log.Level.INFO, "Listening");
			}
			Log.LOG(Log.Level.INFO, "Stop listening");
		}
	}
	
	private void handleEvent(Event e)
	{
		
	}

// INNER CLASSES
	
	class ResizeEvent extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent componentEvent)
	    {
	        if (panelMain_ != null)
	        {
				JPanel messagePanel = panelMain_.panelChat().messagePanel();
				int width = Math.max((int) ((float) messagePanel.getWidth() * 0.75f), 117);
	        	for (Component pan: messagePanel.getComponents())
		        {
		    		int height = (int) pan.getPreferredSize().getHeight();
		    		pan.setMaximumSize(new Dimension(width, height));
		        }
	        }
	    }
	}
	
	class ConnectListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*String host = panelConnect_.host();
			int port = panelConnect_.port();
			// Create a client to connect to the server
			try
			{
				client_ = new Client(host, port);
			}
			catch (UnknownHostException e1)
	        {
	            e1.printStackTrace();
	        }
	        catch (IOException e1)
	        {
	        	e1.printStackTrace();
	        }
			String pseudo = panelConnect_.pseudo();
			Boolean isConnected = client_.connect(pseudo);
			System.out.println(isConnected);
			if (isConnected)*/
			{
				//Pseudo_ = pseudo;
				panelMain_ = new PanelMain(width_);
				setContentPane(panelMain_);
				revalidate();
				menuBar_.connect().setEnabled(false);
				menuBar_.disconnect().setEnabled(true);
				lock_.lock();
				cond_.signal();
				lock_.unlock();
			}
		}
	}
	
	class DisconnectListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
			listening_ = false;
			client_ = null;
			panelMain_ = null;
			setContentPane(panelConnect_);
			revalidate();
			menuBar_.connect().setEnabled(true);
			menuBar_.disconnect().setEnabled(false);
		}
	}
	
	class ExitListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
			System.exit(0);
		}
	}
	
	class ChangePseudoListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
		}
	}
	
	class ThemeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
		}
	}
	
	class FullscreenListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!menuBar_.fullscreen().isSelected())
			{
				setSize(width_, height_);
				setLocationRelativeTo(null);
			}
			else
			{
				setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		}
	}
	
	class ShortcutsListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
		}
	}
	
	class ContributeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// ...
		}
	}
	
// PRIVATE ATTRIBUTES
	
	// Event listener
	private final Lock lock_ = new ReentrantLock();
	private final Condition cond_ = lock_.newCondition();
	private boolean listening_ = false;
	
	// Window
	private int width_ = 1080;
	private int height_ = 720;
	private MenuBar menuBar_;
	private PanelConnect panelConnect_;
	private PanelMain panelMain_;
	
	// Client
	private Client client_;
	
	// State
	private static List<Room> Rooms_ = new ArrayList<Room>();
	private int activeRoomIndex_;
	private static String Pseudo_ = "_default_";
}
