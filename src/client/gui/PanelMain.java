package client.gui;

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
	public PanelMain()
	{
		super();
		treeRoom_ = new TreeRoom();
		panelChat_ = new PanelChat();
		this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.setLeftComponent(new JScrollPane(treeRoom_));
		this.setRightComponent(panelChat_);
	}
	
	public TreeRoom tree()
	{
		return treeRoom_;
	}
	
	public PanelChat panelChat()
	{
		return panelChat_;
	}
	
	public void closeChat()
	{
		panelChat_.setVisible(false);
		panelChat_.messagePanel().removeAll();
		panelChat_.sendTextArea().setText("");
		panelChat_.repaint();
	}
	
	public void openChat()
	{
		panelChat_.messagePanel().removeAll();
		panelChat_.sendTextArea().setText("");
		panelChat_.repaint();
		panelChat_.setVisible(true);
	}
	
// PRIVATE ATTRIBUTES
	
	private TreeRoom  treeRoom_;
	private PanelChat panelChat_;
}