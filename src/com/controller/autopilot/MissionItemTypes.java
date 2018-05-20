package com.controller.autopilot;

import com.MAVLink.enums.MAV_RESULT;

public class MissionItemTypes {
	public static final int INVALID = MAV_RESULT.MAV_RESULT_FAILED;
	public static final int LAND = -3;
	public static final int TAKEOFF = -2;
	public static final int RTL = -1;
	public static final int WAYPOINT = 0;
	public static final int LOITER_5_MIN = 5*60;
}
