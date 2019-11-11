package utilities.events;

/*
 * The HandShakeEvent is a shell servers and clients to initiate the communication
 *
 */
@SuppressWarnings("serial")
public class HandshakeEvent extends Event
{
	
// PUBLIC ENUM

	public enum State
	{
		WAITING,
		NAMESET,
		OTHERNAME,
		OK,
		LISTENING,
		BYE;
	};
	
// PUBLIC METHODS

	// Constructor
	public HandshakeEvent()
	{
		super(EventType.HANDSHAKE, null);
	}

	public void state(final State state)
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

// PRIVATE ATTRIBUTES
	
	private State state_ = State.WAITING;

	private String userName_;

	private int magicWord_;
	
}
