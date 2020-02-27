package shrec5450;

/*import edu.wpi.first.cameraserver.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.*;*/
import org.opencv.core.*;
/*import org.opencv.videoio.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.*;
import java.lang.*;
import java.util.*;
import java.io.*;*/
import static shrec5450.Constants.*;

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
	}

}
