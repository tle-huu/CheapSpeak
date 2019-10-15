package utilities.events;

import java.util.UUID;

// A client changes his pseudo
public class ChangePseudoEvent extends Event
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2663035097965771371L;
	
// PUBLIC METHODS
	
	// Constructor
	public ChangePseudoEvent(UUID uuid, String oldPseudo, String newPseudo)
	{
		super(EventType.CHANGE_PSEUDO, uuid);
		oldPseudo_ = oldPseudo;
		newPseudo_ = newPseudo;
	}
	
	public String oldPseudo()
	{
		return oldPseudo_;
	}
	
	public String newPseudo()
	{
		return newPseudo_;
	}
	
// PRIVATE ATTRIBUTES
	
	private String oldPseudo_;
	private String newPseudo_;

}
