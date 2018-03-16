package ca.mcgill.ecse211.tests;

public class Bridge {
	Navigation nav;
	Odometer odo;
	LightLocalizer ll;
	// private final double TILE_SIZE = 30.48;
	public boolean finished = false;

	public Bridge(Navigation nav, Odometer odo, LightLocalizer ll) {
		this.nav = nav;
		this.odo = odo;
		this.ll = ll;
	}
	/**
	 * Still need to fix this (only works from +y direction)
	 * @param x
	 * @param y
	 */

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
