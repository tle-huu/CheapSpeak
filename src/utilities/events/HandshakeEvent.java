package utilities.events;

// Text message
public class HandshakeEvent extends Event
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1809464133655976084L;
	
// PUBLIC METHODS

	// Constructor
	public HandshakeEvent()
	{
		super(EventType.HANDSHAKE, null);
	}

	public void state(State state)
	{
		state_ = state;
	}

	public void userName(final String name)
	{
		userName_ = name;
	}

	public void magicWord(final int word)
	{
		magicWord_ = word;
	}

	public String userName()
	{
		return userName_;
	}

	public final State state()
	{
		return state_;
	}

	public final int magicWord()
	{
		return magicWord_;
	}


	public enum State
	{
		WAITING,
		NAMESET,
		OTHERNAME,
		OK,
		LISTENING,
		BYE;
	}

// PRIVATE ATTRIBUTES
	
	private State state_ = State.WAITING;

	private String userName_;

	private int magicWord_;
}