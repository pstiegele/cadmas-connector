package com.controller.socketConnection;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import com.controller.autopilot.Autopilot;
import com.telecommand.Arm;
import com.telecommand.Mission;

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
		System.out.printf("Got msg: %s%n", msg);
		try {
			JSONObject jsonMessage = new JSONObject(msg);
			switch (jsonMessage.getString("method")) {
			case "arm":
				new Arm(jsonMessage.getJSONObject("payload"));
				break;
			case "startMission":
				System.out.println("startMission received");
				break;
			case "setMode":
				System.out.println("setMode received: " + jsonMessage);
				Autopilot.getAutopilot().getAutopilotTransmitter()
						.setMode(Integer.parseInt(jsonMessage.getString("mode")));
				break;
			case "mission":
				System.out.println("mission received");
				new Mission(jsonMessage.getJSONObject("payload"));
				break;
			default:
				System.out.println("message with unresolvable method received: " + jsonMessage.getString("method"));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


