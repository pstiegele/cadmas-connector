package com.controller.socketConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import javax.websocket.SendHandler;
import javax.websocket.SendResult;

import org.json.JSONObject;

import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.CadmasClientEndpoint.SocketMessageHandler;
import com.telecommand.Arm;
import com.telecommand.Mission;
import com.telemetry.Heartbeat;
import com.telemetry.TelemetryMessage;

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
			connect();
			while (!clientEndPoint.userSession.isOpen())
				;
			System.out.println("is Open");
			send(new Heartbeat());
			latch.await();

		} catch (URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private SocketMessageHandler getSocketMessageHandler() {
		return new CadmasClientEndpoint.SocketMessageHandler() {
			public void handleMessage(String message) {
				try {
					System.out.println("Message received: " + message);
					JSONObject jsonMessage = new JSONObject(message);
					switch (jsonMessage.getString("method")) {
					case "arm":
						new Arm(jsonMessage.getJSONObject("payload"));
						break;
					case "startMission":
						System.out.println("startMission received");
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
		};
	}

	/**
	 * Create a json representation with all necessary overhead
	 * 
	 * @param message
	 * @return
	 */
	private String createMessage(String payload, String method) {
		JSONObject msg = new JSONObject();
		msg.put("time", System.currentTimeMillis()).put("id", getMsgID()).put("apikey", apikey).put("method", method)
				.put("payload", payload);
		return msg.toString();
	}

	public void connect() throws URISyntaxException {
		String uri = System.getenv().get("CADMAS_URI");
		if (uri == null) {
			uri = "ws://localhost/connector";
			// uri = "wss://cadmasapp.raapvdzcqu.eu-west-1.elasticbeanstalk.com/connector";
		}
		clientEndPoint = new CadmasClientEndpoint(new URI(uri));
		clientEndPoint.addMessageHandler(getSocketMessageHandler());

	}

	public void send(TelemetryMessage msg) {
		boolean sending = false;
		if (sending) {

			clientEndPoint.userSession.getAsyncRemote()
					.sendText(createMessage(msg.getJSON(), msg.getSocketMethodName()), new SendHandler() {

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
