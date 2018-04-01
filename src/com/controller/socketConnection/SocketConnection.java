package com.controller.socketConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import com.controller.socketConnection.CadmasClientEndpoint.SocketMessageHandler;
import com.telecommand.Arm;
import com.telecommand.Mission;
import com.telemetry.TelemetryMessage;

public class SocketConnection extends Thread {

	private CadmasClientEndpoint clientEndPoint = null;
	private String apikey = "";
	
	private static SocketConnection socketConnection;

	public SocketConnection() {
		socketConnection=this;
		this.setName("SocketConnection");
		apikey = System.getenv().get("CADMAS_APIKEY");

		start();
	}
	
	public static SocketConnection getSocketConnection() {
		if(socketConnection==null) {
			new SocketConnection();
		}
		return socketConnection;
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
				try {
					System.out.println("Message received: "+message);
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
						System.out.println("message with unresolvable method received: "+jsonMessage.getString("method"));
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
	private static String getMessage(String message) {
		JSONObject obj = new JSONObject();
		obj.append("user", "bot").append("message", message);
		return obj.toString();
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

	public boolean send(TelemetryMessage msg) {
		System.out.println((msg.getJSON()));

		return true;
	}

//	public void requestMission() {
//		JSONObject req = new JSONObject().put("apikey", apikey).put("method", "getMission");
//		clientEndPoint.sendMessage(req.toString());
//	}

}
