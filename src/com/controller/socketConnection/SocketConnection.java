package com.controller.socketConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import javax.websocket.DeploymentException;

import org.json.JSONObject;

import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.CadmasClientEndpoint.SocketMessageHandler;
import com.telemetry.TelemetryMessage;

public class SocketConnection extends Thread {

	private MessageHandler messageHandler;
	private String token = null;

	public SocketConnection(MessageHandler messageHandler) {
		this.setName("SocketConnection");
		this.messageHandler = messageHandler;

		start();
	}

	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		try {
			connect();
			latch.await();

		} catch (URISyntaxException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private SocketMessageHandler getSocketMessageHandler() {
		return new CadmasClientEndpoint.SocketMessageHandler() {
			public void handleMessage(String message) {
				JSONObject jsonMessage = new JSONObject(message);
				System.out.println(message);
				switch (jsonMessage.getString("method")) {
				case "authenticate":
					if (jsonMessage.getJSONObject("payload").getBoolean("authenticated")) {
						System.out.println("we are authenticated");
						token = jsonMessage.getJSONObject("payload").getString("token");
					} else {
						System.out.println("wrong credentials");
					}
					break;
				case "heartbeat":
					System.out.println("server heartbeat received");
					break;
				case "newMission":
					System.out.println("new mission received");
					break;

				default:
					System.out.println(jsonMessage.getString("method"));
					break;
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
	private static String getMessage(String message) {
		JSONObject obj = new JSONObject();
		obj.append("user", "bot").append("message", message);
		return obj.toString();
	}

	public void connect() throws URISyntaxException {
		String uri = System.getenv().get("CADMAS_URI");
		if (uri == null) {
		uri = "ws://localhost/connector";
//			uri = "wss://cadmasapp.raapvdzcqu.eu-west-1.elasticbeanstalk.com/connector";
		}
		final CadmasClientEndpoint clientEndPoint = new CadmasClientEndpoint(new URI(uri));
		clientEndPoint.addMessageHandler(getSocketMessageHandler());

	}

	public boolean send(TelemetryMessage msg) {
		System.out.println((msg.getJSON()));

		return true;
	}

}
