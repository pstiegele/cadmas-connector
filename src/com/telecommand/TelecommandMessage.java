package com.telecommand;

import com.controller.autopilot.Autopilot;

public interface TelecommandMessage {

	
	public boolean execute(Autopilot autopilot);
}
