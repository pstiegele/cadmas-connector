package com.controller.cameraTransmission;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.enums.MeteringMode;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import com.telemetry.CameraImage;

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
			cam.setHeight(200).setWidth(300).setQuality(90).setExposure(Exposure.SPORTS).setTimeout(100);
		} catch (FailedToRunRaspistillException e) {
			System.err.println("No Raspberry Pi camera found.");
		}
		if (cam != null) {
			BufferedImage img = null;
			while (true) {
				try {
					System.out.println("cam called");
					cam.takeStill(String.valueOf(System.currentTimeMillis()));
					img = cam.takeBufferedStill();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "jpg", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();
					new CameraImage(imageInByte);
				} catch (IOException | IllegalArgumentException | InterruptedException e) {
					System.out.println("got no camera image");
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
