package client.gui;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class PanelMain extends JSplitPane implements ThemeUI
{
	
// PUBLIC METHODS
	
	// Constructor
	public PanelMain()
	{
		super();
		treeRoom_ = new TreeRoom();
		panelChat_ = new PanelChat();
		this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.setDividerLocation(DIVIDER_LOCATION);
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
	
	public void resetChat()
	{
		panelChat_.messagePanel().removeAll();
		panelChat_.clearMessages();
		panelChat_.sendTextArea().setText("");
		panelChat_.repaint();
	}
	
	@Override
	public void setThemeUI()
	{
		treeRoom_.setThemeUI();
		panelChat_.setThemeUI();
	}
	
// PRIVATE ATTRIBUTES
	
	private TreeRoom  treeRoom_;
	private PanelChat panelChat_;
	
	private final int DIVIDER_LOCATION = 220;
	
}