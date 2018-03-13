package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_attitude;

import tools.MessageMemory;

public class Attitude extends TelemetryMessage{
	
	long timestamp = -1;
	msg_attitude mavAttitude=null;
	private static MessageMemory<Attitude> messageMemory = new MessageMemory<>();
	public Attitude() {
		
	}
	
	public Attitude(MAVLinkPacket mavpacket) {
		mavAttitude = new msg_attitude(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("pitch", mavAttitude.pitch);
		return res.toString();
	}


	@Override
	public MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}


}
