import java.io.Serializable;
import java.util.Date;  
import java.util.UUID;


/*
 * Wrapper around raw data to be sent over the network
 *
 */
public class Datagram implements Serializable
{

// PUBLIC
	public Datagram(final Object data)
	{
		timestamp_ = new Date();
		data_ = data;
	}

	public Datagram(final UUID client_uuid, final Object data)
	{
		timestamp_ = new Date();
		data_ = data;
		client_uuid_ = client_uuid;
	}


	public Datagram(final UUID client_uuid, final Date timestamp, final Object data)
	{
		timestamp_ = timestamp;
		data_ = data;
		client_uuid_ = client_uuid;
	}

	public UUID client_uuid()
	{
		return client_uuid_;
	}

	public Date timestamp()
	{
		return timestamp_;
	}

	public Object data()
	{
		return data_;
	}

	public void client_uuid(final UUID new_uuid)
	{
		client_uuid_ = new_uuid;
	}

// PRIVATE

	private 	  UUID 		client_uuid_;
	final private Date 		timestamp_;
	final private Object  	data_;

}