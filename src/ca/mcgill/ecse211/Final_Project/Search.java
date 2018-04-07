package ca.mcgill.ecse211.Final_Project;

import java.util.HashMap;
import java.util.Map;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.sensor.BlockScanner;
import lejos.hardware.Sound;

/**
 * 
 * This class is responsible for searching.
 */

public class Search extends Thread {

	private Navigation nav;
	private Odometer odometer;
	private ColorIdentifier rgb;
	private BlockScanner bs;
	int searchIndexY = 0;
	int searchIndexX = 0;
	double currentY = Tests.SR_LL[1];
	double currentX = Tests.SR_LL[0];
	boolean firstScan = true;
	boolean goDown = false;
	public static boolean isFinished = false;
	boolean fuckOffScan = false;
	int lastHeading = 0;

	int counter = 0;
	private Object lock1 = new Object();
	private Object lock2 = new Object();

	/**
	 * Enum of all the states of the robot functionalities.
	 */
	public enum State {
		INIT, SEARCHING, FOUNDBLOCK, FINISHED
	}

	private State state = State.INIT;

	public Search(Navigation nav, ColorIdentifier rgb, BlockScanner bs) {
		this.nav = nav;
		this.rgb = rgb;
		this.bs = bs;
	}

	public void run() {
		while (true) {
			switch (state) {
			case INIT:
				state = state_init();
				break;
			case SEARCHING:
				state = state_searching();
				break;
			case FOUNDBLOCK:
				state = state_foundblock();
				break;
			case FINISHED:
				state_finished();
				break;
			default:
				break;
			}
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
		}
	}

	private State state_init() {
		return State.SEARCHING;
	}

	private State state_searching() {
		// remember the heading before the scan
		lastHeading = nav.getHeading();
		// Call search
		if (firstScan) {
			goToFirstPoint();
			firstScan = false;
		}
		if (bs.Scan()) {
			// if (fuckOffScan) { 
			nav.stop(false);
			return State.FOUNDBLOCK;
		}
		// No block was found
		else {
			goToNextPoint();
			return State.SEARCHING;
		}

	}
	
	private State state_foundblock() {
		// Check if block is the target
		synchronized (lock1) {
			if (isTargetBlock() == 1) {
				return State.FINISHED;
			} else if (isTargetBlock() == 2) {
				return State.SEARCHING;
			}
			// Rotate to double check
			else {
				// Rotate to double check
				nav.rotate(12.5, false);
				sleepThread(100);
				nav.rotate(12.5, false);
				sleepThread(100);
				if (isTargetBlock() == 1) {
					return State.FINISHED;
				} else if (isTargetBlock() == 2) {
					return State.SEARCHING;
				}
				nav.rotate(-25, false);
				sleepThread(100);
				nav.rotate(-12.5, false);
				sleepThread(100);
				nav.rotate(-12.5, false);
				if (isTargetBlock() == 1) {
					return State.FINISHED;
				} else if (isTargetBlock() == 2) {
					return State.SEARCHING;
				}
				// Target was not found, keep searching
				goToNextPoint();
				//turnToRightHeading();
				//nav.turnTo(0); // we still need to correct this
				return State.SEARCHING;
			}
		}
	}
	/**
	 * determines if a block is the target block
	 * @return 1 if target block, 2 otherwise
	 */
	private int isTargetBlock() {
		int result = checkForBlocksColor();
		//if target block
		if (result == 1) {
			nav.moveBackward();
			return 1;
		} 
		// not target block
		else if (result == 2) {
			nav.moveBackward();
			goToNextPoint();
			//turnToRightHeading();
			//nav.turnTo(0);
			return 2;
		}
		return 3;
	}

	private void state_finished() {
		this.isFinished = true;
	}

	/**
	 * returns 1 if target block, 2 if non-target, 3 otherwise
	 * 
	 * @return int that indicates if block is found or not
	 */
	public int checkForBlocksColor() {
		if (rgb.getBlockColor().equals(Tests.tb)) {
			Sound.beep(); // target block is found
			return 1;
		} else if (!rgb.getBlockColor().equals(Tests.none)) {
			Sound.twoBeeps();
			return 2;
		} else {
			return 3;
		}
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	/**
	 * Takes the robot to the next point for searching
	 */
	private void goToNextPoint() {
		if (!goDown) {
			if (firstScan) {
				firstScan = false;
				// nav.travelToTile(currentX, currentY);
				nav.travelTo(currentX, currentY, true);
				nav.turnTo(0);
			}

			else if (Tests.SR_UR[1] > currentY) {
				currentY++;
				// nav.travelByTileSteps(currentX, currentY);
				nav.travelTo(currentX, currentY, true);
				nav.turnTo(0);
				if (Tests.SR_UR[1] == currentY) {
					nav.turnTo(90);
				}
			}

			else if (Tests.SR_UR[0] > currentX) {
				currentX++;
				// nav.travelByTileSteps(currentX, currentY);
				nav.travelTo(currentX, currentY, true);
				nav.turnTo(90);
				if (Tests.SR_UR[0] == currentX) {
					nav.turnTo(180);
				}
			}
			if (Tests.SR_UR[0] <= currentX) {
				goDown = true;
			}
		} else {
			goDown();
		}

	}

	private void goToFirstPoint() {
		// if LL is the nearest point
		nav.travelByTileSteps(Tests.SR_LL[0] - 1, Tests.SR_LL[0] - 1);
		nav.travelTo(Tests.SR_LL[0], Tests.SR_LL[0], true);
		nav.turnTo(0); // correct this
		// add UR if closer...
	}

	private void goDown() {

		if (Tests.SR_LL[1] < currentY) {
			currentY--;
			System.out.println(currentY);
			// nav.travelByTileSteps(currentX, currentY);
			nav.travelTo(currentX, currentY, true);
			nav.turnTo(180);
			if (Tests.SR_LL[1] == currentY) {
				nav.turnTo(270);
			}
		}

		else if (Tests.SR_LL[0] < currentX) {
			currentX--;
			// nav.travelByTileSteps(currentX, currentY);
			nav.travelTo(currentX, currentY, true);
			nav.turnTo(270);
		}
	}
	private void turnToRightHeading() {
		if (lastHeading == 1) {
			nav.turnTo(0);
		}
		else if (lastHeading == 2) {
			nav.turnTo(180);
		}
		else if (lastHeading == 3) {
			nav.turnTo(90);
		}
		else if (lastHeading == 4) {
			nav.turnTo(270);
		}
		
		
	}
	public void sleepThread(float milliseconds) {
		try {
			Thread.sleep((long) (milliseconds));
		} catch (Exception e) {
		}
	}
}