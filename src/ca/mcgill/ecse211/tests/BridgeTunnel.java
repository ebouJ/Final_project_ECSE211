package ca.mcgill.ecse211.tests;

import lejos.hardware.Sound;

/**
 * This class is responsible for making the robot travel to the bridge.
 */

public class BridgeTunnel {
	Navigation nav;
	Odometer odo;
	LightLocalizer ll;
	// private final double TILE_SIZE = 30.48;
	public boolean finished = false;

	/**
	 * Constructor for the Bridge class
	 * 
	 * @param nav
	 * @param odo
	 * @param ll
	 */
	public BridgeTunnel(Navigation nav, Odometer odo, LightLocalizer ll) {
		this.nav = nav;
		this.odo = odo;
		this.ll = ll;
	}

	/**
	 * Responsible for making the robot travel to the bridge.
	 * 
	 * @param x
	 *            the value of x
	 * @param y
	 *            the value of x
	 */
	// TODO : Still need to fix this (only works from +y direction)
	public void travelToBridge() {
		// bridge is parallel to the y axis
		if (Math.abs(Tests.bridgeLocation_LL[0] - Tests.bridgeLocation_UR[0]) < 2) {
			// if we approach the bridge at UR
			if (!LLnearestPoint(Tests.bridgeLocation_UR, Tests.bridgeLocation_LL)) {
				double x = Tests.bridgeLocation_UR[0];
				double y = Tests.bridgeLocation_UR[1];
				x = x - 1;
				// nav.travelByTileSteps(x, y + 1);
				// nav.turnTo(180);

				nav.travelByTileSteps(x, y);

				nav.turnTo(180);
				nav.travelToTile(Tests.bridgeLocation_LL[0], Tests.bridgeLocation_LL[1] - 1);
				ll.Localize(false);
				// if we approach the tunnel at UR
			} else {
				Sound.beep();
			}
		}
		// bridge is parallel to the x axis
		else {

		}

		finished = true;

	}

	public void travelToTunnel() {
		// tunnel is parallel to the y axis
		if (Math.abs(Tests.tunnelLocation_LL[0] - Tests.tunnelLocation_UR[0]) < 2) {
			// if we approach the tunnel at LL
			if (LLnearestPoint(Tests.tunnelLocation_UR, Tests.tunnelLocation_LL)) {
				double x = Tests.tunnelLocation_LL[0];
				double y = Tests.tunnelLocation_LL[1];
				nav.travelByTileSteps(x, y - 1);
				// nav.turnTo(0);
				ll.Localize(false);
				// wait for light localizer to finish
				while (!ll.finished) {
				}
				// nav.travelToTile(x, y-2);
				// nav.travelByTileSteps(x, y - 1);
				// nav.turnTo(0);
				nav.travelToTile(Tests.tunnelLocation_UR[0] - 1, Tests.tunnelLocation_UR[1]);
				// if we approach the tunnel at UR
			} else {
				Sound.beep();
			}
			// tunnel is parallel to the x axis
		} else {
		}

		finished = true;

	}

	/**
	 * To verify witch bridge/tunnel coordinate is closer to the robot
	 * 
	 * @param UR
	 * @param LL
	 * @return true if LL is nearest point, false otherwise
	 */
	private boolean LLnearestPoint(double[] UR, double[] LL) {
		double odoX = odo.getXYT()[0];
		double odoY = odo.getXYT()[1];
		// compute x and y components
		double xUR = Math.abs(odoX - UR[0] * 30.48);
		double yUR = Math.abs(odoY - UR[1] * 30.48);
		double xLL = Math.abs(odoX - LL[0] * 30.48);
		double yLL = Math.abs(odoY - LL[1] * 30.48);
		// compute hypotenuses
		double distUR = Math.hypot(xUR, yUR);
		double distLL = Math.hypot(xLL, yLL);

		if (distLL < distUR) {
			return true;
		} else {
			return false;
		}
	}

}