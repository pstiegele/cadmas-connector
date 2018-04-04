package com.telemetry;


import com.MAVLink.common.msg_heartbeat;

import tools.MessageMemory;

public class Heartbeat implements TelemetryMessage{
	int baseMode;
	long customMode;
	long timestamp;
	
	private static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat(msg_heartbeat message) {
		timestamp=System.currentTimeMillis();
		baseMode = message.base_mode;
		customMode = message.custom_mode;
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		return null;
	}

	public static MessageMemory<Heartbeat> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "heartbeat";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}


}
