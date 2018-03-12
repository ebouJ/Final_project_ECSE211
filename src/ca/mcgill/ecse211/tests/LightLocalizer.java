package ca.mcgill.ecse211.tests;

/**
 * This class is used to perform light localization
 * 
 *
 */
public class LightLocalizer {

	Navigation nav;
	Odometer odo;
	private final double TILE_SIZE = 30.48;
	public boolean finished = false;
	private final double passLine = 15;

	public LightLocalizer(Navigation nav, Odometer odo) {
		this.nav = nav;
		this.odo = odo;
	}
	/**
	 * performs light localization 
	 * @param atStartPoint (true if at start point, false otherwise)
	 */
	public void Localize(boolean atStartPoint) {

		// correct y or x and theta
		Tests.correctionON = true;
		nav.move(passLine, false);
		Tests.correctionON = false;
		nav.move(-passLine, false);
		// turn towards y or x line
		if (Tests.startingCorner[1] < 1 && atStartPoint) {
			nav.turnTo(0);
		} else if (Tests.startingCorner[1] > 1 && atStartPoint) {
			nav.turnTo(180);
		}
		// turn 90 deg towards x or y line
		if (Tests.startingCorner[0] == Tests.startingCorner[1] && atStartPoint) {
			nav.turn(90);
		} else if (Tests.startingCorner[0] != Tests.startingCorner[1] && atStartPoint) {
			nav.turn(-90);
		} else if (odo.getXYT()[0] < TILE_SIZE) {
			//nav.turnTo(90);
			nav.turn(90);
		} else {
			nav.turn(-90);
			//nav.turnTo(270);
		}
		// correct x and theta
		Tests.correctionON = true;
		nav.move(passLine, false);
		Tests.correctionON = false;
		nav.move(-passLine, false);
		// turn to correct heading

		if (Tests.startingCorner[1] < 1 && atStartPoint) {
			nav.turnTo(0);
		} else if (Tests.startingCorner[1] > 1 && atStartPoint) {
			nav.turnTo(180);
		}
		finished = true;
	}

}
