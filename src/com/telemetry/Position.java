package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_global_position_int;
import com.controller.autopilot.CustomMissionItem;
import com.controller.autopilot.MissionItemType;

import tools.MessageMemory;

public class Position implements TelemetryMessage{
	
	float latitude, longitude, heading, altitudeAbsolute, altitudeRelative;
	long timestamp;

	private static MessageMemory<Position> messageMemory = new MessageMemory<>();
	public Position() {
		latitude = longitude = heading = altitudeAbsolute = altitudeRelative = timestamp = 0;
		messageMemory.add(this);
	}
	public Position(msg_global_position_int message){
		timestamp=System.currentTimeMillis();
		latitude = (float) (message.lat/1e7);
		longitude = (float) (message.lon/1e7);
		heading = message.hdg;
		altitudeAbsolute = (float)message.alt/1000;
		altitudeRelative = (float)message.relative_alt/1000;
		messageMemory.add(this);
	}
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("latitude", latitude).put("longitude", longitude).put("altitudeAbsolute",altitudeAbsolute).put("altitudeRelative",altitudeRelative).put("heading", heading);
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
	
	public CustomMissionItem toCustomMissionItem() {
		return new CustomMissionItem(MissionItemType.LOITER_5_MIN, latitude, longitude, (int) altitudeRelative);
	}

}
