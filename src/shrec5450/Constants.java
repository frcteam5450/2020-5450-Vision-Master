package shrec5450;

import org.opencv.core.Scalar;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.shuffleboard.*;

public class Constants {
	/**
	 * Networktables Constants
	 * Instances, subtables, entries
	 */
	public static final NetworkTableInstance inst = NetworkTableInstance.getDefault();
	//public static final NetworkTable table = inst.getTable("Vision"); 
	public static final int teamNumber = 5450;
	
	public static final ShuffleboardTab tab = Shuffleboard.getTab("Vision");
	
	public static final NetworkTableEntry 
	distanceToTarget = tab.add("Distance To Target", 0).getEntry(),
	angleToTarget = tab.add("Angle to Target", 0).getEntry(),
	distanceToBall = tab.add("Distance to Ball", 0).getEntry(),
	angleToBall = tab.add("Angle to Ball", 0).getEntry();
	
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
