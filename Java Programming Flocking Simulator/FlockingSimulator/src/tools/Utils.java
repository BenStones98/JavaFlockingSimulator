package tools;

import java.lang.Thread;
/**
 * @author Y3848937
 */

/**
 * CLASS CONTAINING UTILITY METHODS AND DEFINITIONS
 */

public class Utils {
	
	
    // **************************************************
    // Definitions
    // **************************************************
	public static final int SCREEN_X_SIZE = 1000;
	public static final int SCREEN_Y_SIZE = 700;
	
    // **************************************************
    // Methods
    // **************************************************
	/**
	 * Sleep method, used in the game loop 
	 * 
	 * UNCHANGED FROM LABS
	 */
	public static void pause(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// We are happy with interruptions, so do not report exception
			Thread.currentThread().interrupt();
		}
	}
	/**
	 * Limits an angle between -180 and +180 degrees
	 * 
	 * @param angle The angle to limit
	 * 
	 * @return angle The reformatted angle
	 */
	public static double checkAngle(double angle) {
		
		while (angle > 180) {
			angle -= 360;
		}
		while (angle < -179) {
			angle += 360;
		}
		return angle;
	}
	}