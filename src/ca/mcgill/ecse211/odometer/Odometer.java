package ca.mcgill.ecse211.odometer;


import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * 
 * This class is responsible for performing Odometery.
 *
 */

public class Odometer extends OdometerData implements Runnable {

  private OdometerData odoData;
  private static Odometer odo = null; // Returned as singleton

  // Motors and related variables
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  private int lastLeftMotorTachoCount;
  private int lastRightMotorTachoCount;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;

  private final double TRACK;
  private final double WHEEL_RAD;

  //private double[] position;
  
  private double dx, dy, dTheta, leftDistance, rightDistance, dDistance;


  private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It
   * cannot be accessed externally.
   * 
   * @param leftMotor 	An EV3 Regulated Motor
   * @param rightMotor	An EV3 Regulated Motor
   * @param TRACK		value of TRACK
   * @param	WHEEL_RAD	Value of the radius of the wheel
   * 
   * @throws OdometerExceptions
   */
  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
    odoData = OdometerData.getOdometerData(); // Allows access to x,y,z
                                              // manipulation methods
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;

    // Reset the values of x, y and z to 0
    odoData.setXYT(0, 0, 0);

    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;
    this.lastLeftMotorTachoCount = 0;
    this.lastRightMotorTachoCount = 0;

    this.TRACK = TRACK;
    this.WHEEL_RAD = WHEEL_RAD;

  }

  /**
   * This method is meant to ensure only one instance of the odometer is used throughout the code.
   * 
   * @param leftMotor 	An EV3 regulated motor
   * @param rightMotor	An EV3 regulated motor
   * @param TRACK		value of TRACK
   * @param	WHEEL_RAD	value of the radius of the wheel
   * 
   * @return new or existing Odometer Object
   * @throws OdometerExceptions
   */
  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
      throws OdometerExceptions {
    if (odo != null) { // Return existing object
      return odo;
    } else { // create object and return it
      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
      return odo;
    }
  }

  /**
   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
   * odometer object has been created
   * 
   * @return error if no previous odometer exists
   */
  public synchronized static Odometer getOdometer() throws OdometerExceptions {

    if (odo == null) {
      throw new OdometerExceptions("No previous Odometer exits.");

    }
    return odo;
  }

  /**
   * This method is where the logic for the odometer will run. Use the methods provided from the
   * OdometerData class to implement the odometer.
   */
  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();

      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();

      // TODO Calculate new robot position based on tachometer counts
      //Not sure to leave these in while loop
      //double dx, dy, dTheta, leftDistance, rightDistance, dDistance;
      
      //Calculate distance traveled by right and left Wheels
      rightDistance = WHEEL_RAD * Math.PI * (rightMotorTachoCount-lastRightMotorTachoCount)/180;
      leftDistance = WHEEL_RAD * Math.PI * (leftMotorTachoCount-lastLeftMotorTachoCount)/180;

      //update previous tacho counts
      lastRightMotorTachoCount = rightMotorTachoCount;
      lastLeftMotorTachoCount = leftMotorTachoCount;
      
      //Calculate distance traveled
      dDistance = 0.5 * (rightDistance + leftDistance);
      //Calculate change in heading
      dTheta = Math.toDegrees((leftDistance - rightDistance) / TRACK);
      
      //update heading
      odo.update(0, 0, dTheta);
      //calculate dx and dy
      dx = dDistance * Math.sin(Math.toRadians(odo.getXYT()[2]));
      dy = dDistance * Math.cos(Math.toRadians(odo.getXYT()[2]));
      
      // TODO Update odometer values with new calculated values
      //this updates the odometer. Theta = 0 because it was updated above
      odo.update(dx, dy, 0);
      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }

}

