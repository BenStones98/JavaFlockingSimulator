package geometry;

public class CartesianCoordinate {
	private double xPosition;
	private double yPosition;
	public CartesianCoordinate(double x, double y) {
	 this.xPosition = x;
	 this.yPosition = y; 
	}
	
	 public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public double getX(){
		 return xPosition;	 
	 }
	 
	 public double getY(){
		 return yPosition;	 
	 }
}

