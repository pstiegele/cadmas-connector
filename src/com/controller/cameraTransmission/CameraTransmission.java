package com.controller.cameraTransmission;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.controller.socketConnection.SocketConnection;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

import tools.Settings;

public class CameraTransmission extends Thread {

	private static CameraTransmission cameraTransmission;

	public CameraTransmission() {
		cameraTransmission = this;
		this.setName("SocketConnection");
		start();
	}

	public static CameraTransmission getCameraTransmission() {
		if (cameraTransmission == null) {
			new CameraTransmission();
		}
		return cameraTransmission;
	}

	@Override
	public void run() {
		RPiCamera cam = null;
		try {
			cam = new RPiCamera();
		} catch (FailedToRunRaspistillException e) {
			System.err.println("No Raspberry Pi camera found.");
		}
		if (cam != null) {
			BufferedImage img = null;
			while (true) {
				try {
					cam.takeStill(String.valueOf(System.currentTimeMillis()));
					img = cam.takeBufferedStill();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "jpg", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();
					SocketConnection.getSocketConnection().sendCameraImage(ByteBuffer.wrap(imageInByte));
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(Settings.getInstance().getCameraIntervall());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
