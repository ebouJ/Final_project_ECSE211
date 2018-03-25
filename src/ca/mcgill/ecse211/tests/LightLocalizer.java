package ca.mcgill.ecse211.tests;

import ca.mcgill.ecse211.Final_Project.Tests;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
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
		if ((Tests.startCorner == 0) && atStartPoint) {
			nav.turnTo(90);
		} else if ((Tests.startCorner == 1) && atStartPoint) {
			nav.turnTo(0);
		} else if ((Tests.startCorner == 2) && atStartPoint) {
			nav.turnTo(270);
		} else if ((Tests.startCorner == 3) && atStartPoint) {
			nav.turnTo(180);
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

		// Navigate to nearest intersection (Useless but its a beta requirement)
		double x = Tests.startingCorner[0];
		double y = Tests.startingCorner[1];
		if (Tests.startCorner == 0 && atStartPoint) {
			nav.travelTo(x + 1, y + 1, true);
		}
		else if (Tests.startCorner == 1 && atStartPoint) {
			nav.travelTo(x - 1, y + 1, true);
		}
		else if (Tests.startCorner == 2 && atStartPoint) {
			nav.travelTo(x - 1, y - 1, true);
		}
		else if (Tests.startCorner == 3 && atStartPoint) {
			nav.travelTo(x + 1, y - 1, true);
		}
		// turn to correct heading
		if (Tests.startingCorner[1] < 1 && atStartPoint) {
			nav.turnTo(0);
		} else if (Tests.startingCorner[1] > 1 && atStartPoint) {
			nav.turnTo(180);
		}
		//travel to middle of nearest tile
		if (Tests.startCorner == 0 && atStartPoint) {
			nav.travelToTile(x + 1, y + 1);
		}
		else if (Tests.startCorner == 1 && atStartPoint) {
			nav.travelToTile(x - 2, y + 1);
		}
		else if (Tests.startCorner == 2 && atStartPoint) {
			nav.travelToTile(x - 2, y-2);
		}
		else if (Tests.startCorner == 3 && atStartPoint) {
			nav.travelToTile(x + 1, y-2);
		}

		finished = true;
	}

}
