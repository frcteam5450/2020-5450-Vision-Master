package shrec5450;

import org.opencv.core.*;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;

import static shrec5450.Constants.*;

/**
 * Version 0.1.0 | February 12, 2020
 * Vision Program is tested and Working
 * This program can track an FRC Vision target, and
 * calculate the distance and angle to it.
 * @author evang
 *
 */

public class Vision {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		inst.startClientTeam(teamNumber);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		new Thread(new TrackTarget(
				camPort, 
				width, 
				height, 
				brightness, 
				fov, 
				targetWidth,
				targetSideRatio,
				targetSideRatioError,
				upperBound, 
				lowerBound, 
				boundBoxColor,
				distanceToTarget,
				angleToTarget,
				visionViable,
				"Track Vision Target"
				)).start();
		
		/*new Thread(new TrackTarget(
				camPortBall, 
				width, 
				height, 
				brightnessBall, 
				fov, 
				ballWidth,
				ballSideRatio,
				ballSideRatioError,
				upperBoundBall, 
				lowerBoundBall, 
				boundBoxColor,
				distanceToBall,
				angleToBall,
				"Track Power Cell"
				)).start();*/
		//UsbCamera cam = CameraServer.getInstance().startAutomaticCapture(1);
		//cam.setResolution(640, 360);
		
	}

}
