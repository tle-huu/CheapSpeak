package client.gui;

import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar
{
	
// PUBLIC METHODS

	// Constructor
	public MenuBar()
	{
		// Connection menu
		connect_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
		connection_.add(connect_);
		disconnect_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		disconnect_.setEnabled(false);
		connection_.add(disconnect_);
		connection_.addSeparator();
		exit_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
		connection_.add(exit_);
		
		// Speaker menu
		mute_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
		speaker_.add(mute_);
		speaker_.add(volume_);
		
		// Appearance menu
		light_.setActionCommand(light_.getText());
		dark_.setActionCommand(dark_.getText());
		themeButtonGroup_.add(light_);
		themeButtonGroup_.add(dark_);
		theme_.add(light_);    
		theme_.add(dark_);
		setDefaultTheme();
		appearance_.add(theme_);
		fullscreen_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
		appearance_.add(fullscreen_);
		
		// Help menu
		help_.add(shortcuts_);
		contribute_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK));
		help_.add(contribute_);
		
		// Set mnemonics
		connection_.setMnemonic('C');
		speaker_.setMnemonic('S');
		appearance_.setMnemonic('A');
		help_.setMnemonic('H');

		// Add menus in the bar
		this.add(connection_);
		this.add(speaker_);
		this.add(appearance_);
		this.add(help_);
	}
	
	public JMenuItem connect()
	{
		return connect_;
	}

	public JMenuItem disconnect()
	{
		return disconnect_;
	}

	public JMenuItem exit()
	{
		return exit_;
	}

	public JMenuItem volume()
	{
		return volume_;
	}

	public JMenuItem shortcuts()
	{
		return shortcuts_;
	}

	public JMenuItem contribute()
	{
		return contribute_;
	}
	
	public ButtonGroup theme()
	{
		return themeButtonGroup_;
	}
	
	public JCheckBoxMenuItem mute()
	{
		return mute_;
	}

	public JCheckBoxMenuItem fullscreen()
	{
		return fullscreen_;
	}
	
// PRIVATE METHOD
	
	private void setDefaultTheme()
	{
		switch (UIManager.DEFAULT_THEME)
		{
			case LIGHT:
				light_.setSelected(true);
				break;
				
			case DARK:
				dark_.setSelected(true);
				break;
				
			default:
				break;
		}
	}
	
// PRIVATE ATTRIBUTES
		
	private JMenu connection_ = new JMenu("Connection"),
				  speaker_ = new JMenu("Speaker"),
				  appearance_ = new JMenu("Appearance"),
				  theme_ = new JMenu("Theme"),
				  help_ = new JMenu("Help");
	
	private JMenuItem connect_ = new JMenuItem("Connect"),
					  disconnect_ = new JMenuItem("Disconnect"),
					  exit_ = new JMenuItem("Exit"),
					  volume_ = new JMenuItem("Volume"),
					  shortcuts_ = new JMenuItem("Shortcuts"),
					  contribute_ = new JMenuItem("Contribute");
	
	private JRadioButtonMenuItem light_ = new JRadioButtonMenuItem("Light"),
								 dark_ = new JRadioButtonMenuItem("Dark");
	
	private ButtonGroup themeButtonGroup_ = new ButtonGroup();
	
	private JCheckBoxMenuItem mute_ = new JCheckBoxMenuItem("Mute"),
							  fullscreen_ = new JCheckBoxMenuItem("Fullscreen");
	
}
