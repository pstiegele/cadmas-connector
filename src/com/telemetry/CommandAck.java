package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_statustext;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_RESULT;

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
	
	public CommandAck(msg_mission_ack mission){
		timestamp=System.currentTimeMillis();
		command = msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK;
		result = mission.type;
		messageMemory.add(this);
	}
	
	public CommandAck(msg_mission_count count){
		timestamp=System.currentTimeMillis();
		command = msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT;
		result = count.count;
		messageMemory.add(this);
	}
	
	public CommandAck(msg_mission_request request){
		timestamp=System.currentTimeMillis();
		command = msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST;
		result = request.seq;
		messageMemory.add(this);
	}
	
	public CommandAck(msg_statustext status) {
		timestamp=System.currentTimeMillis();
		switch(new String(new String(status.text).replaceAll("\0", "").getBytes())){
		case "Throttle armed":
			command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
			result = MAV_RESULT.MAV_RESULT_ACCEPTED;
			break;
		case "Throttle disarmed":
			command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
			result = MAV_RESULT.MAV_RESULT_ACCEPTED;
			break;
		case "Calibrating barometer":
			command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
			result = MAV_RESULT.MAV_RESULT_IN_PROGRESS;
			break;
		case "Airspeed sensor calibrated":
			command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
			result = MAV_RESULT.MAV_RESULT_ACCEPTED;
			break;
		}
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
