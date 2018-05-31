package com.controller.autopilot;

import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import tools.Settings;

public class CustomMissionItem {
	int type = 0; // Use MissionItemTypes enum
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
		type = MissionItemType.INVALID;
		switch(item.command){
		case MAV_CMD.MAV_CMD_NAV_LAND:
			type = MissionItemType.LAND;
			break;
		case MAV_CMD.MAV_CMD_NAV_TAKEOFF:
			type = MissionItemType.TAKEOFF;
			break;
		case MAV_CMD.MAV_CMD_NAV_WAYPOINT:
			type = MissionItemType.WAYPOINT;
			break;
		case MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH:
			type = MissionItemType.RTL;
			break;
		case MAV_CMD.MAV_CMD_NAV_LOITER_TIME:
			type = (int)item.param1;
			break;
		default:
			type = MissionItemType.INVALID;
			break;
		}
		latitude = item.x;
		longitude = item.y;
		altitude = (int) item.z;
	}
	
	public msg_mission_item toMavlinkItem() {
		msg_mission_item item = new msg_mission_item();
		item.frame = Settings.getInstance().getFrameOrientation();
		item.x = latitude;
		item.y = longitude;
		item.z = altitude;
		switch(type){
		case MissionItemType.INVALID:
			return null;
		case MissionItemType.LAND:
			item.command = MAV_CMD.MAV_CMD_NAV_LAND;
			item.param1 = Settings.getInstance().getAbortAltitude(); //abort altitude in meters
			break;
		case MissionItemType.TAKEOFF:
			item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
			item.param1 = Settings.getInstance().getTakeOffPitch(); //pitch angle in degrees
			break;
		case MissionItemType.RTL:
			item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
			break;
		case MissionItemType.WAYPOINT:
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
		case MissionItemType.INVALID:	//-4
			typeString = "invalid";
			break;
		case MissionItemType.LAND:
			typeString = "Land";
			break;
		case MissionItemType.TAKEOFF:
			typeString = "Take Off";
			break;
		case MissionItemType.RTL:
			typeString = "RTL";
			break;
		case MissionItemType.WAYPOINT:
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
