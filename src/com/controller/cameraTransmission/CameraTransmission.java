package com.controller.cameraTransmission;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.enums.ImageEffect;
import com.hopding.jrpicam.enums.MeteringMode;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import com.telemetry.CameraImage;

import tools.Settings;

public class CameraTransmission extends Thread {

	private static CameraTransmission cameraTransmission;

	public CameraTransmission() {
		cameraTransmission = this;
		this.setName("CameraTransmission");
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
		if (Settings.getInstance().getEmulateCamera()) {
			try {
				emulateCamera();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			RPiCamera cam = null;
			try {
				cam = new RPiCamera();
				cam.setHeight(200).setWidth(300).setQuality(70).setExposure(Exposure.SPORTS).setTimeout(2000)
						.setDateTimeOn().setFullPreviewOff().turnOffPreview().turnOffThumbnail();
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

	private void emulateCamera() throws IOException {
		//System.out.println("emulate camera");
		File folder = new File("C:\\Users\\pstiegele\\Documents\\Workspaces\\Git\\cameratest");
		File[] listOfFiles = folder.listFiles();
		int fileIndex=0;
		//System.out.println(listOfFiles.length);
		while (true) {
			String filename = "";
			if(fileIndex>=listOfFiles.length)
				fileIndex=0;
			if(listOfFiles[fileIndex].isFile()&&listOfFiles[fileIndex].getName().endsWith(".data")) {
				filename=listOfFiles[fileIndex].getAbsolutePath();
				fileIndex++;
			}else {
				fileIndex++;
				continue;
			}
			
			System.out.println("cam called: "+filename);
			String everything;
			String[] everythingSplitted;
			byte[] everythingSplittedInBytes;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			//BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\pstiegele\\Documents\\Workspaces\\Git\\cameratest\\1532607017.data"));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				everything = sb.toString().substring(1).substring(0, sb.length()-4);
				everythingSplitted = everything.split(",");
				everythingSplittedInBytes = new byte[everythingSplitted.length];
				for (int i = 0; i < everythingSplitted.length; i++) {
					int actIndex = i;
					if(everythingSplitted[i].contains("]")) {
						everythingSplitted[i]=everythingSplitted[i].substring(0,everythingSplitted[i].indexOf("]"));
						i=everythingSplitted.length;
					}
						
					try {
						everythingSplittedInBytes[actIndex]=Byte.parseByte(everythingSplitted[actIndex]);	
					} catch (Exception e) {
						System.err.println("Error in file: "+filename+"\nException: "+e);
						continue;
					}
				}
			} finally {
				br.close();
			}
			new CameraImage(everythingSplittedInBytes);
			try {
				Thread.sleep(Settings.getInstance().getCameraIntervall());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
