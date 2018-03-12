package ca.mcgill.ecse211.tests;

import ca.mcgill.ecse211.tests.ColorIdentifier.BlockColor;
import lejos.hardware.Sound;

public class Search extends Thread {

	private final double TILE_SIZE = 30.48;
	private Navigation nav;
	private Odometer odometer;
	private ColorIdentifier rgb;
	int counter = 0;
	boolean detect = false;

	public enum State {
		INIT, SEARCHING, FOUNDBLOCK, FOUNDTARGET, FINISHED
	}

	private State state = State.INIT;

	public Search(Odometer odo, Navigation nav, ColorIdentifier rgb) {
		this.nav = nav;
		this.odometer = odo;
		this.rgb = rgb;
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
			case FOUNDTARGET:
				state = state_foundTarget();
				break;
			case FINISHED:
				state = state_finished();
				break;
			default:
				break;
			}
		}
	}

	private State state_init() {
		return State.SEARCHING;
	}

	private State state_searching() {
		return State.SEARCHING;
	}

	private State state_foundblock() {
		nav.stop(true);
		if (checkForBlocksColor() == 1) {
			return State.FOUNDTARGET;
		} else {
			nav.turn(90);
			nav.move(15, false);
			nav.turn(90);
			nav.move(15, false);
			nav.turn(90);
			nav.move(15, false);
			return State.SEARCHING;
		}
	}

	private State state_foundTarget() {
		return State.FINISHED;
	}

	private State state_finished() {
		return null;
	}
	/**
	 * returns 1 if target block, 2 if non-target, 3 otherwise
	 * @return
	 */
	public int checkForBlocksColor() {
		if (rgb.getBlockDetected()) {
			System.out.println("Object detected! " + rgb.getBlockColor());
			if (rgb.getBlockColor().equals(Tests.tb)) {
				Sound.beep(); // target block is found
				return 1;
			} else {
				Sound.twoBeeps();// non target block is found
				return 2;
			}
		}
		return 3;
	}

}