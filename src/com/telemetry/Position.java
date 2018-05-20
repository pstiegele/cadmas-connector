package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_global_position_int;

import tools.MessageMemory;

public class Position implements TelemetryMessage{
	
	float latitude, longitude, altitude;
	long timestamp;

	private static MessageMemory<Position> messageMemory = new MessageMemory<>();
	public Position(msg_global_position_int message){
		timestamp=System.currentTimeMillis();
		latitude = message.lat;
		longitude = message.lon;
		altitude = message.alt;
		messageMemory.add(this);
	}
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("latitude", latitude/10000000).put("longitude", longitude/10000000).put("altitude",altitude/1000);
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

}
