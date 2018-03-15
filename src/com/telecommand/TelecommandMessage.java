package com.telecommand;

import com.MAVLink.Messages.MAVLinkMessage;
import com.controller.autopilot.Autopilot;

public interface TelecommandMessage {

	
	public MAVLinkMessage execute(Autopilot autopilot);
}
