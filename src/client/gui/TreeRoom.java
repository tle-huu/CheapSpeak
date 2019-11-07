package client.gui;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class TreeRoom extends JTree implements ThemeUI
{
	
// PUBLIC METHODS
	
	// Constructor
	public TreeRoom()
	{
		super();
		
		// Initialize the tree structure
		model_ = (DefaultTreeModel) this.getModel();
		root_ = new DefaultMutableTreeNode();
		
		// Disable the default double-click event
		this.setToggleClickCount(0);
		
		// Set theme UI
		setThemeUI();
		
		// Display the tree
		this.setRootVisible(false);
	}
	
	public void init(final List<Room> rooms)
	{
		for (Room room: rooms)
		{
			DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room);
			for (String client: room.clients())
			{
				DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode(client);
				roomNode.add(clientNode);
			}
			root_.add(roomNode);
		}
		model_.setRoot(root_);
		model_.reload();
		expand();
	}
	
	public void addRoom(final Room room)
	{
		DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room);
		root_.add(roomNode);
		model_.reload();
		expand();
	}
	
	public void removeRoom(final String room)
	{
		int roomCount = root_.getChildCount();
		for (int i = 0; i < roomCount; ++i)
		{
			DefaultMutableTreeNode roomNode = (DefaultMutableTreeNode) root_.getChildAt(i);
			if (roomNode.toString().equals(room))
			{
				root_.remove(roomNode);
				model_.reload();
				break;
			}
		}
		expand();
	}
	
	public void addClient(final String room, final String client)
	{
		int roomCount = root_.getChildCount();
		for (int i = 0; i < roomCount; ++i)
		{
			DefaultMutableTreeNode roomNode = (DefaultMutableTreeNode) root_.getChildAt(i);
			if (roomNode.toString().equals(room))
			{
				roomNode.add(new DefaultMutableTreeNode(client));
				model_.reload(roomNode);
				break;
			}
		}
		expand();
	}
	
	public void removeClient(final String room, final String client)
	{
		int roomCount = root_.getChildCount();
		for (int i = 0; i < roomCount; ++i)
		{
			DefaultMutableTreeNode roomNode = (DefaultMutableTreeNode) root_.getChildAt(i);
			if (roomNode.toString().equals(room))
			{
				int clientCount = roomNode.getChildCount();
				for (int j = 0; j < clientCount; ++j)
				{
					DefaultMutableTreeNode clientNode = (DefaultMutableTreeNode) roomNode.getChildAt(j);
					if (clientNode.toString().equals(client))
					{
						roomNode.remove(clientNode);
						model_.reload(roomNode);
						break;
					}
				}
				break;
			}
		}
		expand();
	}
	
	@Override
	public void setThemeUI()
	{
		// Set the color
		this.setBackground(UIManager.getColorResource("TREE_COLOR"));
		this.setForeground(UIManager.getColorResource("FONT_TREE_COLOR"));
	}
	
// PRIVATE METHOD
	
	private void expand()
	{
		for (int i = 0; i < this.getRowCount(); ++i)
		{
		    this.expandRow(i);
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private final DefaultTreeModel       model_;
	private final DefaultMutableTreeNode root_;
	
}
