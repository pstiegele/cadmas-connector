package com.controller.socketConnection;

import java.util.ArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.controller.autopilot.Autopilot;
import com.controller.autopilot.CustomMissionItem;
import com.controller.autopilot.FlightMode;
import com.controller.autopilot.MissionItemType;

@WebSocket
public class CadmasClientEndpoint {
	Session session = null;

	public CadmasClientEndpoint() {

	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
		this.session = null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//try to reconnect
		SocketConnection.getSocketConnection().runLatch.countDown();
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Got connect: %s%n", session.getRemoteAddress());
		this.session = session;
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		// System.out.printf("Got msg: %s%n", msg);
		try {
			JSONObject jsonMessage = new JSONObject(msg);
			switch (jsonMessage.getString("method")) {
			case "calibrate":
				System.out.println("calibrate received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().calibrate();
				break;
			case "arm":
				System.out.println("arm received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().arm();
				break;
			case "setMode":
				System.out.println("setMode received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().setMode(
						FlightMode.getFlightModeByName(jsonMessage.getJSONObject("payload").getString("mode")));
				break;
			case "disarm":
				System.out.println("disarm received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().disarm();
				break;
			case "setHomePosition":
				System.out.println("setHomePosition received: " + jsonMessage);
				CustomMissionItem cmi = new CustomMissionItem(0, jsonMessage.getFloat("latitude"),
						jsonMessage.getFloat("longitude"), jsonMessage.getInt("altitude"));
				Autopilot.getAutopilot().getAutopilotTransmitter().setHomePosition(cmi);
				break;
			case "setMission":
				System.out.println("setMission received: " + jsonMessage);
				ArrayList<CustomMissionItem> mission = parseMission(
						jsonMessage.getJSONObject("payload").getJSONArray("waypoints"));
				Autopilot.getAutopilot().getAutopilotTransmitter().sendMission(mission,
						jsonMessage.getJSONObject("payload").getBoolean("restart"));
				break;
			case "attitudeACK":
			case "batteryACK":
			case "heartbeatACK":
			case "missionStateACK":
			case "positionACK":
			case "velocityACK":
			case "cameraImageACK":
				// System.out.println("ack received: " + jsonMessage);
				break;
			default:
				System.out.println("message with unresolvable method received: " + jsonMessage.getString("method"));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<CustomMissionItem> parseMission(JSONArray waypoints) {
		ArrayList<CustomMissionItem> waypointList = new ArrayList<>();

		for (int i = 0; i < waypoints.length(); i++) {
			JSONObject element = (JSONObject) waypoints.get(i);
			int type = MissionItemType.getMissionItemTypeByName(element.getString("type"));
			float latitude = element.getFloat("latitude");
			float longitude = element.getFloat("longitude");
			int altitude = element.getInt("altitude");
			waypointList.add(new CustomMissionItem(type, latitude, longitude, altitude));
		}
		return waypointList;
	}
}
