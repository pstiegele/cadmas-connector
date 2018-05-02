package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_command_ack;

import tools.MessageMemory;

public class CommandAck implements TelemetryMessage{
	
	int command, result;
	long timestamp;
	
	private static MessageMemory<CommandAck> messageMemory = new MessageMemory<>();
	public CommandAck(msg_command_ack message) {
		timestamp=System.currentTimeMillis();
		command = message.command;
		result = message.result;
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("command", command).put("result", result);
		return res;
	}


	public static MessageMemory<CommandAck> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "commandAck";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public int getCommand(){
		return command;
	}
	
	public int getResult(){
		return result;
	}

}
