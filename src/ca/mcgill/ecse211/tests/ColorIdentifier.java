package ca.mcgill.ecse211.tests;

import java.util.Arrays;
import java.io.*;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

/**
 * Provides filtered light data to light localizer
 *
 *
 */
public class ColorIdentifier extends Thread {

	private float[] lightData;
	private float lightLevel = -1f;
	private static final Port blockColorPort = LocalEV3.get().getPort("S4");
	EV3ColorSensor colorSensor = new EV3ColorSensor(blockColorPort);
	SampleProvider sample = colorSensor.getColorIDMode();
	public boolean blockDetected = false; // true if a colored block is detected
	// possible colors of blocks

	public enum BlockColor {
		RED, BLUE, YELLOW, WHITE, NONE
	};

	private BlockColor tb;
	public boolean tbDetected = false;

	public BlockColor blockColor = BlockColor.NONE;

	/**
	 * 
	 * @param filteredLightData
	 */
	public ColorIdentifier(float[] filteredLightData, BlockColor tb) {
		this.lightData = filteredLightData;
		this.tb = tb;
	}

	public void run() {
		while (true) {
			sample.fetchSample(lightData, 0);
			this.lightLevel = lightData[0];
			if (lightData[0] == 2) {
				blockDetected = true;
				blockColor = BlockColor.BLUE;
			}
			if (lightData[0] == 0) {
				blockDetected = true;
				blockColor = BlockColor.RED;
			}
			if (lightData[0] == 3) {
				blockDetected = true;
				blockColor = BlockColor.YELLOW;
			}
			if (lightData[0] == 6) {
				blockDetected = true;
				blockColor = BlockColor.WHITE;
			}
			// if we are not detecting a colored block
			else if ((lightData[0] != 0) && (lightData[0] != 2) && (lightData[0] != 3) && (lightData[0] != 6)) {
				blockDetected = false;
				blockColor = BlockColor.NONE;
			}

			sleepThread(0.050f); // Poor man's timed sampling
		}
	}

	public void sleepThread(float seconds) {
		try {
			Thread.sleep((long) (seconds * 1000f));
		} catch (Exception e) {
		}
	}
	public synchronized boolean getBlockDetected() {
		return this.blockDetected;
	}
	public synchronized BlockColor getBlockColor() {
		return this.blockColor;
	}
}
