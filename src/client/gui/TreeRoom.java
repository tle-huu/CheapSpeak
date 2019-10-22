package client.gui;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreeRoom extends JTree
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4466274080800189880L;
	
// PUBLIC METHODS
	
	// Constructor
	public TreeRoom()
	{
		super();
		model_ = (DefaultTreeModel) this.getModel();
		root_ = new DefaultMutableTreeNode("Rooms");
		this.setRootVisible(false);
	}
	
	public void init(final List<Room> rooms)
	{
		for (int i = 0; i < rooms.size(); ++i)
		{
			Room room = rooms.get(i);
			DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room);
			List<String> clients = room.clients();
			for (int j = 0; j < clients.size(); ++j)
			{
				DefaultMutableTreeNode client = new DefaultMutableTreeNode(clients.get(j));
				roomNode.add(client);
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
	
// PRIVATE METHOD
	
	private void expand()
	{
		for (int i = 0; i < this.getRowCount(); ++i)
		{
		    this.expandRow(i);
		}
	}
	
// PRIVATE ATTRIBUTES
	
	private DefaultTreeModel       model_;
	private DefaultMutableTreeNode root_;
}
