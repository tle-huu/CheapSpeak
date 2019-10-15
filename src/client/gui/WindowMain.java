package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import client.stub.Client;
import utilities.events.ChangePseudoEvent;
import utilities.events.ConnectionEvent;
import utilities.events.DisconnectionEvent;
import utilities.events.Event;
import utilities.events.EventEngine;
import utilities.events.NewRoomEvent;
import utilities.events.RemoveRoomEvent;
import utilities.events.TextEvent;
import utilities.events.VoiceEvent;
import utilities.events.EnterRoomEvent;
import utilities.infra.Log;

public class WindowMain extends JFrame implements EventEngine
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3724351605438242811L;
	
// PUBLIC METHODS
	
	// Constructor
	public WindowMain()
	{
		super();
		
		// Set default room
		defaultRoom_ = new Room("Lobby");
		Rooms_.add(defaultRoom_);
		
		// Set the window
		this.setTitle("Window Main");
		this.setSize(width_, height_);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new ExitAdapter());
		this.setLocationRelativeTo(null);
		
		// Set the resize event
		this.addComponentListener(new ResizeEvent());
		
		// Set the menu bar
		initMenu();
		
		// Set the panel
		panelConnect_ = new PanelConnect();
		panelConnect_.connectButton().addActionListener(new ConnectListener());
		this.setContentPane(panelConnect_);
	}
	
	public static List<Room> rooms()
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
	
	@Override
	public boolean handleConnection(ConnectionEvent event)
	{
		Log.LOG(Log.Level.INFO, "Connection event received");
		
		// Get the username
		String pseudo = event.userName();
		
		// Add the new client
		addClient(defaultRoom_.name(), pseudo);
		
		// Set the current room to default room if this is the user
		if (Pseudo_.equals(pseudo))
		{
			currentRoom_ = defaultRoom_.name();
		}
		
		return true;
	}

	@Override
	public boolean handleDisconnection(DisconnectionEvent event)
	{
		Log.LOG(Log.Level.INFO, "Disconnection event received");
		
		// Get the room and the username
		String pseudo = event.userName();
		
		// Remove the client
		removeClient(pseudo);
		
		return true;
	}
	
	@Override
	public boolean handleNewRoom(NewRoomEvent event)
	{
		Log.LOG(Log.Level.INFO, "New room event received");
		
		// Get the room name
		String roomName = event.roomName();
		
		// Add the room
		addRoom(roomName);
		
		return true;
	}

	@Override
	public boolean handleRemoveRoom(RemoveRoomEvent event)
	{
		Log.LOG(Log.Level.INFO, "Remove room event received");
		
		// Get the room name
		String roomName = event.roomName();
		
		// Remove the room
		removeRoom(roomName);
		
		// Reset the main panel if the user is inside
		if (currentRoom_.equals(roomName))
		{
			currentRoom_ = null;
			panelMain_.resetChat();
		}
		
		return true;
	}

	@Override
	public boolean handleVoice(VoiceEvent event)
	{
		Log.LOG(Log.Level.INFO, "Voice event received");
		
		return true;
	}
	
	@Override
	public boolean handleText(TextEvent event)
	{
		Log.LOG(Log.Level.INFO, "Text event received");
		
		// Get the username and the message
		String pseudo = event.userName();
		String txt = event.textPacket();
		
		// Push the message on the panel if it's not the user's message
		if (!Pseudo_.equals(pseudo))
		{
			panelMain_.panelChat().pushMessage(txt, pseudo);
		}
		
		return true;
	}
	
	@Override
	public boolean handleEnterRoom(EnterRoomEvent event)
    {
		Log.LOG(Log.Level.INFO, "Enter room event received");
		
		// Get the room and the username
		String roomName = event.roomName();
		String pseudo = event.userName();
		
		// Remove the client from the previous room
		removeClient(pseudo);
		
		// Add the client in the new room
		addClient(roomName, pseudo);
		
		// Update the room if needed
		if (Pseudo_.equals(pseudo))
		{
			currentRoom_ = roomName;
			panelMain_.resetChat();
		}
		
		return true;
    }
	
	@Override
	public boolean handleChangePseudo(ChangePseudoEvent event)
    {
		Log.LOG(Log.Level.INFO, "Change pseudo event received");
		
		// Get the old and new pseudo
		String oldPseudo = event.oldPseudo();
		String newPseudo = event.newPseudo();
		
		// Change the current pseudo if needed
		if (Pseudo_.equals(oldPseudo))
		{
			Pseudo_ = newPseudo;
		}
		
		// Change the username in the tree
		changeClient(oldPseudo, newPseudo);
		
		return true;
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
	
	private void disconnect()
	{
		Log.LOG(Log.Level.INFO, "Disconnected");
		
		// Send the disconnection message to the server
		if (client_ != null)
		{
			// Send the disconnection message to the server
			Event event = new DisconnectionEvent(null, Pseudo_);
			client_.send_event(event);
			
			// End the connection
			client_.disconnect();
			
			// Reset the client
			client_ = null;
		}
		
		// Reset the rooms
		Rooms_ = new ArrayList<Room>();
		defaultRoom_.clear();
		Rooms_.add(defaultRoom_);
		
		// Stop listening
		listening_ = false;
	}
	
	private void exit()
	{
		// Disconnect from the server
		if (panelMain_ != null)
		{
			disconnect();
		}
		
		// End the app
		Log.LOG(Log.Level.INFO, "Exit");
		System.exit(0);
	}
	
	private void addRoom(String room)
	{
		// Add the room to the list
		Room newRoom = new Room(room);
		Rooms_.add(newRoom);
		
		// Update the tree
		panelMain_.tree().addRoom(newRoom);
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
	
	private void removeClient(String client)
	{
		// Update the room
		String room = null;
		for (Room r: Rooms_)
		{
			boolean isRemoved = r.removeClient(client);
			if (isRemoved)
			{
				room = r.name();
				break;
			}
		}
		
		// Update the tree
		if (room != null)
		{
			panelMain_.tree().removeClient(room, client);
		}
	}
	
	private void changeClient(String oldClient, String newClient)
	{
		// Update the room
		String room = null;
		for (Room r: Rooms_)
		{
			boolean isRemoved = r.removeClient(oldClient);
			if (isRemoved)
			{
				room = r.name();
				break;
			}
		}
		
		// Update the tree
		if (room != null)
		{
			panelMain_.tree().removeClient(room, oldClient);
			panelMain_.tree().addClient(room, newClient);
		}
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
			Log.LOG(Log.Level.INFO, "Start listening");
			listening_ = true;
			while (listening_ && client_ != null)
			{
				Event event = client_.getEvent();
				if (event != null)
				{
					boolean hasWorked = handleEvent(event);
					if (!hasWorked)
					{
						Log.LOG(Log.Level.ERROR, "This event (type " + event.type() + ") didn't work: " + event.uuid());
					}
				}
			}
			listening_ = false;
			Log.LOG(Log.Level.INFO, "Stop listening");
		}
	}

// INNER CLASSES
	
	class ResizeEvent extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent componentEvent)
	    {
	        // Set the size of the message boxes
			if (panelMain_ != null)
	        {
	        	PanelChat panelChat = panelMain_.panelChat();
				JPanel messagePanel = panelChat.messagePanel();
	        	int width = panelChat.getWidth();
				int padding = (int) ((float) width * 0.25f);
				Color backgroundColor = panelChat.backgroundColor();
				Color otherMessageColor = panelChat.otherMessageColor();
	        	for (Component pan: messagePanel.getComponents())
		        {
	        		Color panColor = pan.getBackground();
	        		if (panColor.equals(otherMessageColor))
	        		{
	        			((JPanel) pan).setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10 + padding, backgroundColor));
	        		}
	        		else
	        		{
	        			((JPanel) pan).setBorder(BorderFactory.createMatteBorder(10, 10 + padding, 10, 10, backgroundColor));
	        		}
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
			// Get host, port and pseudo
			String host = panelConnect_.host();
			int port = panelConnect_.port();
			String pseudo = panelConnect_.pseudo();
			
			// Check if the port is correct
			if (port < 0 || port >= 65536)
			{
				Log.LOG(Log.Level.WARNING, "The port " + port + " is not supported");
				return ;
			}
			
			// Create a client and connect to the server
			boolean isConnected = noConnectionMode_;
			try
			{
				client_ = new Client(host, port);
				isConnected = client_.connect(pseudo);
			}
			catch (UnknownHostException e1)
	        {
	            Log.LOG(Log.Level.WARNING, "The host " + host + " is unknown");
	        }
	        catch (IOException e1)
	        {
	        	e1.printStackTrace();
	        }
			
			// Set the main panel
			if (isConnected)
			{
				// Set the pseudo
				Pseudo_ = pseudo;
				
				// Switch to the main panel
				panelMain_ = new PanelMain();
				panelMain_.setDividerLocation(width_ * 20 / 100);
				panelMain_.tree().addMouseListener(new RoomAdapter());
				panelMain_.panelChat().sendButton().addActionListener(new SendListener());
				setContentPane(panelMain_);
				revalidate();
				
				// Reset the connect/disconnect buttons
				menuBar_.connect().setEnabled(false);
				menuBar_.disconnect().setEnabled(true);
				
				// Start listening to the server
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
			// Disconnect from the server
			disconnect();
			
			// Switch to the connection panel
			panelMain_ = null;
			setContentPane(panelConnect_);
			revalidate();
			
			// Reset the connect/disconnect buttons
			menuBar_.connect().setEnabled(true);
			menuBar_.disconnect().setEnabled(false);
		}
	}
	
	class SendListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Get the chat panel
			PanelChat panelChat = panelMain_.panelChat();
			
			// Reset the focus on the text area
			panelChat.sendTextArea().grabFocus();
			
			// Get the text field and reset it
			String txt = panelChat.sendTextArea().getText();
			panelChat.sendTextArea().setText("");
			
			// Break if the user is not a room
			if (currentRoom_ == null)
			{
				return ;
			}
			
			// Push the message on the panel
			panelChat.pushMessage(txt, Pseudo_);
			
			// Send the message to the server
			if (client_ != null)
			{
				Event event = new TextEvent(null, Pseudo_, txt);
				client_.send_event(event);
			}
		}
	}
	
	class RoomAdapter extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
	    {
			if (e.getClickCount() == 1)
			{
				// Get the tree
				TreeRoom tree = panelMain_.tree();
				
				// Check if the user clicked on a node
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		        if (path != null)
		        {
		        	// Check if the node is a room
		        	if (path.getPathCount() == 2)
		        	{
			        	// Get the room name
		        		String roomName = path.getLastPathComponent().toString();
		        		
		        		// Check if the user was already in this room
		        		if (currentRoom_ == null || !currentRoom_.equals(roomName))
		        		{	
		        			// Reset the main panel
		        			currentRoom_ = null;
		        			panelMain_.resetChat();
		        			
		        			// Send the enter room event to the server
		        			if (client_ != null)
							{
								Event event = new EnterRoomEvent(null, Pseudo_, roomName);
								client_.send_event(event);
							}
		        		}
		        	}
		        }
	        }
	    }
	}
	
	class ExitAdapter extends WindowAdapter
	{
        @Override
        public void windowClosing(WindowEvent e)
        {
            exit();
        }
    }
	
	class ExitListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			exit();
		}
	}
	
	class ChangePseudoListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Break if not in the main panel
			if (panelMain_ == null)
			{
				// Inform the client he cannot change his pseudo on this page
				JOptionPane.showMessageDialog(null, 
											  "You cannot change your pseudo on this page !", 
											  "Information", 
											  JOptionPane.INFORMATION_MESSAGE);
				
				// Break
				return ;
			}
			
			// Create an input window to get the new pseudo
			String newPseudo = JOptionPane.showInputDialog(null, 
														   "Enter a new pseudo", 
														   "Change your pseudo", 
														   JOptionPane.QUESTION_MESSAGE);
			
			// Send the change pseudo event to the server
        	if (client_ != null)
			{
				Event event = new ChangePseudoEvent(null, Pseudo_, newPseudo);
				client_.send_event(event);
			}
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
			// Set the shortcuts list
			String shortcuts = "Connect:        ctrl+e\n"
							 + "Disconnect:     ctrl+d\n"
							 + "Exit:           ctrl+w\n"
							 + "Change pseudo:  ctrl+p\n"
							 + "Change theme:   ctrl+t\n"
							 + "Fullscreen:     ctrl+f\n"
							 + "Contribute:     ctrl+b";
			
			// Display an information message
			JOptionPane.showMessageDialog(null, 
										  shortcuts, 
										  "Shortcuts", 
										  JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	class ContributeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Display an information message
			JOptionPane.showMessageDialog(null, 
										  "Do not hesitate to give a tip :)", 
										  "Be generous !", 
										  JOptionPane.INFORMATION_MESSAGE);
		}
	}

// PRIVATE ATTRIBUTES
	
	// Event listener
	private final   Lock lock_ = new ReentrantLock();
	private final   Condition cond_ = lock_.newCondition();
	private boolean listening_ = false;
	
	// Window
	private int          width_ = 1080;
	private int          height_ = 720;
	private MenuBar      menuBar_;
	private PanelConnect panelConnect_;
	private PanelMain    panelMain_;
	
	// Client
	private Client client_;
	
	// State
	private static List<Room> Rooms_ = new ArrayList<Room>();
	private Room              defaultRoom_;
	private String            currentRoom_ = null;
	private static String     Pseudo_ = "default_";
	private boolean           noConnectionMode_ = true;
}
