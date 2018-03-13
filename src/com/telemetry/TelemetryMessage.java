package com.telemetry;

import tools.MessageMemory;

public abstract class TelemetryMessage {
	
	public abstract String getJSON();
	public abstract MessageMemory<?> getMessageMemory();

}
