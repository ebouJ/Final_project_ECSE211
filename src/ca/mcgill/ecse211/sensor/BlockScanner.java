package ca.mcgill.ecse211.sensor;

import java.util.HashMap;

import ca.mcgill.ecse211.Final_Project.Tests;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;

/**
 * This class is responsible for scanning for a block once the robot is in a
 * search zone
 * 
 * @author remi
 *
 */
public class BlockScanner extends Thread {

	Navigation nav;
	UltrasonicPoller us;
	Odometer odo;
	public boolean scannedBlock = false;
	HashMap<Float, Double> map = new HashMap<Float, Double>();
	private float smallestDistance = Float.MAX_VALUE;
	private double smallestAngle = Double.MAX_VALUE;

	public BlockScanner(Navigation nav, UltrasonicPoller us, Odometer odo) {
		this.nav = nav;
		this.us = us;
		this.odo = odo;
	}

	/**
	 * Scan method
	 * 
	 */

	public synchronized boolean Scan() {
		// a timer to scan 90 degrees forward and backwards
		// put all the distances , and angles measured in the hashMap
		// reset values
		
		// spin 90 anti-clockwise
		spin(90);
		if (smallestDistance < 60f && withinSearchZone()) {
			Sound.beep();
			nav.turnTo(smallestAngle);
			if(this.smallestDistance > 35) { // greater than 90 
				nav.move(this.smallestDistance - 15,false);
				nav.turn(-25);
				spin(90);
				nav.turnTo(smallestAngle);
				nav.move(smallestDistance - 4, false);
			}else {
				nav.move(smallestDistance - 4, false);
			}
			return true;
		}
		return false;
	}

	public void spin(int angle) {
		smallestDistance = Float.MAX_VALUE;
		smallestAngle = Double.MAX_VALUE;
		int timer = angle == 90 ? 2000 : 4000;
		rotate(timer,true);
		//spin 90 counter-clockwise
		rotate(timer,false);
		
	    //spin 90 anti-clockwise
		smallestAngle = map.get(smallestDistance);
	}
	private void rotate(int timer, boolean clockwise) {
		long endTimeMillisCW = System.currentTimeMillis() + timer;
		while (true) {
			nav.spin(clockwise);
			float temp = us.getDistance();
			if (temp > 0) {
				smallestDistance = temp < smallestDistance ? temp : smallestDistance;
				map.put(temp, odo.getXYT()[2]);
			}
			if (System.currentTimeMillis() > endTimeMillisCW) {
				break;
			}
		}
	}

	public float getDistance() {
		return this.smallestDistance;
	}

	public double getAngle() {
		return this.smallestAngle;
	}

	private boolean withinSearchZone() {
		boolean withInX = false;
		boolean withInY = false;

		double targetX = (smallestDistance * Math.sin(Math.toRadians(smallestAngle)));
		targetX += odo.getXYT()[0];
		double targetY = (smallestDistance * Math.cos(Math.toRadians(smallestAngle)));
		targetY += odo.getXYT()[1];

		// if x value is within zone
		if ((targetX > Tests.SR_LL[0] * 30.48) && (targetX < Tests.SR_UR[0] * 30.48)) {
			withInX = true;
		}
		// if y value is within zone
		if ((targetY > Tests.SR_LL[1] * 30.48) && (targetY < Tests.SR_UR[1] * 30.48)) {
			withInY = true;
		}

		return withInY && withInX;
	}

}
