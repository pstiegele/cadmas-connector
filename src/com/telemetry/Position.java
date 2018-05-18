package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_global_position_int;

import tools.MessageMemory;

public class Position implements TelemetryMessage{
	
	float latitude, longitude, altitudeAbsolute, altitudeRelative;
	long timestamp;

	private static MessageMemory<Position> messageMemory = new MessageMemory<>();
	public Position(msg_global_position_int message){
		timestamp=System.currentTimeMillis();
		latitude = message.lat;
		longitude = message.lon;
		altitudeAbsolute = (float)message.alt/1000;
		altitudeRelative = (float)message.relative_alt/1000;
		messageMemory.add(this);
	}
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("latitude", latitude).put("longitude", longitude).put("altitudeAbsolute",altitudeAbsolute).put("altitudeRelative",altitudeRelative);
		return res;
	}

	public static MessageMemory<Position> getMessageMemory() {
		return messageMemory;
	}
	
	@Override
	public String getSocketMethodName() {
		return "position";
	}
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public float getAltitudeAbsolute() {
		return altitudeAbsolute;
	}
	
	public float getAltitudeRelative() {
		return altitudeRelative;
	}

}
