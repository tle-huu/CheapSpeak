package client.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class PanelMain extends JSplitPane
{
	
// PUBLIC METHODS
	
	// Constructor
	public PanelMain(int width)
	{
		super();
		treeRoom_ = new TreeRoom();
		panelChat_ = new PanelChat();
		this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.setLeftComponent(new JScrollPane(treeRoom_));
		this.setRightComponent(panelChat_);
		this.setDividerLocation(width*20/100);
	}
	
	public TreeRoom tree()
	{
		return treeRoom_;
	}
	
	public PanelChat panelChat()
	{
		return panelChat_;
	}
	
// PRIVATE ATTRIBUTES
	
	private TreeRoom treeRoom_;
	private PanelChat panelChat_;
}