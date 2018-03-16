package ca.mcgill.ecse211.tests;

/**
 * This class is responsible for making the robot travel to the bridge.
 */

public class Bridge {
	Navigation nav;
	Odometer odo;
	LightLocalizer ll;
	// private final double TILE_SIZE = 30.48;
	public boolean finished = false;

	/**
	 * Constructor for the Bridge class
	 * @param nav
	 * @param odo
	 * @param ll
	 */
	public Bridge(Navigation nav, Odometer odo, LightLocalizer ll) {
		this.nav = nav;
		this.odo = odo;
		this.ll = ll;
	}
	
	/**
	 * Responsible for making the robot travel to the bridge.
	 * @param x	the value of x
	 * @param y	the value of x
	 */
	//TODO : Still need to fix this (only works from +y direction)
	public void travelToBridge(double x, double y) {
		nav.travelByTileSteps(x - 2, y);
		nav.turnTo(0);
		ll.Localize(false);
		// wait for light localizer to finish
		while (!ll.finished) {
		}
		
		nav.travelByTileSteps(x - 1, y);
		finished = true;
	}

}
