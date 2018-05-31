package com.controller.socketConnection;

import java.util.ArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import com.controller.autopilot.Autopilot;
import com.controller.autopilot.CustomMissionItem;
import com.controller.autopilot.FlightMode;

@WebSocket
public class CadmasClientEndpoint {
	Session session = null;

	public CadmasClientEndpoint() {

	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
		this.session = null;
		// this.closeLatch.countDown(); // trigger latch
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Got connect: %s%n", session);
		this.session = session;
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		//System.out.printf("Got msg: %s%n", msg);
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
				Autopilot.getAutopilot().getAutopilotTransmitter()
						.setMode(FlightMode.getFlightModeByName(jsonMessage.getString("mode")));
				break;
			case "disarm":
				System.out.println("disarm received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().disarm();
				break;
			case "setHomePosition":
				System.out.println("setHomePosition received: " + jsonMessage);
				CustomMissionItem cmi = new CustomMissionItem(jsonMessage.getInt("type"),
						jsonMessage.getFloat("latitude"), jsonMessage.getFloat("longitude"),
						jsonMessage.getInt("height"));
				Autopilot.getAutopilot().getAutopilotTransmitter().setHomePosition(cmi);
				break;
			case "setMission":
				System.out.println("setMission received: " + jsonMessage);
				ArrayList<CustomMissionItem> mission = parseMission(jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter().sendMission(mission,
						jsonMessage.getBoolean("restart"));
				break;
			case "attitudeACK":
			case "batteryACK":
			case "heartbeatACK":
			case "missionStateACK":
			case "positionACK":
			case "velocityACK":
				System.out.println("ack received: " + jsonMessage);
				break;
			default:
				System.out.println("message with unresolvable method received: " + jsonMessage.getString("method"));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<CustomMissionItem> parseMission(JSONObject jsonMessage) {
		// TODO Auto-generated method stub
		return null;
	}
}
