package com.controller.autopilot;

import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import com.MAVLink.enums.MAV_RESULT;

import tools.Settings;

public class CustomMissionItem {
	int type = 0; // 0=Waypoint -1=RTL -2=TakeOff -3=Land n=Loiter for n secs ...
	float latitude = 0;
	float longitude = 0;
	int altitude = 0;
	
	public CustomMissionItem(int type, float latitude, float longitude, int height){
		this.type = type;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = height;
	}
	
	public CustomMissionItem(msg_mission_item item) {
		type = MAV_RESULT.MAV_RESULT_FAILED;
		switch(item.command){
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
			type = (int)item.param1;
			break;
		default:
			break;
		}
		latitude = item.x;
		longitude = item.y;
		altitude = (int) item.z;
	}
	
	public msg_mission_item toMavlinkItem() {
		msg_mission_item item = new msg_mission_item();
		item.frame = MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT;
		item.x = latitude;
		item.y = longitude;
		item.z = altitude;
		switch(type){
		case -3:
			item.command = MAV_CMD.MAV_CMD_NAV_LAND;
			item.param1 = Settings.getInstance().getAbortAltitude(); //abort altitude in meters
		case -2:
			item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
			item.param1 = Settings.getInstance().getTakeOffPitch(); //pitch angle in degrees
			break;
		case -1:
			item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
			break;
		case 0:
			item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
			break;
		default:
			item.command = MAV_CMD.MAV_CMD_NAV_LOITER_TIME;
			item.param1 = type; //loiter time in seconds
			item.param3 = Settings.getInstance().getLoiterRadius(); //loiter radius in meters
			break;
		}
		return item;
	}

	@Override
	public String toString() {
		String typeString;
		switch(type){
		case MAV_RESULT.MAV_RESULT_FAILED:	//-4
			typeString = "invalid";
			break;
		case -3:
			typeString = "Land";
			break;
		case -2:
			typeString = "Take Off";
			break;
		case -1:
			typeString = "RTL";
			break;
		case 0:
			typeString = "Waypoint";
			break;
		default:
			typeString = "Loiter(" + type +"s)";
			break;
		}
		return typeString + "\tLat: " + latitude + "\tLong: " + longitude + "\tAlt: " + altitude;
	}

	@Override
	public boolean equals(Object obj) {
		double tolerance = 4e-6; // =0.000004
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomMissionItem other = (CustomMissionItem) obj;
		if (altitude != other.altitude)
			return false;
		if (Math.abs(latitude - other.latitude) > tolerance)
			return false;
		if (Math.abs(longitude - other.longitude) > tolerance)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
