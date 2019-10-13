package boid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import tools.Utils;

/**
 * @author Y3848937
 */

public abstract class Boid {
    // **************************************************
    // Fields
    // **************************************************
	static int lastId = 0;
	public final int id; //Each boid assigned unique ID - used for debugging (incremented from lastId)
	protected Canvas myCanvas;
	protected CartesianCoordinate location;
	protected double angleRadians;
	private boolean isPenDown;
	protected int size;
	protected int radius;
	
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
	
	public Boid(Canvas myCanvas, double xPos, double yPos) {
		this.myCanvas = myCanvas;
		this.location = new CartesianCoordinate(xPos, yPos);
		this.angleRadians = 0;
		this.isPenDown = false;
		this.size = 20;
		this.radius = 100;
		id = ++lastId; 
	}

	
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * The boid is moved in its current direction for the given number of pixels.
	 * If the pen is down when the robot moves, a line will be drawn on the floor.
	 * 
	 * @param i The number of pixels to move.
	 */
	public void move(double i) {
		double x = location.getX() + (i * Math.sin(angleRadians));
		double y = location.getY() + (i * Math.cos(angleRadians));
		CartesianCoordinate currentPos = new CartesianCoordinate(x, y);
		if (isPenDown) {
			myCanvas.drawLineBetweenPoints(currentPos, location);
		}

		location = currentPos;
	}
	
	/**
	 * Calculates the absolute distance between a boid and a cooridnate
	 * 
	 * @param x component of the absolute distance in the x direction 
	 * @param y component of the absolute distance in the y direction 
	 * 
	 * @return calculatedDistance the direct distance between the boid and the coordinate 
	 */
	public double computeDist(double x, double y) {
		double xDistance = x - this.getLocation().getX();
		double yDistance = y - this.getLocation().getY();
		double calculatedDistance =  Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
		return calculatedDistance;
	}
	
	/**
	 * Rotates the boid clockwise by the specified angle in degrees.
	 * 
	 * @param i The number of degrees to turn.
	 */
	public void turn(double i) {
		angleRadians += Math.toRadians(i);
	}

	/**
	 * Moves the pen off the canvas so that the boid's route isn’t drawn for any
	 * subsequent movements.
	 */
	public void putPenUp() {
		this.isPenDown = false;
	}

	/**
	 * Lowers the pen onto the canvas so that the boid's route is drawn.
	 */
	public void putPenDown() {
		this.isPenDown = true;
	}
	
	
    // **************************************************
    // Protected methods
    // **************************************************
	/**
	 * For both x and y components:
	 * 		-if max position is exceeded, the boids coordinates are reset to the min position
	 * 		-if min position is exceeded, the boids coordinates are reset to the max position	
	 * 
	 */
	protected void screenBuffer() {
	
		if (location.getX() > Utils.SCREEN_X_SIZE) {
			location.setxPosition(0);
		} else if (location.getX() < 0) {
			location.setxPosition(Utils.SCREEN_X_SIZE);
		}
		
		if (location.getY() > Utils.SCREEN_Y_SIZE) {
			location.setyPosition(0);
		} else if (location.getY() < 0) {
			location.setyPosition(Utils.SCREEN_Y_SIZE);
		}
	}
	
	/**
	 * Finds all boids whose locations are within the search radius
	 * 
	 * @param allBoids boid object array to fill with those nearby
	 * @param radius the radius in which to search for boids
	 * @return allboids containing boids within the radius
	 */
	protected ArrayList<Boid> findNearbyBoids(List<Boid> allBoids, int radius) {
		HashSet<Boid> nearbyBoids = new HashSet<Boid>();
		for (Boid boidToCheck : allBoids) {
			if (boidToCheck != this) {
				//Sqrt function avoided when checking distance to increase efficiency of code
				double xDistance = boidToCheck.getLocation().getX() - this.getLocation().getX();
				double yDistance = boidToCheck.getLocation().getY() - this.getLocation().getY();
				double absDistanceSq =(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
				if (absDistanceSq < radius*radius) {
					nearbyBoids.add(boidToCheck);
				}
			}
		}
		if (nearbyBoids.size() > 0) {
			// add this
			nearbyBoids.add(this);
		}
		return new ArrayList<Boid>(nearbyBoids);
	}
	
    // **************************************************
    // Protected methods
    // **************************************************
	public abstract void draw();
	
	public abstract void undraw();
	
    // **************************************************
    // Getters/setters 
    // **************************************************
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setAngleRadians(double angle) {
		this.angleRadians = angle;
	}
	
	public void setAngleDegrees(double deg) {
		this.angleRadians = Math.toRadians(deg);
	}

	public CartesianCoordinate getLocation() {
		return location;
	}

	public double getAngleRadians() {
		return angleRadians;
	}

	public double getAngleDegrees() {
		return Math.toDegrees(angleRadians);
	}

	public int getRadius() {
		return radius;
	}
	
	public int getId() {
		return id;
	}
	
    // **************************************************
    // Methods to be overridden (Not used by all child classes)
    // **************************************************
	
	public void update(int time) {
		
	}
	
	public void flock(List<Boid> allBoids) {
	}

	public void hunt(List<Boid> allBoids) {
	}
	
	public void flee(List<Boid> allBoids) {
	}
	
	
    // **************************************************
    // Getters/setters to be overridden
    // **************************************************

	public void setcohesionControl(double cohesion) {
	}

	public void setAlignmentControl(double alignment) {
	}
	public void setEaten(boolean eaten) {
	}

	public void setSeparationControl(double separationControl) {
	}
	public double getcohesionControl() {
		return 0;
	}
	public double getSeparationControl() {
		return 0;
	}
	public double getAlignmentControl() {
		return 0;
	}
	public boolean isEaten() {
		return false;
	}
	public void setSpeed(int i) {		
	}


}