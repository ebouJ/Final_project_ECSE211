package ca.mcgill.ecse211.navigation;

import ca.mcgill.ecse211.Final_Project.Tests;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.tests.LightLocalizer;
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
	 */
	// TODO : Still need to fix this (only works from +y direction)
	public void travelToBridge() {
		// bridge is parallel to the y axis
		if (Math.abs(Tests.bridgeLocation_LL[0] - Tests.bridgeLocation_UR[0]) < 2) {
			// if we approach the bridge at UR
			if (!(LLnearestPointY(Tests.bridgeLocation_UR, Tests.bridgeLocation_LL, true))) {
				double x = Tests.bridgeLocation_UR[0];
				double y = Tests.bridgeLocation_UR[1];
				x = x - 1;
				// travel to front of bridge
				nav.travelByTileSteps(x, y);
				// cross bridge
				nav.travelToTile(Tests.bridgeLocation_LL[0], Tests.bridgeLocation_LL[1] - 1);
				// Localize
				ll.Localize(false);
			}
			// if we approach the bridge at LL
			else {
				double x = Tests.bridgeLocation_LL[0];
				double y = Tests.bridgeLocation_LL[1];
				// travel to front of bridge
				nav.travelByTileSteps(x, y - 1);
				// cross bridge
				nav.travelToTile(Tests.bridgeLocation_UR[0] - 1, Tests.bridgeLocation_UR[1]);
				// Localize
				ll.Localize(false);
			}
		}
		// bridge is parallel to the x axis
		else {
			// if we approach the bridge at UR
			if (!(LLnearestPointY(Tests.bridgeLocation_UR, Tests.bridgeLocation_LL, false))) {
				double x = Tests.bridgeLocation_UR[0];
				double y = Tests.bridgeLocation_UR[1];
				y = y - 1;
				// travel to front of bridge
				nav.travelByTileSteps(x, y);
				// cross bridge
				nav.travelToTile(Tests.bridgeLocation_LL[0] - 1, Tests.bridgeLocation_LL[1]);
				// Localize
				ll.Localize(false);
			}
			// if we approach the bridge at LL
			else {
				double x = Tests.bridgeLocation_LL[0];
				double y = Tests.bridgeLocation_LL[1];
				// travel to front of bridge
				nav.travelByTileSteps(x - 1, y);
				// cross bridge
				nav.travelToTile(Tests.bridgeLocation_UR[0], Tests.bridgeLocation_UR[1] - 1);
				// Localize
				ll.Localize(false);
			}
		}

		finished = true;

	}

	public void travelToTunnel() {
		// tunnel is parallel to the y axis
		if (Math.abs(Tests.tunnelLocation_LL[0] - Tests.tunnelLocation_UR[0]) < 2) {
			// if we approach the tunnel at LL
			if (LLnearestPointY(Tests.tunnelLocation_UR, Tests.tunnelLocation_LL, true)) {
				double x = Tests.tunnelLocation_LL[0];
				double y = Tests.tunnelLocation_LL[1];
				// Travel to front of tunnel
				nav.travelByTileSteps(x, y - 1);
				// Localize
				ll.Localize(false);
				// wait for light localizer to finish
				while (!ll.finished) {
				}
				// Cross tunnel
				nav.travelToTile(Tests.tunnelLocation_UR[0] - 1, Tests.tunnelLocation_UR[1]);
			}
			// if we approach the tunnel at UR
			else {
				double x = Tests.tunnelLocation_UR[0];
				double y = Tests.tunnelLocation_UR[1];
				x = x - 1;
				// travel to front of tunnel
				nav.travelByTileSteps(x, y);
				// Localize
				ll.Localize(false);
				// wait for light localizer to finish
				while (!ll.finished) {
				}
				// cross bridge
				nav.travelToTile(Tests.tunnelLocation_LL[0], Tests.tunnelLocation_LL[1] - 1);
			}
		}
		// tunnel is parallel to the x axis
		else {
			// if we approach the tunnel at LL
			if (LLnearestPointY(Tests.tunnelLocation_UR, Tests.tunnelLocation_LL, false)) {
				double x = Tests.tunnelLocation_LL[0];
				double y = Tests.tunnelLocation_LL[1];
				// Travel to front of tunnel
				nav.travelByTileSteps(x - 1, y);
				// Localize
				ll.Localize(false);
				// wait for light localizer to finish
				while (!ll.finished) {
				}
				// Cross tunnel
				nav.travelToTile(Tests.tunnelLocation_UR[0], Tests.tunnelLocation_UR[1] - 1);
			}
			// if we approach the tunnel at UR
			else {
				double x = Tests.tunnelLocation_UR[0];
				double y = Tests.tunnelLocation_UR[1];
				y = y - 1;
				// travel to front of tunnel
				nav.travelByTileSteps(x, y);
				// Localize
				ll.Localize(false);
				// wait for light localizer to finish
				while (!ll.finished) {
				}
				// cross bridge
				nav.travelToTile(Tests.tunnelLocation_LL[0] - 1, Tests.tunnelLocation_LL[1]);
			}
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
	private boolean LLnearestPointY(double[] UR, double[] LL, boolean Yaxis) {

		double odoX = odo.getXYT()[0];
		double odoY = odo.getXYT()[1];

		// compute x and y components
		double xUR;
		double yUR;
		if (Yaxis) {
			xUR = Math.abs(odoX - (UR[0] - 1) * 30.48);
			yUR = Math.abs(odoY - UR[1] * 30.48);
		} else {
			xUR = Math.abs(odoX - (UR[0]) * 30.48);
			yUR = Math.abs(odoY - (UR[1] - 1) * 30.48);
		}
		double xLL = Math.abs(odoX - LL[0] * 30.48);
		double yLL = Math.abs(odoY - LL[1] * 30.48);
		// compute hypotenuses
		double distUR = Math.hypot(xUR, yUR);
		double distLL = Math.hypot(xLL, yLL);
		boolean b = distLL < distUR;
		return b;
	}

}