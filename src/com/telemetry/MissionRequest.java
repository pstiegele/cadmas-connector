package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_request;

import tools.MessageMemory;

public class MissionRequest extends msg_mission_request implements TelemetryMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = MAVLINK_MSG_ID_MISSION_REQUEST;
	long timestamp = -1;
	public static MessageMemory<MissionRequest> messageMemory = new MessageMemory<>();
	public MissionRequest() {
		
	}
	
	public MissionRequest(MAVLinkPacket mavpacket) {
		super(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("seq", this.seq);
		return res.toString();
	}

	
	public static MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "getMission";
	}


}
