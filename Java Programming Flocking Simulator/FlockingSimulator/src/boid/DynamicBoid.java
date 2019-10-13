package boid;

import java.util.ArrayList;
import drawing.Canvas;

/**
 * @author Y3848937
 */

public abstract class DynamicBoid extends Boid {
	// **************************************************
	// Fields
	// **************************************************
	private int speed = 100;
	
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
	public DynamicBoid(Canvas canvas, double xpos, double ypos) {
		super(canvas, xpos, ypos);
	}
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * Draws the triangular shape used by dynamic Boids
	 */
	public void draw() {		
		
		//Current angle of the boid needs to be saved to be reinstantiated after it is drawn
		double angle = getAngleRadians();	
		
		//boid is drawn from the tip of the triangle
		turn(150);
		putPenDown();
		//Size scaler used to determine the lengths of the sides
		move(size);
		turn(120);
		putPenUp();
		move(size);
		turn(120);
		putPenDown();
		move(size);
		turn(-30);
		putPenUp();
		setAngleRadians(angle);
	}

	/**
	 * Undraws the boid
	 */
	public void undraw() {
		//The last 2 lines on the canvas are removed
		myCanvas.removeMostRecentLine();
		myCanvas.removeMostRecentLine();
		myCanvas.updateUI();
	}
	
	/**
	 * Moves the boid on the screen
	 * 
	 * @param time  Uses the deltaTime value to control the movement of the boids
	 */
	public void update(int time) {
		//Distance = Speed*time (using deltatime and the given speed of the boid)
		double distance = (double) (getSpeed() * (time * 0.001));
		move(distance);
		this.screenBuffer();
	}

    // **************************************************
    // Getters/setters 
    // **************************************************
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
    // **************************************************
    // Getters/setters to be overridden 
    // **************************************************
	public void flock(ArrayList<Boid> allBoids) {

	}
	public void setAlignmentControl(double alignment) {
	}
	public double getAlignmentControl() {
		return 0;
	}
	public boolean isEaten() {
		return false;
	}
	public void setEaten(boolean eaten) {
	}
}
