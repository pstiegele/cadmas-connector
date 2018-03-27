package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_scaled_pressure;

import tools.MessageMemory;

public class CommandAck extends msg_command_ack implements TelemetryMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = MAVLINK_MSG_ID_COMMAND_ACK;
	long timestamp = -1;
	private static MessageMemory<CommandAck> messageMemory = new MessageMemory<>();
	public CommandAck() {
		
	}
	
	public CommandAck(MAVLinkPacket mavpacket) {
		super(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("command", this.command);
		return res.toString();
	}


	@Override
	public MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}


}
