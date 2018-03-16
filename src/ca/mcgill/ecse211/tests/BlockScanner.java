package ca.mcgill.ecse211.tests;

import java.util.HashMap;

import lejos.hardware.Sound;

/**
 * Scans for a block once the robot is in a search zone
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
		long endTimeMillis = System.currentTimeMillis() + 4000;
	    while (true) {
	        nav.spin(true);
	        map.put(us.getDistance(), odo.getXYT()[2]);
	        System.out.println(map);
	        if (System.currentTimeMillis() > endTimeMillis) {
	            break;
	        }
	    }
	    
	 // spin 90 anti-clockwise
	    
		long endTimeMillisCW = System.currentTimeMillis() + 4000;
	    while (true) {
	        nav.spin(false);
	        map.put(us.getDistance(), odo.getXYT()[2]);
	        System.out.println(map);
	        if (System.currentTimeMillis() > endTimeMillisCW) {
	            break;
	        }
	    }
	    
	    // loop through the hashMap and get the smallest distance and the smallest angle

	    double smallestAngle = Double.MAX_VALUE;
	    
	    for(float distance: map.keySet()) {
	       if(distance < smallestDistance){
	    	   		smallestDistance = distance;
	       }
	    }
	    smallestAngle = map.get(smallestDistance);
         if(smallestDistance < 30f) {
        	 	nav.turnTo(smallestAngle);
        	 	return true;
         }
    
         return false;
	}
	
	public float getDistance() {
		return this.smallestDistance;
	}
	

}
