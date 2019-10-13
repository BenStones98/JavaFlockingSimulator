package geometry;
import java.lang.Math;

public class LineSegment {
	private CartesianCoordinate startPoint;
	private CartesianCoordinate endPoint;
	public LineSegment(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
		 this.startPoint = startPoint;
		 this.endPoint = endPoint; 
		}
	public CartesianCoordinate getStartPoint() {
		return startPoint;
	}
	public CartesianCoordinate getEndPoint() {
		return endPoint;
	}
	
	public double length() {
		
		double dX;
		double dY;
		double distance;
		
		dX = endPoint.getX() - startPoint.getX();
		dY = endPoint.getY() - startPoint.getY();
		
		distance = Math.sqrt(dX*dX + dY*dY);
		return distance;
	}
	 
}
