package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_heartbeat;

import tools.MessageMemory;

public class Heartbeat extends TelemetryMessage{
	
	long timestamp = -1;
	msg_heartbeat mavHeartbeat=null;
	private static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat() {
		
	}
	
	public Heartbeat(MAVLinkPacket mavpacket) {
		mavHeartbeat = new msg_heartbeat(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("autopilot", mavHeartbeat.autopilot);
		return res.toString();
	}


	@Override
	public MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}


}
