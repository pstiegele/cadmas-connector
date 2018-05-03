package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_statustext;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_RESULT;
import com.controller.autopilot.CustomMissionItem;

import tools.MessageMemory;

public class MissionItem implements TelemetryMessage{

	int command, result;
	CustomMissionItem item;
	long timestamp;
	
	private static MessageMemory<MissionItem> messageMemory = new MessageMemory<>();
	public MissionItem(msg_mission_item missionItem) {
		timestamp=System.currentTimeMillis();
		command = msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM;
		result = missionItem.seq;
		int type = 0;
		switch(missionItem.command){
		case MAV_CMD.MAV_CMD_NAV_LAND:
			type = -3;
			break;
		case MAV_CMD.MAV_CMD_NAV_TAKEOFF:
			type = -2;
			break;
		case MAV_CMD.MAV_CMD_NAV_WAYPOINT:
			type = 0;
			break;
		case MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH:
			type = -1;
			break;
		case MAV_CMD.MAV_CMD_NAV_LOITER_TIME:
			type = (int)missionItem.param1;
			break;
		default:
			break;
		}
		item = new CustomMissionItem(type, missionItem.x, missionItem.y, (int) missionItem.z);
		messageMemory.add(this);
	}
	
	public MissionItem(msg_home_position homeItem){
		timestamp=System.currentTimeMillis();
		command = msg_home_position.MAVLINK_MSG_ID_HOME_POSITION;
		result = MAV_RESULT.MAV_RESULT_ACCEPTED;
		item = new CustomMissionItem(0, (float) (homeItem.latitude / 1E7), (float) (homeItem.longitude / 1E7), (int) (homeItem.altitude / 1E3));
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("command", command).put("result", result);
		return res;
	}


	public static MessageMemory<MissionItem> getMessageMemory() {
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
	
	public CustomMissionItem getMissionItem(){
		return item;
	}

}
