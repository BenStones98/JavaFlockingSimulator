package boid;

import drawing.Canvas;
/**
 * @author Y3848937
 */
public class StaticPredatorBoid extends Boid implements StaticBoid {
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
	public StaticPredatorBoid(Canvas canvas, double xpos, double ypos) {
		super(canvas, xpos, ypos);
	}
	
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * Draws the square shape of the static predator
	 */
	public void draw() {
		putPenDown();
		turn(90);
		move(25);
		turn(90);
		move(25);
		turn(90);
		move(25);
		turn(90);
		move(25);
	}
	/**
	 * Undraws the square shape by removing the last 4 lines
	 */
	public void undraw() {
		myCanvas.removeMostRecentLine();
		myCanvas.removeMostRecentLine();
		myCanvas.removeMostRecentLine();
		myCanvas.removeMostRecentLine();
		myCanvas.updateUI();
	}
}
