package com.controller.messageHandler;

import java.util.concurrent.CountDownLatch;

import com.controller.socketConnection.SocketConnection;
import com.telecommand.Arm;
import com.telecommand.Mission;
import com.telecommand.StartMission;
import com.telemetry.Attitude;
import com.telemetry.Battery;
import com.telemetry.CameraImage;
import com.telemetry.Heartbeat;
import com.telemetry.MissionState;
import com.telemetry.Position;
import com.telemetry.Velocity;

import javafx.collections.ListChangeListener;

public class MessageHandler extends Thread {

	private static MessageHandler messageHandler;
	static long lastAttitudeTimestamp, lastBatteryTimestamp, lastHeartbeatTimestamp, lastMissionItemTimestamp,
			lastMissionStateTimestamp, lastPositionTimestamp, lastVelocityTimestamp;

	static int refreshRateAttitude, refreshRateBattery, refreshRateHeartbeat, refreshRateMissionItem,
			refreshRateMissionState, refreshRatePosition, refreshRateVelocity;

	public MessageHandler() {
		messageHandler = this;
		this.setName("MessageHandler");
		refreshRateAttitude = refreshRateBattery = refreshRateHeartbeat = refreshRateMissionItem = refreshRateMissionState = refreshRatePosition = refreshRateVelocity = 500;
		start();
	}

	public static MessageHandler getMessageHandler() {
		if (messageHandler == null) {
			new MessageHandler();
		}
		return messageHandler;
	}

	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		setListeners();

		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setListeners() {
		setTelemetryListeners();
		setTelecommandListeners();
	}

	private void setTelecommandListeners() {
		// Mission
		Mission.getMessageMemory().addListener((ListChangeListener<? super Mission>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Mission.getMessageMemory().getNewestElement().execute();
				}
			}).start();

		}));

		// Arm
		Arm.getMessageMemory().addListener((ListChangeListener<? super Arm>) (c -> {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Arm.getMessageMemory().getNewestElement().execute();
				}
			}).start();

		}));
		// StartMission
		StartMission.getMessageMemory().addListener((ListChangeListener<? super StartMission>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					StartMission.getMessageMemory().getNewestElement().execute();
				}
			}).start();

		}));

	}

	private void setTelemetryListeners() {
		// Attitude
		Attitude.getMessageMemory().addListener((ListChangeListener<? super Attitude>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Attitude.getMessageMemory().getNewestElement().getTimestamp()
							- lastAttitudeTimestamp > refreshRateAttitude) {
						lastAttitudeTimestamp = Attitude.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
					}
				}
			}).start();

		}));
		// Heartbeat
		Heartbeat.getMessageMemory().addListener((ListChangeListener<? super Heartbeat>) (c -> {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (Heartbeat.getMessageMemory().getNewestElement().getTimestamp()
							- lastHeartbeatTimestamp > refreshRateHeartbeat) {
						lastHeartbeatTimestamp = Heartbeat.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(Heartbeat.getMessageMemory().getNewestElement());
					}

				}
			}).start();

		}));
		// Battery
		Battery.getMessageMemory().addListener((ListChangeListener<? super Battery>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Battery.getMessageMemory().getNewestElement().getTimestamp()
							- lastBatteryTimestamp > refreshRateBattery) {
						lastBatteryTimestamp = Battery.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(Battery.getMessageMemory().getNewestElement());
					}

				}
			}).start();

		}));
		// MissionState
		MissionState.getMessageMemory().addListener((ListChangeListener<? super MissionState>) (c ->

		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (MissionState.getMessageMemory().getNewestElement().getTimestamp()
							- lastMissionStateTimestamp > refreshRateMissionState) {
						lastMissionStateTimestamp = MissionState.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(MissionState.getMessageMemory().getNewestElement());
					}

				}
			}).start();

		}));
		// Position
		Position.getMessageMemory().addListener((ListChangeListener<? super Position>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Position.getMessageMemory().getNewestElement().getTimestamp()
							- lastPositionTimestamp > refreshRatePosition) {
						lastPositionTimestamp = Position.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(Position.getMessageMemory().getNewestElement());
					}

				}
			}).start();

		}));
		// Velocity
		Velocity.getMessageMemory().addListener((ListChangeListener<? super Velocity>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Velocity.getMessageMemory().getNewestElement().getTimestamp()
							- lastVelocityTimestamp > refreshRateVelocity) {
						lastVelocityTimestamp = Velocity.getMessageMemory().getNewestElement().getTimestamp();
						SocketConnection.getSocketConnection().send(Velocity.getMessageMemory().getNewestElement());
					}

				}
			}).start();

		}));
		// CameraImage
		CameraImage.getMessageMemory().addListener((ListChangeListener<? super CameraImage>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
						SocketConnection.getSocketConnection().send(CameraImage.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
	}

}
