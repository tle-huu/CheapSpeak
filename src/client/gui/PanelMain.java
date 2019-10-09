package client.gui;

import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class PanelMain extends JSplitPane
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3080649116988544368L;
	
// PUBLIC METHODS
	
	// Constructor
	public PanelMain(int width, ActionListener sendListener)
	{
		super();
		treeRoom_ = new TreeRoom();
		panelChat_ = new PanelChat(sendListener);
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
	
	private TreeRoom  treeRoom_;
	private PanelChat panelChat_;
}