package com.controller.autopilot;

import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;

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
		int type = 0;
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

	@Override
	public String toString() {
		String typeString;
		switch(type){
		case -4:
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
	
	
}
