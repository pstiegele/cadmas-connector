package com.controller.socketConnection;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.websocket.MessageHandler;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;

import org.json.JSONObject;

import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_vfr_hud;
import com.controller.autopilot.Autopilot;
import com.telecommand.Arm;
import com.telecommand.Mission;
import com.telemetry.Attitude;
import com.telemetry.Position;
import com.telemetry.TelemetryMessage;
import com.telemetry.Velocity;

public class SocketConnection extends Thread {

	private CadmasClientEndpoint clientEndPoint = null;
	private String apikey = "";
	private int msgID = 0;

	private static SocketConnection socketConnection;

	public SocketConnection() {
		socketConnection = this;
		this.setName("SocketConnection");
		apikey = System.getenv().get("CADMAS_APIKEY");

		start();
	}

	public static SocketConnection getSocketConnection() {
		if (socketConnection == null) {
			new SocketConnection();
		}
		return socketConnection;
	}

	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		try {
			try {
				connect();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (!clientEndPoint.userSession.isOpen())
				;
			System.out.println("is Open");
//			while(true) {
//				test();
//				Thread.sleep(500);
//			}
			latch.await();

		} catch (URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void test() {
		Random r = new Random();
		int rangeMin = -30;
		int rangeMax = 30;
		msg_attitude att = new msg_attitude();
		att.pitch = (float) (rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		att.roll = (float) (rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		att.yaw = (float) (rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		new Attitude(att);
		
		msg_vfr_hud vfr = new msg_vfr_hud();
		vfr.climb = (float) (rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		vfr.airspeed = (float) (0 + (160 - 0) * r.nextDouble());
		vfr.alt = (float) (0 + (3000 - 0) * r.nextDouble());
		new Velocity(vfr);
		
		msg_global_position_int pos = new msg_global_position_int();
		pos.lon = (int) (9970359+(rangeMin*10 + (rangeMax*10 - rangeMin*10) * r.nextDouble()));
		pos.lat = (int) (49781641+(rangeMin*10 + (rangeMax*10 - rangeMin*10) * r.nextDouble()));
		pos.alt = (int) (100+(rangeMin + (rangeMax - rangeMin) * r.nextDouble()));
		new Position(pos);
	}

	/**
	 * Create a json representation with all necessary overhead
	 * 
	 * @param message
	 * @return
	 */
	private String createMessage(TelemetryMessage telemetryMSG) {
		JSONObject msg = new JSONObject();
		msg.put("id", getMsgID()).put("method", telemetryMSG.getSocketMethodName()).put("payload",
				telemetryMSG.getJSON());
		return msg.toString();
	}

	public void connect() throws URISyntaxException, MalformedURLException {
		String uri = System.getenv().get("CADMAS_URI");
		String apikey = System.getenv().get("CADMAS_APIKEY");
		if (uri == null) {
			//uri = "ws://localhost/connector?apikey=" + apikey;
			uri = "ws://192.168.2.174/connector?apikey=" + apikey;
			//uri = "wss://cadmas.net:8081/connector?apikey="+apikey;
		}
		//URI myuri = new URI("wss", null, "cadmasapp.raapvdzcqu.eu-west-1.elasticbeanstalk.com", 8081, "/client", "token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcyIsImV4cCI6MTUyNzQ1MjQ5ODQ1OSwidXNlcklEIjoxLCJpYXQiOjE1MjQ4NjA0OTh9.9M9_aKxT9e1aIbMwRtUMbonFx8z-jPvOIrHQpV0PDhk", null);
		//URI myuri = new URI("wss", null, "cadmas.net", 8081, "/auth", null, null);
		//System.out.println(myuri.toString());
		clientEndPoint = new CadmasClientEndpoint(new URI(uri), apikey);
		clientEndPoint.userSession.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				try {
					JSONObject jsonMessage = new JSONObject(message);
					switch (jsonMessage.getString("method")) {
					case "arm":
						new Arm(jsonMessage.getJSONObject("payload"));
						break;
					case "startMission":
						System.out.println("startMission received");
						break;
					case "setMode":
						System.out.println("setMode received: "+jsonMessage);
						Autopilot.getAutopilot().getAutopilotTransmitter().setMode(Integer.parseInt(jsonMessage.getString("mode")));
						break;
					case "mission":
						System.out.println("mission received");
						new Mission(jsonMessage.getJSONObject("payload"));
						break;
					default:
						System.out.println(
								"message with unresolvable method received: " + jsonMessage.getString("method"));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void send(TelemetryMessage msg) {
		System.out.println("send msg: " + msg.getJSON());
		boolean sending = true;
		if (sending) {

			clientEndPoint.userSession.getAsyncRemote().sendText(createMessage(msg), new SendHandler() {

				@Override
				public void onResult(SendResult result) {
					if (!result.isOK()) {
						System.out.println("error sending socket message. Exception: ");
						System.err.println(result.getException());
					}

				}
			});

		}
	}

	private int getMsgID() {
		if (msgID == Integer.MAX_VALUE) {
			msgID = 0;
			return msgID;
		}
		return msgID++;
	}

	// public void requestMission() {
	// JSONObject req = new JSONObject().put("apikey", apikey).put("method",
	// "getMission");
	// clientEndPoint.sendMessage(req.toString());
	// }

}
