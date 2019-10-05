// Text message
public class HandshakeEvent extends Event
{

// PUBLIC

	public HandshakeEvent()
	{
		super(EventType.HANDSHAKE, null);
	}

	public void state(State state)
	{
		state_ = state;
	}

	public void user_name(final String name)
	{
		user_name_ = name;
	}

	public final State state()
	{
		return state_;
	}

	public final int magic_word()
	{
		return magic_word_;
	}

	public void magic_word(int word)
	{
		magic_word_ = word;
	}

// PRIVATE

	public enum State
	{
		WAITING,
		NAMESET,
		OTHERNAME,
		OK,
		BYE
	}


	private State state_ = State.WAITING;

	private String user_name_;

	private int magic_word_;
}