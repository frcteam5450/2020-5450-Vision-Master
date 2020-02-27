package shrec5450;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static shrec5450.Constants.*;

import java.util.ArrayList;
import java.util.List;

public class TrackTarget implements Runnable {
	
	private UsbCamera cam;
	private CvSink sink;
	private CvSource outputStream;
	
	private Mat source;
	private Mat output;
	
	private int
	camPort,
	width,
	height,
	brightness;
	
	private Scalar
	upperBound,
	lowerBound,
	boundingBoxColor;
	
	private double
	fov,
	targetSize,
	targetSideRatio,
	targetSideRatioError;
	
	private NetworkTableEntry
	distanceToTarget,
	angleToTarget,
	visionViable;
	
	public TrackTarget(
			int camPort,
			int width,
			int height,
			int brightness,
			double fov,
			double targetSize,
			double targetSideRatio,
			double targetSideRatioError,
			Scalar upperBound,
			Scalar lowerBound,
			Scalar boundingBoxColor,
			NetworkTableEntry distanceToTarget,
			NetworkTableEntry angleToTarget,
			NetworkTableEntry visionViable,
			String streamName
			) {
		this.camPort = camPort;
		this.width = width;
		this.height = height;
		this.brightness = brightness;
		this.fov = fov;
		this.targetSize = targetSize;
		this.targetSideRatio = targetSideRatio;
		this.targetSideRatioError = targetSideRatioError;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.boundingBoxColor = boundingBoxColor;
		this.distanceToTarget = distanceToTarget;
		this.visionViable = visionViable;
		this.angleToTarget = angleToTarget;
		
		cam = CameraServer.getInstance().startAutomaticCapture(camPort);
		cam.setResolution(width, height);
		cam.setBrightness(brightness);
		print("Initialized and started camera!");
		
		sink = CameraServer.getInstance().getVideo();
		outputStream = CameraServer.getInstance().putVideo(streamName, width, height);
		print("Started Processed Stream");
		
		source = new Mat();
		output = new Mat();
		print("Initialized Mats");
	}
	
	private void print(String str) {
		System.out.println("TrackTarget: " + str);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.interrupted()) {
			if (sink.grabFrame(source) == 0) continue;
			print("Got frame");
			
			Mat temp = new Mat();
			
			Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
			Core.inRange(temp, lowerBound, upperBound, temp);
			print("Thresholded frame");
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			
			Imgproc.findContours(temp, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			print("Found " + contours.size() + " contours");
			
			double referenceArea = 0;
			int selectedContour = -1;
			for (int i = 0; i < contours.size(); i++) {
				if (Imgproc.contourArea(contours.get(i)) > referenceArea) {
					referenceArea = Imgproc.contourArea(contours.get(i));
					selectedContour = i;
				}
			}
			print("Selected contour " + selectedContour);
			
			output = source;
			
			double distance = -1;
			double angle = -100;
			double width = -1;
			double height = -1;
			boolean visionViable = false;
			
			if (selectedContour != -1) {
				temp = contours.get(selectedContour);
				Rect rect = Imgproc.boundingRect(temp);
				
				Point pt1 = rect.tl();
				Point pt2 = rect.br();
				Imgproc.rectangle(output, pt1, pt2, boundingBoxColor);
				width = rect.width;
				height = rect.height;
				
				double sideRatio = (double) rect.width/rect.height;
				//SmartDashboard.putNumber("side ratio", sideRatio);
				if (targetSideRatio - targetSideRatioError < sideRatio && targetSideRatio + targetSideRatioError > sideRatio) {
					
					double halfTotalWidth = ((targetSize * width) / width) / 2;
					distance = halfTotalWidth / (Math.tan(Math.toRadians(fov / 2)));
					
					double centerX = rect.x + (rect.width / 2);
					double offset = (width / 2) - centerX;
					double offsetIn = (halfTotalWidth * offset) / (width / 2);
					angle = Math.toDegrees(Math.atan(offsetIn / distance));
					visionViable = true;
				}
				
			}
			
			/*SmartDashboard.putNumber("Bounding Rectangle Width", width);
			SmartDashboard.putNumber("Bounding Rectangle Height", height);
			SmartDashboard.putNumber("Distance to Target", distance);
			SmartDashboard.putBoolean("Vision Viable?", visionViable);
			SmartDashboard.putNumber("Angle to Target", angle);*/
			
			this.visionViable.setBoolean(visionViable);
			distanceToTarget.setNumber(distance);
			angleToTarget.setNumber(angle);
			
			outputStream.putFrame(output);
		}
	}

}
