package ca.mcgill.ecse211.tests;

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
		// bridge is facing y axis
		if (Math.abs(Tests.bridgeLocation_LL[0] - Tests.bridgeLocation_UR[0]) < 2) {
			double x = Tests.bridgeLocation_UR[0];
			double y = Tests.bridgeLocation_UR[1];
			x = x - 1;
			//nav.travelByTileSteps(x, y + 1);
			//nav.turnTo(180);

			nav.travelByTileSteps(x, y);

			nav.turnTo(180);
			nav.travelToTile(Tests.bridgeLocation_LL[0], Tests.bridgeLocation_LL[1] -1);
			ll.Localize(false);
		} else {

		}

		finished = true;

	}

	public void travelToTunnel() {
		// tunnel is facing y axis
		if (Math.abs(Tests.tunnelLocation_LL[0] - Tests.tunnelLocation_UR[0]) < 2) {
			double x = Tests.tunnelLocation_LL[0];
			double y = Tests.tunnelLocation_LL[1];
			nav.travelByTileSteps(x, y - 1);
			//nav.turnTo(0);
			ll.Localize(false);
			// wait for light localizer to finish
			while (!ll.finished) {
			}
			//nav.travelToTile(x, y-2);
			//nav.travelByTileSteps(x, y - 1);
			//nav.turnTo(0);
			nav.travelToTile(Tests.tunnelLocation_UR[0] - 1, Tests.tunnelLocation_UR[1]);
		}

		finished = true;

	}

}
