package ca.mcgill.ecse211.tests;

import lejos.hardware.Sound;

/**
 * This class is responsible for scanning  for a block once the robot is in a search zone
 * @author remi
 *
 */
public class BlockScanner {

	Navigation nav;
	UltrasonicPoller us;
	public boolean scannedBlock = false;

	public BlockScanner(Navigation nav, UltrasonicPoller us) {

		this.nav = nav;
		this.us = us;

	}
	/**
	 * Scan method
	 */
	public void Scan() {
		//TO DO: Spin robot until block is found
		// loop is just for testing
		while (true) {
			System.out.println(us.getDistance());
			if (us.getDistance() < 20f) {
				Sound.beep();
				scannedBlock = true;
			}
		}
	}

}
