package geometry;
/**
 * @author Y3848937
 */
public class DirectedCoordinate extends CartesianCoordinate{
    // **************************************************
    // Fields
    // **************************************************
	private double angle;
    // **************************************************
    // Constructors
    // **************************************************
    /**
    * Default constructor to create and initialise a DirectedCoordinate
    *
    * @param x The x value of the coordinate
    * @param y The y value of the coordinate
    * @param Angle The anglle associated with the coordinate 
    */
	public DirectedCoordinate(double x, double y, double Angle) {
		super(x, y);
		angle = Angle;
	}
    // **************************************************
    // Getters/Setters
    // **************************************************
	public double getAngleDegrees() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

}
