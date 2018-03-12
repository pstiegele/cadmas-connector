package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_heartbeat;

public class Heartbeat extends TelemetryMessage{
	
	long timestamp = -1;
	msg_heartbeat mavHeartbeat=null;
	public Heartbeat() {
		
	}
	
	public Heartbeat(MAVLinkPacket mavpacket) {
		mavHeartbeat = new msg_heartbeat(mavpacket);
		timestamp=System.currentTimeMillis();
		
	}
	
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("autopilot", mavHeartbeat.autopilot);
		return res.toString();
	}


}
