package boid;

import java.util.List;
import java.util.Random;

import drawing.Canvas;
import geometry.DirectedCoordinate;
import tools.Utils;

/**
 * @author Y3848937
 */

public class FlockingBoid extends DynamicBoid {
	// **************************************************
	// Fields
	// **************************************************
	private double cohesionControl;
	private double separationControl;
	private double alignmentControl;
	public boolean eaten = false;
	private static Random randomGenerator = new Random();
	private double maxAngle = 3;
	float maxAngleDelta = 6;
	
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
	public FlockingBoid(Canvas canvas, double xpos, double ypos) {
		super(canvas, xpos, ypos);
		this.size = 5;
		//On entry, a random starting angle is calculated
		setAngleDegrees(randomGenerator.nextInt(360));
	}
	
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * Method to control the flocking interactions between other boids
	 * 
	 * @param allBoids array containing all the nearby boids within the search radius
	 */
	public void flock(List<Boid> allBoids) {

		//Useful References: 
		// 1. 0 degrees is down
		// 2. 90 degrees is right
		// 3. Math.atan2(0, 1) is down
		// 4. Math.atan2(1, 0) is right
		
		List<Boid> nearbyBoids = findNearbyBoids(allBoids, this.radius);
		if (nearbyBoids.size() > 0) {
			
			DirectedCoordinate nearbyAverages = calculateNearbyAverages(nearbyBoids);
			double cohesionAngle = calculateCohesionAngle(nearbyAverages);
			double distToCenter = computeDist(nearbyAverages.getX(), nearbyAverages.getY());
			double scaledDist = distToCenter / radius;
			
			//Scaler used to control the amount of cohesion depending on the distance to the centre of the flock
			scaledDist = Math.pow(scaledDist, 2);
			
			if (predatorCheck(nearbyBoids))
			{
				//Any nearby predators cause separation
				this.turn(0.1*Utils.checkAngle(cohesionAngle + 180));
			}
			else if (foodCheck(nearbyBoids)) 
			{
				//Any nearby food causes cohesion on the food
				this.turn(0.3*Utils.checkAngle(cohesionAngle));
			}
			else {
				//The current angle of the boid has to be taken into account when implementing alignment 
				double origAngle = this.getAngleDegrees();
				//Alignment, separation and cohesion implemented into one angle, resulting in smoother movement
				double angleDelta = cohesionControl * cohesionAngle * scaledDist
						+ separationControl * Utils.checkAngle(cohesionAngle + 180) * (1 - scaledDist)
						+ alignmentControl * (nearbyAverages.getAngleDegrees() - origAngle);
				// 	Clamp change in angle per frame to avoid "shakiness" 
				angleDelta = Math.min(Math.max(angleDelta, -maxAngleDelta), maxAngleDelta);
				this.turn(angleDelta);
			} // end if
		} // end if
	}
	
	/**
	 * Method to control the behaviour of boids around predators
	 * 
	 * @param allBoids array containing all the nearby boids within the search
	 *                   radius
	 */
	public void flee(List<Boid> allBoids) {
		double tempX;
		double tempY;
		double angleToPredator = 0;

		
		for (Boid boidToCheck : allBoids) {
			if (boidToCheck.getClass() == PredatorBoid.class || boidToCheck.getClass() == StaticPredatorBoid.class) {
				//If a predator is identified, the angle to turn to face it is calculated
				tempX = boidToCheck.getLocation().getX() - this.getLocation().getX();
				tempY = boidToCheck.getLocation().getY() - this.getLocation().getY();
				double tempAngle = Math.toDegrees(Math.atan2(tempX, tempY) - this.getAngleRadians());
				//Multiple predator angles are accumulated, resulting in an angle facing the average position of the predators
				angleToPredator += tempAngle;
			} // end if
		} // end for
		
		//If any predators are found, the flocking boids will turn away from the direction of them 
		if (angleToPredator > 0) {
			this.turn(angleToPredator + 180);
		}
	}
	
	/**
	 * Update functions controls the random movement of the flocking boids 
	 * 
	 * @param time Uses the deltaTime value to control the movement of the boids
	 *                   
	 */
	@Override
	public void update(int time) {
		{
			// add random motion
			double angle = randomGenerator.nextDouble() * 2 * maxAngle - maxAngle;
			this.turn(angle);
		}
		//Boid is moved forward using the DynamicBoid's update function
		super.update(time);
	}
	
    // **************************************************
    // Private  methods
    // **************************************************
	/**
	 * Calculates the average values of neary boids
	 * 
	 * @param nearbyBoids boid list containing those inside the search radius
	 * 
	 * @return the average xPosition, yPosition and angle of boids in the list in the form of a
	 * 		   DirectedCoordinate
	 */
	private DirectedCoordinate calculateNearbyAverages(List<Boid> nearbyBoids) {
		double totalX = 0;
		double totalY = 0;
		double totalAngle = 0;
		
		for (Boid boid : nearbyBoids) {
			totalX += boid.getLocation().getX();
			totalY += boid.getLocation().getY();
			totalAngle += Utils.checkAngle(boid.getAngleDegrees());
		} // end for
		
		double aveXPos = totalX / nearbyBoids.size();
		double aveYPos = totalY / nearbyBoids.size();
		double aveAngle = totalAngle / nearbyBoids.size();
		return new DirectedCoordinate((aveXPos), (aveYPos), aveAngle);
	}
	/**
	 * Calculates the cohesion angle
	 * 
	 * @param nearbyBoids boid list containing those inside the search radius
	 * 
	 * @return the average xPosition, yPosition and angle of boids in the list in the form of a
	 * 		   DirectedCoordinate
	 */
	private double calculateCohesionAngle(DirectedCoordinate averages) {
		double xPart = averages.getX() - this.getLocation().getX();
		double yPart = averages.getY() - this.getLocation().getY();

		double TurnAngle = Math.toDegrees(Math.atan2(xPart, yPart) - this.getAngleRadians());

		return Utils.checkAngle(TurnAngle);
	}
	
	/**
	 * Flags any predators from a list of boids
	 * 
	 * @param allBoids boid list to check for predators
	 */
	private boolean predatorCheck(List<Boid> allBoids) {
		boolean found = false;
		for (Boid boidToCheckToCheck : allBoids) {
			if (boidToCheckToCheck.getClass() == PredatorBoid.class || boidToCheckToCheck.getClass() == StaticPredatorBoid.class) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Flags any food from a list of boids
	 * 
	 * @param allBoids boid list to check for food
	 */
	private boolean foodCheck(List<Boid> allBoids) {
		boolean found = false;
		for (Boid boidToCheck : allBoids) {
			if (boidToCheck.getClass() == FoodBoid.class) {
				found = true;
				break;
			}
		}
		return found;
	}

    // **************************************************
    // Getters/setters 
    // **************************************************
	public double getAlignmentControl() {
		return alignmentControl;
	}
	public boolean isEaten() {
		return eaten;
	}
	public double getSeparationControl() {
		return separationControl;
	}
	public double getcohesionControl() {
		return cohesionControl;
	}
	public void setAlignmentControl(double alignment) {
		alignmentControl = alignment;
	}
	public void setcohesionControl(double cohesion) {
		cohesionControl = cohesion;
	}
	public void setSeparationControl(double separationControl) {
		this.separationControl = separationControl;
	}
	public void setEaten(boolean eaten) {
		this.eaten = eaten;
	}
}

	











