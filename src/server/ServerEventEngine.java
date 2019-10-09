package server;

import utilities.events.ConnectionEvent;
import utilities.events.DisconnectionEvent;
import utilities.events.TextEvent;
import utilities.events.VoiceEvent;
import utilities.infra.Log;

public class ServerEventEngine extends EventEngine
{


// PROTECTED

	@Override
	protected boolean handle_connection(ConnectionEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_connection");
		return true;
	}

	@Override
	protected boolean handle_disconnection(DisconnectionEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_disconnection");
		return true;
	}

	@Override
	protected boolean handle_voice(VoiceEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_voice");
		return true;
	}

	@Override
	protected boolean handle_text(TextEvent event)
	{
		Log.LOG(Log.Level.DEBUG, "handle_text");
		return true;
	}
}