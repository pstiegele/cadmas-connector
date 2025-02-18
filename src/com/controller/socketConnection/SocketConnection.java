package com.controller.socketConnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;

import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_battery_status;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_vfr_hud;
import com.telemetry.Attitude;
import com.telemetry.Battery;
import com.telemetry.Heartbeat;
import com.telemetry.MissionState;
import com.telemetry.Position;
import com.telemetry.TelemetryMessage;
import com.telemetry.Velocity;

import tools.Settings;

public class SocketConnection extends Thread {

	private CadmasClientEndpoint clientEndPoint = null;
	private int msgID = 0;
	CountDownLatch runLatch = null;

	private static SocketConnection socketConnection;

	public SocketConnection() {
		socketConnection = this;
		this.setName("SocketConnection");
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
		while (true) {

			runLatch = new CountDownLatch(1);
			try {
				try {
					boolean connected = false;
					do {
						connect();
						Thread.sleep(2000);
						if (clientEndPoint != null&&clientEndPoint.session!=null&&clientEndPoint.session.isOpen())
							connected = true;
					} while (!connected);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (clientEndPoint == null && !clientEndPoint.session.isOpen())
					;
				// System.out.println("is Open");
				// while(true) {
				// test();
				// Thread.sleep(500);
				// }
				runLatch.await();

			} catch (URISyntaxException | InterruptedException e) {
				throw new RuntimeException(e);
			}

		}
	}

	private void test() {
		while (true) {
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
			
			new Battery(new msg_battery_status());
			int[] a = {1,2,3,4};
			new Heartbeat(new msg_heartbeat(), a, 26);
			new MissionState(new msg_mission_current());

			msg_global_position_int pos = new msg_global_position_int();
			pos.lon = (int) (9970359 + (rangeMin * 10 + (rangeMax * 10 - rangeMin * 10) * r.nextDouble()));
			pos.lat = (int) (49781641 + (rangeMin * 10 + (rangeMax * 10 - rangeMin * 10) * r.nextDouble()));
			pos.alt = (int) (100 + (rangeMin + (rangeMax - rangeMin) * r.nextDouble()));
			new Position(pos);
			try {
				Thread.sleep(Settings.getInstance().getTelemetryRefreshRate());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

		SslContextFactory sec = new SslContextFactory();
		WebSocketClient client = new WebSocketClient(sec);
		client.setConnectTimeout(2000);
		clientEndPoint = new CadmasClientEndpoint();
		try {
			client.start();

			URI echoUri = new URI(Settings.getInstance().getSocketURI());
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			ArrayList<String> protocol = new ArrayList<String>();
			protocol.add(Settings.getInstance().getSocketAPIKey());
			request.setSubProtocols(protocol);
			client.connect(clientEndPoint, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("oh: " + e);
		}

	}

	public void send(TelemetryMessage msg) {
		boolean sending = true;
		if (sending && clientEndPoint != null && clientEndPoint.session != null && clientEndPoint.session.isOpen()) {
			synchronized (SocketConnection.class) {

				try {
					String res = createMessage(msg);
					// System.out.println("send msg: "+res);
					clientEndPoint.session.getRemote().sendString(res);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void sendBinary(ByteBuffer msg) {
		boolean sending = true;
		if (sending && clientEndPoint != null && clientEndPoint.session != null && clientEndPoint.session.isOpen()) {
			synchronized (SocketConnection.class) {

				try {
					// String res = createMessage(msg);
					// System.out.println("send msg: "+res);
					clientEndPoint.session.getRemote().sendBytes(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private int getMsgID() {
		if (msgID == Integer.MAX_VALUE) {
			msgID = 0;
			return msgID;
		}
		return msgID++;
	}

}
