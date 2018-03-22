package ca.mcgill.ecse211.tests;

import lejos.hardware.Sound;

/**
 * This class is used to perform light localization
 * 
 *
 */
public class LightLocalizer {

	Navigation nav;
	Odometer odo;
	OdometryCorrection oc;
	private final double TILE_SIZE = 30.48;
	public boolean finished = false;
	private final double passLine = 9;

	public LightLocalizer(Navigation nav, Odometer odo, OdometryCorrection oc) {
		this.nav = nav;
		this.odo = odo;
		this.oc = oc;
	}

	/**
	 * performs light localization
	 * 
	 * @param atStartPoint
	 *            (true if at start point, false otherwise)
	 */
	public void Localize(boolean atStartPoint) {

		// correct y or x and theta
		Tests.correctionON = true;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		while (!oc.needsCorrection) {
			nav.moveForward(175);
		}

		Tests.correctionON = false;
		nav.move(-passLine, false);

		// turn 90 deg towards x or y line
		if ((Tests.startCorner == 0 || Tests.startCorner == 3) && atStartPoint) {
			nav.turnTo(90);
		} else if ((Tests.startCorner == 1 || Tests.startCorner == 2) && atStartPoint) {
			nav.turnTo(270);
		} else if (odo.getXYT()[0] < TILE_SIZE) {
			nav.turn(90);
		} else {
			nav.turn(90);
			// nav.turn(-90);
		}
		// correct x and theta
		Tests.correctionON = true;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		while (!oc.needsCorrection) {
			nav.moveForward(175);
		}
		Tests.correctionON = false;
		nav.move(-passLine, false);

		// turn to correct heading
		if (Tests.startingCorner[1] < 1 && atStartPoint) {
			nav.turnTo(0);
		} else if (Tests.startingCorner[1] > 1 && atStartPoint) {
			nav.turnTo(180);
		}
		Sound.twoBeeps();
		finished = true;
	}

}
