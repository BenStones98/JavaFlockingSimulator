package boid;

import java.util.List;

import drawing.Canvas;
import tools.Utils;

/**
 * @author Y3848937
 */
public class PredatorBoid extends DynamicBoid{
    // **************************************************
    // Constructors
    // **************************************************
    /**
    * Default constructor to create and initialise a boid
    *
    * @param myCanvas the canvas to add the boid to
    * @param xPos the initial x position of the boid
    * @param yPos the initial y position of the boid
    */
	public PredatorBoid(Canvas canvas, double xpos, double ypos) {
		super(canvas, xpos, ypos);
		
		this.radius = 50; //Uses a fixed radius and speedto find prey
		this.setSpeed(150);
		this.size = 60;
	}
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * Main function to be called in the game loop to control hunting
	 * 
	 * @param allBoids list of Boids to pass to findPrey function
	 */
	public void hunt(List<Boid> allBoids) {
		double preyAngle = findPrey(allBoids);
		this.turn(preyAngle);
	}
    // **************************************************
    // Private methods
    // **************************************************
	/**
	 * Finds the prey 
	 * 
	 * @param allBoids list containing all boids to search for nearby prey
	 *
	 * @return the angle needed to turn to face the nearest flocking boid
	 */
	private double findPrey(List<Boid> allBoids) {
		double tempX;
		double tempY;
		//minDistance initialised to infinity so first value checked will always take its place
		double minDistance = Double.POSITIVE_INFINITY;
		double TurnAngle = 0;
		
		for (Boid boidToCheckCheck : allBoids) {
			if (boidToCheckCheck.getClass() == FlockingBoid.class) {
				tempX = boidToCheckCheck.getLocation().getX() - this.getLocation().getX();
				tempY = boidToCheckCheck.getLocation().getY() - this.getLocation().getY();
				double distanceSqrd = (tempX*tempX + tempY*tempY);
				//The absolute distance to each flocking boid is calculated and compared against the distance to the current nearest flocking boid
				if (distanceSqrd < minDistance)
				{
					minDistance = distanceSqrd;
					if (minDistance > 400)
					{ //angle to face the nearest flocking boid is calculated
					TurnAngle = Math.toDegrees(Math.atan2(tempX, tempY) - this.getAngleRadians());
					}
					else if (minDistance <= 400) 
					{
						//boid is set as eaten if within 20 pixels of the predator
						boidToCheckCheck.setEaten(true);
					}//end if
				}//end if
			}//end if
		}//end for
		return Utils.checkAngle(TurnAngle);
	}
}
