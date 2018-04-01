package com.controller.messageHandler;

import java.util.concurrent.CountDownLatch;

import com.controller.socketConnection.SocketConnection;
import com.telecommand.Arm;
import com.telecommand.Mission;
import com.telecommand.StartMission;
import com.telemetry.Attitude;
import com.telemetry.Heartbeat;

import javafx.collections.ListChangeListener;

public class MessageHandler extends Thread {

	private static MessageHandler messageHandler;

	public MessageHandler() {
		messageHandler = this;
		this.setName("MessageHandler");
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
					SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
		// Heartbeat
		Heartbeat.getMessageMemory().addListener((ListChangeListener<? super Heartbeat>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SocketConnection.getSocketConnection().send(Heartbeat.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
		// Battery
		Attitude.getMessageMemory().addListener((ListChangeListener<? super Attitude>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
				}
			}).start();

		}));
		// MissionState
		Attitude.getMessageMemory().addListener((ListChangeListener<? super Attitude>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
		// Position
		Attitude.getMessageMemory().addListener((ListChangeListener<? super Attitude>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
		// Velocity
		Attitude.getMessageMemory().addListener((ListChangeListener<? super Attitude>) (c -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SocketConnection.getSocketConnection().send(Attitude.getMessageMemory().getNewestElement());
				}
			}).start();
			
		}));
	}

}
