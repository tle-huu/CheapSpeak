package client.gui;

import java.util.ArrayList;
import java.util.List;

public class Room
{
	
// PUBLIC METHODS
	
	// Constructor
	public Room(final String name)
	{
		name_ = name;
	}
	
	public String name()
	{
		return name_;
	}
	
	public List<String> clients()
	{
		return clients_;
	}
	
	public void addClient(final String client)
	{
		clients_.add(client);
	}
	
	public boolean removeClient(final String client)
	{
		return clients_.remove(client);
	}
	
	public void clear()
	{
		clients_.clear();
	}
	
	@Override
	public String toString()
	{
		return name_;
	}
	
// PRIVATE ATTRIBUTES

	private String name_;
	
	private List<String> clients_ = new ArrayList<String>();
	
}
