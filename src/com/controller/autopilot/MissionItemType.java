package com.controller.autopilot;

public class MissionItemType {
	public static final int INVALID = -4;
	public static final int LAND = -3;
	public static final int TAKEOFF = -2;
	public static final int RTL = -1;
	public static final int WAYPOINT = 0;
	public static final int LOITER_5_MIN = 5 * 60;

	public static int getMissionItemTypeByName(String name) {
		switch (name) {
		case "INVALID":
		case "invalid":
		case "Invalid":
			return INVALID;
		case "LAND":
		case "land":
		case "Land":
			return LAND;
		case "TAKEOFF":
		case "takeoff":
		case "Takeoff":
			return TAKEOFF;
		case "RTL":
		case "rtl":
		case "Rtl":
			return RTL;
		case "WAYPOINT":
		case "waypoint":
		case "Waypoint":
			return WAYPOINT;
		case "LOITER":
		case "Loiter":
		case "loiter":
		case "LOITER_5_MIN":
			return LOITER_5_MIN;
		default:
			return RTL;
		}
	}
}