package com.telemetry;

import tools.MessageMemory;
import com.MAVLink.Messages.MAVLinkMessage;

public interface TelemetryMessage{
	
	public abstract String getJSON();
}
