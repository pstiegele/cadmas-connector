package com.controller.autopilot;

public class FlightMode {
	public static final int MANUAL = 0;
	// public static final int CIRCLE = 1;
	public static final int STABILIZE = 2;
	// public static final int TRAINING = 3;
	// public static final int ACRO = 4;
	public static final int FLY_BY_WIRE_THROTTLE_MANUAL = 5;
	public static final int FLY_BY_WIRE_THROTTLE_AUTO = 6;
	// public static final int CRUISE = 7;
	// public static final int AUTOTUNE = 8;
	public static final int AUTO = 10;
	public static final int RTL = 11;
	public static final int LOITER = 12;
	// public static final int AVOID_ADSB = 14;
	// public static final int GUIDED = 15;
	// public static final int INITIALISING = 16;
	// public static final int QSTABILIZE = 17;
	// public static final int QHOVER = 18;
	// public static final int QLOITER = 19;
	// public static final int QLAND = 20;
	// public static final int QRTL = 21;

	public static int getFlightModeByName(String name) {
		switch (name) {
		case "MANUAL":
		case "manual":
		case "Manual":
			return MANUAL;
		case "STABILIZE":
		case "Stabilize":
		case "stabilize":
			return STABILIZE;
		case "FLY_BY_WIRE_THROTTLE_MANUAL":
		case "Fly_by_wire_throttle_manual":
		case "FBWA":
			return FLY_BY_WIRE_THROTTLE_MANUAL;
		case "FLY_BY_WIRE_THROTTLE_AUTO":
		case "Fly_by_wire_throttle_auto":
		case "FBWB":
			return FLY_BY_WIRE_THROTTLE_AUTO;
		case "AUTO":
		case "auto":
		case "Auto":
			return AUTO;
		case "RTL":
		case "rtl":
		case "Rtl":
			return RTL;
		case "LOITER":
		case "Loiter":
		case "loiter":
			return LOITER;
		default:
			return RTL;
		}
	}
}
