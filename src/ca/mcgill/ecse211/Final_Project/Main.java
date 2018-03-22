package ca.mcgill.ecse211.Final_Project;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.ecse211.Final_Project.ColorIdentifier.BlockColor;
import ca.mcgill.ecse211.Final_Project.Tests.Start_Zone;
import ca.mcgill.ecse211.WiFiClient.WifiConnection;
import ca.mcgill.ecse211.navigation.BridgeTunnel;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import ca.mcgill.ecse211.sensor.BlockScanner;
import ca.mcgill.ecse211.sensor.LineDetector1;
import ca.mcgill.ecse211.sensor.LineDetector2;
import ca.mcgill.ecse211.sensor.UltrasonicLocalizer;
import ca.mcgill.ecse211.sensor.UltrasonicPoller;
import ca.mcgill.ecse211.sensor.UltrasonicLocalizer.State;
import ca.mcgill.ecse211.tests.OdoTestTrack;
import ca.mcgill.ecse211.tests.BlockDisplay;
import ca.mcgill.ecse211.tests.Display;
import ca.mcgill.ecse211.tests.LightLocalizer;
import ca.mcgill.ecse211.tests.OdoTestRadius;
import ca.mcgill.ecse211.tests.OdoTestSquare;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.MedianFilter;

public class Main {
	
	
	  private static final String SERVER_IP = "192.168.2.26"; 
	  private static final int TEAM_NUMBER = 2; 
	  private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
	 
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	
	private static final Port usPort = LocalEV3.get().getPort("S2");
	// Us data and filter for localization
	private static SampleProvider usSample;
	private static SampleProvider medianFilter;
	private static float[] usData;

	// RGB sensor port1
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	// RGB data and filter for line1 detection
	private static SampleProvider rgbSample;
	private static SampleProvider meanFilter;
	private static float[] RGBData;

	// RGB sensor port2
	private static final Port colorPort2 = LocalEV3.get().getPort("S3");
	// RGB data and filter for line2 detection
	private static SampleProvider rgbSample2;
	private static SampleProvider meanFilter2;
	private static float[] RGBData2;

	// Constants (Adjust these for better performance)
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 14.8;
  

	// Possible Starting Corners
	public static double startCorner; 
	
	// Starting corner for DEMO
	public static double[] startingCorner = new double[2];
	// Bridge and tunnel coodrdinates
	public static  final double[] bridgeLocation_UR = new double[2]; //{ 6, 5 };
	public static final double[] bridgeLocation_LL = new double[2];  // { 5, 3 };
	public static final double[] tunnelLocation_LL =  new double[2]; //{ 2, 3 };
	public static final double[] tunnelLocation_UR = new double[2];//{ 3, 5 };
	// Zone coordinates
	public static final double[] red_UR = new double[2];
	public static final double[] red_LL = new double[2];
	public static final double[] green_UR = new double[2];
	public static final double[] green_LL = new double[2];

	// Starting zone (Team)
	public static enum Start_Zone {
		Green_Zone, Red_Zone
	};
	
	 // Color of the zone
	static HashMap<Integer,String> colors = new HashMap<Integer,String>();
     
	
	// Set starting zone (for testing)
	public static Start_Zone startZone;
	// public static Start_Zone startZone = Start_Zone.Red_zone;

	// Target Block
	public static BlockColor tb;
	private static ColorIdentifier blockColorSensor;

	// Flag for odometry Correction
	public static boolean correctionON = false;

	public static void main(String[] args) {
		  System.out.println("Running");
		  // set the color mapping 
		  colors.put(1,"Red");
		  colors.put(2, "Blue");
		  colors.put(3,"Yellow");
		  colors.put(4, "White");
		 
		  WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER,ENABLE_DEBUG_WIFI_PRINT); 
		      try {
		  	  Map data = conn.getData();
		      int redTeam = ((Long) data.get("RedTeam")).intValue();
		      int greenTeam = ((Long) data.get("GreenTeam")).intValue();
		      int greenOponentFlag = ((Long) data.get("OG")).intValue();
		      int redOponentFlag = ((Long) data.get("OR")).intValue();
		      // SET THE STARTZONE OF TEAM 2
		      // RedCorner
		      if(redTeam == 2) {
		    	  	startZone = Start_Zone.Red_Zone;
		    	  	assignBlockColor(colors.get(redOponentFlag));
		    	  	startCorner = ((Long) data.get("RedCorner")).intValue();
		      }else if(greenTeam == 2){
		    	  	startZone = Start_Zone.Green_Zone;
		    	  	assignBlockColor(colors.get(greenOponentFlag));
		    	  	startCorner = ((Long) data.get("GreenCorner")).intValue();
		      }
		      
		      // assign the starting coordinate 
		      assignStartingCoordinate(startCorner);
		      
		      // set the bridge coordinate 
		      // TODO check if all the coordinates are within the range of the board
		      // for  now I am assuming they are going to give the right inputs
		      bridgeLocation_UR[0] = ((Long) data.get("BR_UR_x")).intValue();
		      bridgeLocation_UR[1] = ((Long) data.get("BR_UR_y")).intValue();
		      bridgeLocation_LL[0] = ((Long) data.get("BR_LL_x")).intValue();
		      bridgeLocation_LL[1] = ((Long) data.get("BR_LL_y")).intValue();
		      tunnelLocation_LL[0] = ((Long) data.get("TN_LL_x")).intValue();
		      tunnelLocation_LL[1] = ((Long) data.get("TN_LL_y")).intValue();
		      tunnelLocation_UR[0] = ((Long) data.get("TN_UR_x")).intValue();
		      tunnelLocation_UR[0] = ((Long) data.get("TN_UR_y")).intValue();

		    } catch (Exception e) {
		      System.err.println("Error: " + e.getMessage());
		    }
		  
		  
		  
	}
	private static void assignBlockColor(String color) {
		if(color == "Red") {
		    tb = BlockColor.RED;
		}else if(color == "Blue") {
			tb = BlockColor.BLUE;
		} else if(color == "Yellow") {
			tb = BlockColor.YELLOW;
		}else if(color == "White") {
			tb = BlockColor.WHITE;
		}
		
	}
	private static void assignStartingCoordinate(double startCorner2) {
		if (startCorner2 == 0) {
		startingCorner[0] = 0;
		startingCorner[1] = 0;
		} else if (startCorner2 == 1) {
		startingCorner[0] = 8;
		startingCorner[1] = 0;
		} else if (startCorner2 == 2) {
		startingCorner[0] = 8;
		startingCorner[1] = 8;
		} else if (startCorner2 == 3) {
		startingCorner[0] = 0;
		startingCorner[1] = 8;
		}
	}

}
