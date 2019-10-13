package boid;

import drawing.Canvas;
/**
 * @author Y3848937
 */
public class FoodBoid extends Boid implements StaticBoid{
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
	public FoodBoid(Canvas canvas, double xpos, double ypos) {
		super(canvas, xpos, ypos);
		draw();
	}
    // **************************************************
    // Public methods
    // **************************************************
	/**
	 * Draws the circular shape of the food
	 */
	public void draw() {
		//Line is drawn as angle updates to 360 to complete a full circle
		for (int i = 0; i <= 359; i++) {
			putPenDown();
			move(0.4);
			turn(1);
		}
		//myCanvas.updateUI();
	}
	/**
	 * Undraws the circular shape of the food
	 */
	public void undraw() {
		for (int i = 0; i <= 359; i++) {
			myCanvas.removeMostRecentLine();
		}
		//updateUI added to fix bug which prevented multiple foods being removed
		myCanvas.updateUI();
	}
	
	
}