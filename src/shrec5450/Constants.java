package shrec5450;

import org.opencv.core.Scalar;
import edu.wpi.first.networktables.*;

public class Constants {
	/**
	 * Networktables Constants
	 * Instances, subtables, entries
	 */
	public static final NetworkTableInstance inst = NetworkTableInstance.getDefault();
	public static final NetworkTable table = inst.getTable("Vision"); 
	public static final int teamNumber = 5450;
	
	public static final NetworkTableEntry 
	distanceToTarget = table.getEntry("Distance To Target"),
	angleToTarget = table.getEntry("Angle To Target"),
	distanceToBall = table.getEntry("Distance To Ball"),
	angleToBall = table.getEntry("Angle To Ball");
	
	/**
	 * Vision Constants
	 */
	public static final int
	camPort = 0,
	camPortBall = 1,
	width = 640,
	height = 360,
	brightness = 0,
	brightnessBall = 50;
	
	public static final double
	fov = 60.84,
	targetWidth = 39.25,
	targetSideRatio = 2.3088,
	targetSideRatioError = targetSideRatio * 0.25,
	ballWidth = 7,
	ballSideRatio = 1,
	ballSideRatioError = ballSideRatio * 0.1;
	
	public static final Scalar
	lowerBound = new Scalar(50, 126, 140),
	upperBound = new Scalar(130, 255, 255),
	lowerBoundBall = new Scalar(0, 0, 0),
	upperBoundBall = new Scalar(0, 0, 0),
	boundBoxColor = new Scalar(0, 255, 255);
}
