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
	
	private UsbCamera _cam;
	private CvSink sink;
	private CvSource outputStream;
	
	private Mat source;
	private Mat output;
	
	private int
	_camPort,
	_width,
	_height,
	_brightness;
	
	private Scalar
	_upperBound,
	_lowerBound,
	_boundingBoxColor;
	
	private double
	_fov,
	_targetSize,
	_targetSideRatio,
	_targetSideRatioError;
	
	private NetworkTableEntry
	_distanceToTarget,
	_angleToTarget;
	
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
			String streamName
			) {
		_camPort = camPort;
		_width = width;
		_height = height;
		_brightness = brightness;
		_fov = fov;
		_targetSize = targetSize;
		_targetSideRatio = targetSideRatio;
		_targetSideRatioError = targetSideRatioError;
		_upperBound = upperBound;
		_lowerBound = lowerBound;
		_boundingBoxColor = boundingBoxColor;
		_distanceToTarget = distanceToTarget;
		_angleToTarget = angleToTarget;
		
		_cam = CameraServer.getInstance().startAutomaticCapture(_camPort);
		_cam.setResolution(_width, _height);
		_cam.setBrightness(_brightness);
		print("Initialized and started camera!");
		
		sink = CameraServer.getInstance().getVideo();
		outputStream = CameraServer.getInstance().putVideo(streamName, _width, _height);
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
			Core.inRange(temp, _lowerBound, _upperBound, temp);
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
			double angle = -1;
			double width = -1;
			double height = -1;
			boolean visionViable = false;
			
			if (selectedContour != -1) {
				temp = contours.get(selectedContour);
				Rect rect = Imgproc.boundingRect(temp);
				
				Point pt1 = rect.tl();
				Point pt2 = rect.br();
				Imgproc.rectangle(output, pt1, pt2, _boundingBoxColor);
				width = rect.width;
				height = rect.height;
				
				double sideRatio = (double) rect.width/rect.height;
				SmartDashboard.putNumber("side ratio", sideRatio);
				if (_targetSideRatio - _targetSideRatioError < sideRatio && _targetSideRatio + _targetSideRatioError > sideRatio) {
					
					double halfTotalWidth = ((_targetSize * _width) / width) / 2;
					distance = halfTotalWidth / (Math.tan(Math.toRadians(_fov / 2)));
					
					double centerX = rect.x + (rect.width / 2);
					double offset = (_width / 2) - centerX;
					double offsetIn = (halfTotalWidth * offset) / (_width / 2);
					angle = Math.toDegrees(Math.atan(offsetIn / distance));
					visionViable = true;
				}
				
			}
			
			/*SmartDashboard.putNumber("Bounding Rectangle Width", width);
			SmartDashboard.putNumber("Bounding Rectangle Height", height);
			SmartDashboard.putNumber("Distance to Target", distance);
			SmartDashboard.putBoolean("Vision Viable?", visionViable);
			SmartDashboard.putNumber("Angle to Target", angle);*/
			
			_distanceToTarget.setNumber(distance);
			_angleToTarget.setNumber(angle);
			
			outputStream.putFrame(output);
		}
	}

}
