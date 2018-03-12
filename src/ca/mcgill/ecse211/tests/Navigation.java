
package ca.mcgill.ecse211.tests;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * 
 * This class Navigates the robot
 * 
 *
 */

public class Navigation {
	private static final int FORWARD_SPEED = 300;
	private static final int ROTATE_SPEED = 150;
	private static final double TILE_SIZE = 30.48;
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	double leftRadius, rightRadius;
	double width;
	Odometer odo;
	boolean isNavigating = false;

	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		this.odo = odo;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		leftMotor.setAcceleration(600);
		rightMotor.setAcceleration(600);
	}

	/**
	 * navigates the robot to a point
	 * 
	 * @param x
	 * @param y
	 * @param tileMultiple
	 *            multiplies x and y by tile size if true
	 */

	public void travelTo(double x, double y, boolean tileMultiple) {
		isNavigating = true;
		// Calculate distance from current position
		double hypotenuse = computeTargetHypot(x, y, tileMultiple);

		// Calculate theta (deg) towards target wrt to +y axis
		double targetTheta = computeTargetTheta(x, y, tileMultiple);

		// turn towards destination
		turn(targetTheta);

		// travel to target
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(leftRadius, hypotenuse), true);
		rightMotor.rotate(convertDistance(rightRadius, hypotenuse), false);

		isNavigating = false;

	}

	/**
	 * Travels to middle of specified tile, turns  on correction
	 * 
	 * @param x
	 * @param y
	 */
	public void travelToTile(double x, double y) {
		isNavigating = true;
		Tests.correctionON = true;

		// Calculate distance from current position
		double hypotenuse = computeTargetHypot(x + 0.5, y + 0.5, true);

		// Calculate theta (deg) towards target wrt to +y axis
		double targetTheta = computeTargetTheta(x + 0.5, y + 0.5, true);

		// turn towards destination
		turn(targetTheta);

		// travel to target
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(leftRadius, hypotenuse), true);
		rightMotor.rotate(convertDistance(rightRadius, hypotenuse), false);

		isNavigating = false;
		Tests.correctionON = false;
	}

	/**
	 * Travels in steps, tile by tile, to allow correction
	 * 
	 * @param x
	 * @param y
	 */
	public void travelByTileSteps(double x, double y) {

		if ((odo.getXYT()[1] / TILE_SIZE) <= y + 1 && (odo.getXYT()[0] / TILE_SIZE) <= x + 1) {
			for (int i = (int) (odo.getXYT()[1] / TILE_SIZE) + 1; i <= y; i++) {
				travelToTile(Math.floor(odo.getXYT()[0] / TILE_SIZE), i);
			}

			for (int i = (int) (odo.getXYT()[0] / TILE_SIZE) + 1; i <= x; i++) {
				travelToTile(i, Math.floor(odo.getXYT()[1] / TILE_SIZE));
			}
		}

		else if ((odo.getXYT()[1] / TILE_SIZE + 1) >= y && (odo.getXYT()[0] / TILE_SIZE + 1) >= x) {

			for (int i = (int) (odo.getXYT()[1] / TILE_SIZE) - 1; i >= y; i--) {
				travelToTile(Math.floor(odo.getXYT()[0] / TILE_SIZE), i);
			}

			for (int i = (int) (odo.getXYT()[0] / TILE_SIZE) - 1; i >= x; i--) {
				travelToTile(i, Math.floor(odo.getXYT()[1] / TILE_SIZE));
			}
		}

	}

	/**
	 * This method turns the robot angle theta (limits angle)
	 * 
	 * @param theta
	 */
	public void turn(double theta) {
		// limit to the minimum angle
		if (theta > 180.0) {
			theta = theta - 360.0;
		} else if (theta < -180.0) {
			theta = theta + 360.0;
		}
		// set motor speeds
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		// set amount to rotate
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	}

	/**
	 * Turns the robot Towards a target angle theta
	 * 
	 * @param theta
	 */
	public void turnTo(double theta) {
		theta = theta - odo.getXYT()[2];
		if (theta > 180.0) {
			theta = theta - 360.0;
		} else if (theta < -180.0) {
			theta = theta + 360.0;
		}
		// set motor speeds
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		// set amount to rotate
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);

	}

	/**
	 * This method turns the robot angle theta (without limiting)
	 * 
	 * @param theta
	 * @param returnEarly
	 *            returns early if true
	 */

	public void rotate(double theta, boolean returnEarly) {
		// set motor speeds and acceleration
		leftMotor.setAcceleration(350);
		rightMotor.setAcceleration(350);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		// rotate
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), returnEarly);

	}

	/**
	 * Makes the borbot spin indefintitely
	 * 
	 * @param clockwise
	 */
	public void spin(boolean clockwise) {
		leftMotor.setAcceleration(350);
		rightMotor.setAcceleration(350);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		if (clockwise) {
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.backward();
			rightMotor.forward();
		}

	}

	/**
	 * Moves the robot forward by dist
	 * 
	 * @param dist
	 *            (cm)
	 * @param returnEarly
	 *            returns early if true
	 */
	public void move(double dist, boolean returnEarly) {
		// set motor speeds and acceleration
		leftMotor.setAcceleration(600);
		rightMotor.setAcceleration(600);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		// move
		leftMotor.rotate(convertDistance(leftRadius, dist), true);
		rightMotor.rotate(convertDistance(rightRadius, dist), returnEarly);
	}

	/**
	 * moves robot forward until stop is called
	 */
	public void moveForward(int speed) {
		leftMotor.setAcceleration(600);
		rightMotor.setAcceleration(600);
		if (speed == 0) {
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
		} else {
			// leftMotor.stop();
			// rightMotor.stop();
			leftMotor.setSpeed(speed);
			rightMotor.setSpeed(speed);
			return;
		}

		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * Computes distance to target
	 * 
	 * @param x
	 * @param y
	 * @param tileMultiple
	 *            multiplies x and y by tileSize if true
	 * @return
	 */

	public double computeTargetHypot(double x, double y, boolean tileMultiple) {
		if (tileMultiple) {
			x = x * TILE_SIZE;
			y = y * TILE_SIZE;
		}

		x = x - odo.getXYT()[0]; // x distance
		y = y - odo.getXYT()[1]; // y distance
		double hypotenuse = Math.hypot(x, y); // distance to target
		return hypotenuse;
	}

	/**
	 * Computes theta to target
	 * 
	 * @param x
	 * @param y
	 * @param tileMultiple
	 *            multiplies x and y by tileSize if true
	 * @return
	 */
	public double computeTargetTheta(double x, double y, boolean tileMultiple) {
		if (tileMultiple) {
			x = x * TILE_SIZE;
			y = y * TILE_SIZE;
		}
		x = x - odo.getXYT()[0]; // x distance
		y = y - odo.getXYT()[1]; // y distance
		double theta = Math.toDegrees(Math.atan2(y, x)); // wrt to +x
		theta = 90 - theta; // wrt to +y
		theta = theta - odo.getXYT()[2];
		return theta;
	}

	public boolean isNavigating() {
		return isNavigating;
	}

	/**
	 * stops the robot
	 * 
	 * @return returns early if true
	 */

	public void stop(boolean returnEarly) {
		rightMotor.stop(true);
		leftMotor.stop(returnEarly);
	}

	/**
	 * ...
	 * 
	 * @param radius
	 * @param distance
	 * @return
	 */

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public void sleepThread(float seconds) {
		try {
			Thread.sleep((long) (seconds * 1000f));
		} catch (Exception e) {
		}
	}

}
