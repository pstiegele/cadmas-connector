package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_altitude;

import tools.MessageMemory;

public class Altitude implements TelemetryMessage{
	float monotonicAlt, relativeAlt, absoluteAlt;
	long timestamp;
	
	private static MessageMemory<Altitude> messageMemory = new MessageMemory<>();
	
	public Altitude(msg_altitude message) {
		timestamp=System.currentTimeMillis();
		monotonicAlt = message.altitude_monotonic;
		relativeAlt = message.altitude_relative;
		absoluteAlt = message.altitude_amsl;
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("monotonicAlt", monotonicAlt).put("relativeAlt", relativeAlt).put("absoluteAlt", absoluteAlt);
		return res;
	}
	
	public static MessageMemory<Altitude> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "Altitude";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public float getMonotonicAlt() {
		return monotonicAlt;
	}
	
	public float getRelativeAlt() {
		return relativeAlt;
	}
	
	public float getAbsoluteAlt() {
		return absoluteAlt;
	}

}
