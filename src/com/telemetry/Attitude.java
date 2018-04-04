package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_attitude;

import tools.MessageMemory;

public class Attitude implements TelemetryMessage{
	
	float pitch, roll, heading;
	long timestamp;
	
	private static MessageMemory<Attitude> messageMemory = new MessageMemory<>();
	public Attitude(msg_attitude message) {
		timestamp=System.currentTimeMillis();
		pitch = message.pitch;
		roll = message.roll;
		heading = message.yaw;
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("pitch", pitch).put("roll", roll).put("heading", heading);
		return res.toString();
	}


	public static MessageMemory<Attitude> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "attitude";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}


}
