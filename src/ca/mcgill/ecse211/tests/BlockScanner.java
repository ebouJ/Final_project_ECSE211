package ca.mcgill.ecse211.tests;

import java.util.HashMap;

import lejos.hardware.Sound;

/**
 * This class is responsible for scanning  for a block once the robot is in a search zone
 * @author remi
 *
 */
public class BlockScanner extends Thread {
	
	Navigation nav;
	UltrasonicPoller us;
	Odometer odo;
	public boolean scannedBlock = false;
	HashMap<Float,Double> map  = new HashMap<Float,Double>();
	private float smallestDistance = Float.MAX_VALUE;


	public BlockScanner(Navigation nav, UltrasonicPoller us, Odometer odo) {
		this.nav = nav;
		this.us = us;
		this.odo = odo;
	}
	/**
	 * Scan method
	 * 
	 */

	
	public boolean Scan() {
        // a timer to scan 90 degrees forward and backwards
		// put all the distances , and angles measured in the hashMap
		
		// spin 90 clockwise
		long endTimeMillis = System.currentTimeMillis() + 2900;
	    while (true) {
	        nav.spin(true);
	        float temp= us.getDistance();
	        map.put(temp, odo.getXYT()[2]);
	        smallestDistance = temp < smallestDistance ?  temp  : smallestDistance;
	        if (System.currentTimeMillis() > endTimeMillis) {
	            break;
	        }
	    }
		long endTimeMillisCW = System.currentTimeMillis() + 2900;
	    while (true) {
	        nav.spin(false);
	        float temp= us.getDistance();
	        map.put(temp, odo.getXYT()[2]);
	        smallestDistance = temp < smallestDistance ?  temp  : smallestDistance;
	        if (System.currentTimeMillis() > endTimeMillisCW) {
	            break;
	        }
	    }
	    
	 // spin 90 anti-clockwise
	    	    
	    // loop through the hashMap and get the smallest distance and the smallest angle

	    double smallestAngle = Double.MAX_VALUE;
	 // inefficient find a solution for it   
//	    for(float distance: map.keySet()) {
//	       if(distance < smallestDistance && distance != 0){
//	    	   		smallestDistance = distance;
//	       }
//	    }
	    smallestAngle = map.get(smallestDistance);
	    System.out.println("smallest distance is " + smallestDistance);
	    System.out.println("Smallest angle is " + smallestAngle );
         if(smallestDistance < 60f) {
        	 	Sound.beep();
        	 	nav.turnTo(smallestAngle);
        	 	nav.move(this.smallestDistance-4, false);
        	 	return true;
         }
    
         return false;
	}
	
	public float getDistance() {
		return this.smallestDistance;
	}
	

}
