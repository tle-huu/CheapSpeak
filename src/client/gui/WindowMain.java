package client.gui;

import java.awt.Component;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import client.stub.Client;
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

@SuppressWarnings("serial")
public class WindowMain extends JFrame implements EventEngine, ThemeUI
{
	
// PUBLIC METHODS
	
	// Constructor
	public WindowMain()
	{
		super();
		
		// Set default room
		defaultRoom_ = new Room(DEFAULT_ROOM_NAME);
		rooms_.add(defaultRoom_);
		
		// Set the window
		this.setTitle(WINDOW_NAME);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new ExitAdapter());
		this.setLocationRelativeTo(null);
		
		// Set the resize event
		this.addComponentListener(new ResizeEvent());
		
		// Set the menu bar
		initMenu();
		
		// Set the connect panel
		panelConnect_ = new PanelConnect();
		panelConnect_.connectButton().addActionListener(new ConnectListener());
		this.setContentPane(panelConnect_);
	}
	
	public void start()
	{
		this.setVisible(true);
		Log.LOG(Log.Level.INFO, "Start");
		eventListener();
	}
	
	@Override
	public boolean handleConnection(ConnectionEvent event)
	{
		Log.LOG(Log.Level.INFO, "Connection event received");
		
		// Get the username
		String pseudo = event.userName();
		
		// Add the new client
		addClient(DEFAULT_ROOM_NAME, pseudo);
		
		// Check if this is the current user
		if (pseudo_.equals(pseudo))
		{
			// Set the current room to default room
			currentRoom_ = DEFAULT_ROOM_NAME;
			
			// Start the audio processor
			//AUDIOPROCESSORaudioProcessor_.startMicrophoneThread();
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
		
		// Send the sound packet to the audio processor
		//AUDIOPROCESSORaudioProcessor_.playSoundPacket(event);
		
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
		if (!pseudo_.equals(pseudo))
		{
			panelMain_.panelChat().pushMessage(txt, pseudo, false);
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
		if (pseudo_.equals(pseudo))
		{
			// Update the current room
			currentRoom_ = roomName;
			panelMain_.resetChat();
			
			// Unmute the audio processor
			//AUDIOPROCESSORaudioProcessor_.unmute();
		}
		
		return true;
    }
	
	@Override
	public void setThemeUI()
	{
		// Set theme UI of panel connect
		if (panelConnect_ != null)
		{
			panelConnect_.setThemeUI();
		}
		
		// Set theme UI of panel main
		if (panelMain_ != null)
		{
			panelMain_.setThemeUI();
		}
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
		
		// Speaker menu
		menuBar_.mute().addActionListener(new MuteListener());
		
		// Appearance menu
		ThemeListener themeListener = new ThemeListener();
		Iterator<AbstractButton> buttonIterator = menuBar_.theme().getElements().asIterator();
		while (buttonIterator.hasNext())
		{
			buttonIterator.next().addActionListener(themeListener);
		}
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
			Event event = new DisconnectionEvent(null, pseudo_);
			client_.send_event(event);
			
			// End the connection
			client_.disconnect();
			
			// Reset the client
			client_ = null;
		}
		
		// Reset the rooms
		rooms_ = new ArrayList<Room>();
		defaultRoom_.clear();
		rooms_.add(defaultRoom_);
		
		// Stop listening to the server
		listening_.set(false);
	}
	
	private void exit()
	{
		// Disconnect from the server
		if (panelMain_ != null)
		{
			disconnect();
		}
		
		// Close the app
		Log.LOG(Log.Level.INFO, "Exit");
		System.exit(0);
	}
	
	private void addRoom(final String room)
	{
		// Add the room to the list
		Room newRoom = new Room(room);
		rooms_.add(newRoom);
		
		// Update the tree
		panelMain_.tree().addRoom(newRoom);
	}
	
	private void removeRoom(final String room)
	{
		// Update the room
		for (Room r: rooms_)
		{
			if (r.name().equals(room))
			{
				rooms_.remove(r);
				break;
			}
		}
		
		// Update the tree
		panelMain_.tree().removeRoom(room);
	}
	
	private void addClient(final String room, final String client)
	{
		// Update the room
		for (Room r: rooms_)
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
	
	private void removeClient(final String client)
	{
		// Update the room
		String room = null;
		for (Room r: rooms_)
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
	
	private void eventListener()
	{
		while (true)
		{
			// Wait until the user is connected to the server
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
			
			// Start listening to the server
			Log.LOG(Log.Level.INFO, "Start listening");
			listening_.set(true);
			while (listening_.get() && client_ != null)
			{
				// Get an event
				Event event = client_.getEvent();
				
				// Handle the event
				if (event != null)
				{
					boolean hasWorked = handleEvent(event);
					if (!hasWorked)
					{
						Log.LOG(Log.Level.ERROR, "This event (type " + event.type() + ") didn't work: " + event.uuid());
					}
				}
			}
			
			// Stop listening to the server
			listening_.set(false);
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
	        	// Get the panel chat
				PanelChat panelChat = panelMain_.panelChat();
				
				// Get the new width 
	        	int width = panelChat.getWidth();

	        	// Update each message
	        	for (PanelMessage message: panelChat.messages())
		        {
	        		// Update the size
	        		message.setWidth(width);	        		
		        }
	        }
	    }
	}
	
	class ConnectListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Get the connect variables
			String host = panelConnect_.host();
			int port = panelConnect_.port();
			String pseudo = panelConnect_.pseudo();
			
			// Check if the pseudo is correct
			if (pseudo.isEmpty() || pseudo.length() > PSEUDO_MAX_LENGTH)
			{
				Log.LOG(Log.Level.WARNING, "The pseudo " + pseudo + " is not supported");
				return ;
			}
			
			// Check if the port is correct
			if (port < MIN_PORT || port > MAX_PORT)
			{
				Log.LOG(Log.Level.WARNING, "The port " + port + " is not supported");
				return ;
			}
			Log.LOG(Log.Level.INFO, "Port used by the client: " + port);
			
			// Create a client and connect to the server
			boolean isConnected = NO_CONNECTION_MODE;
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
				pseudo_ = pseudo;
				
				// Switch to the main panel
				panelMain_ = new PanelMain();
				panelMain_.tree().init(rooms_);
				panelMain_.tree().addMouseListener(new RoomAdapter());
				panelMain_.tree().setCellRenderer(new TreeCellRenderer());
				panelMain_.panelChat().sendButton().addActionListener(new SendListener());
				setContentPane(panelMain_);
				revalidate();
				
				// Create the audio processor
				//AUDIOPROCESSORaudioProcessor_ = new AudioProcessor(client);
				
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
			// Break if the user is not in a room
			if (currentRoom_ == null)
			{
				return ;
			}
			
			// Get the chat panel
			PanelChat panelChat = panelMain_.panelChat();
			
			// Reset the focus on the text area
			panelChat.sendTextArea().grabFocus();
			
			// Get the text field and reset it
			String textMessage = panelChat.sendTextArea().getText().trim();
			panelChat.sendTextArea().setText("");
			
			// Push the message on the panel
			panelChat.pushMessage(textMessage, pseudo_, true);
			
			// Send the message to the server
			if (client_ != null)
			{
				Event event = new TextEvent(null, pseudo_, textMessage);
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
		        			
		        			// Mute the audio processor
		        			//AUDIOPROCESSORaudioProcessor_.mute();
		        			
		        			// Send the enter room event to the server
		        			if (client_ != null)
							{
								Event event = new EnterRoomEvent(null, pseudo_, roomName);
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
	
	class MuteListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Mute/unmute the microphone
			isMuted_ = menuBar_.mute().isSelected();
			if (isMuted_)
			{
				//AUDIOPROCESSORaudioProcessor_.mute();
			}
			else
			{
				//AUDIOPROCESSORaudioProcessor_.unmute();
			}
		}
	}
	
	class ThemeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Get the selected theme
			String theme = menuBar_.theme().getSelection().getActionCommand();
			
			// Update the current theme UI
			switch (theme)
			{
				case "Light":
					UIManager.setTheme(UIManager.Theme.LIGHT);
					break;
					
				case "Dark":
					UIManager.setTheme(UIManager.Theme.DARK);
					break;
				
				default:
					break;
			}
			setThemeUI();
		}
	}
	
	class FullscreenListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Switch to fullscreen mode
			if (!menuBar_.fullscreen().isSelected())
			{
				setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
			String shortcuts = "Connect:    ctrl+e\n"
							 + "Disconnect:    ctrl+d\n"
							 + "Exit:    ctrl+w\n"
							 + "Mute:    ctrl+m\n"
							 + "Fullscreen:    ctrl+f\n"
							 + "Contribute:    ctrl+b";
			
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
	
	class TreeCellRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean selected, boolean expanded, 
				boolean isLeaf, int row, boolean focused)
		{
			// Get the tree cell renderer component
			Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
			
			// Set the cell icon and font depending on whether the node is a room or a client
			int level = ((DefaultMutableTreeNode) value).getLevel();
			if (level == 1)
			{
				// Room node
				this.setIcon(UIManager.getIconResource("ROOM_ICON"));
				this.setFont(UIManager.getFontResource("FONT_TREE_ROOM"));
			}
			else
			{
				// Client node
				this.setIcon(UIManager.getIconResource("CLIENT_ICON"));
				this.setFont(UIManager.getFontResource("FONT_TREE_CLIENT"));
				
				// Highlight the cell if it's the user's pseudo
				String pseudo = (String) ((DefaultMutableTreeNode) value).getUserObject();
				if (pseudo.equals(pseudo_))
				{
					this.setForeground(UIManager.getColorResource("TREE_PSEUDO_COLOR"));
				}
			}
			
			// Set the cell background
			this.setBackgroundNonSelectionColor(UIManager.getColorResource("TREE_COLOR"));
			
			return component;
		}
	}

// PRIVATE ATTRIBUTES
	
	// Client
	private Client          client_;
	private final Lock      lock_ = new ReentrantLock();
	private final Condition cond_ = lock_.newCondition();
	private AtomicBoolean   listening_ = new AtomicBoolean(false);
	//AUDIOPROCESSORprivate AudioProcessor  audioProcessor_;
	
	// Window
	private MenuBar      menuBar_;
	private PanelConnect panelConnect_;
	private PanelMain    panelMain_;
	
	// State
	private List<Room> rooms_ = new ArrayList<Room>();
	private Room       defaultRoom_;
	private String     currentRoom_ = null;
	private String     pseudo_ = "default_";
	private boolean    isMuted_ = false;
	
	// Final
	private final int     DEFAULT_WIDTH = 1080;
	private final int     DEFAULT_HEIGHT = 720;
	private final String  WINDOW_NAME = "CheapSpeak";
	private final String  DEFAULT_ROOM_NAME = "Lobby";
	private final boolean NO_CONNECTION_MODE = true;
	private final int     PSEUDO_MAX_LENGTH = 32;
	private final int     MIN_PORT = 0;
	private final int     MAX_PORT = 65535;
	
}
