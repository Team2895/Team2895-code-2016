
package org.usfirst.frc.team2895.robot;


import edu.wpi.first.wpilibj.vision.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
    RobotDrive drive;
    Joystick robotDriver;
    Joystick mechDriver;
    VictorSP mech;
    USBCamera cameraFront;
    USBCamera cameraMoveable;
    Servo camLeftRight;
    Servo camUpDown;
    DigitalInput topSwitch;
    int session;
    Image img = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    Boolean camLeftValue;
    Robot r;
    CameraServer camServer;
    public static USBCamera camFront;
	public static USBCamera camBack;
	static boolean rearCam = false; // stores whether the front camera is on
	static boolean frontCam = false; // stores whether the front camera is on
    

    public Robot() {
        drive = new RobotDrive(0, 1);
        drive.setExpiration(0.1);
        robotDriver = new Joystick(0);
        mechDriver = new Joystick(1);
        mech = new VictorSP(3);
        camLeftRight = new Servo(5);
        camUpDown = new Servo(6);
        topSwitch = new DigitalInput(1); 
        camLeftValue = false;
        camServer = CameraServer.getInstance();
        camServer.setQuality(100);
        cameraFront = new USBCamera("cam0");
        cameraMoveable = new USBCamera("cam1");
        cameraFront.openCamera();
        cameraMoveable.openCamera();
        cameraFront.startCapture();
        
        

    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
        drive.setSafetyEnabled(false);
        /*
        drive.drive(-0.5, 0.0);	// drive forwards half speed
        Timer.delay(3.0);		//    for 2 seconds
        drive.drive(0.0, 0.0);	// stop robot
        */
        mech.set(1.0);
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {

        drive.setSafetyEnabled(true);
        while (isOperatorControl() && isEnabled()) {
            drive.arcadeDrive(robotDriver); // drive with arcade style (use right stick)   
            
			try {
				// camera streaming
				if (rearCam) {
					cameraMoveable.getImage(img);
				} else if(frontCam) {
					cameraFront.getImage(img);
				}
				camServer.setImage(img); // puts image on the dashboard
			} catch (Exception e) {
				// System.out.println("Error " + e.toString());
				// e.printStackTrace();
				//RobotMap.haveCam = false;
				System.out.println("You don't have camera!");
			}
			
            switchCamera();
            camBracket();
            intakeMech();
            Timer.delay(0.001);		// wait for a motor update time
        }
       
    }
    
    
    public void switchCamera(){
    	if(mechDriver.getRawButton(3)){
    		rearCam = false;
    		frontCam = true;
    		cameraMoveable.stopCapture();
    		cameraFront.startCapture();
            
    	}else if(mechDriver.getRawButton(2)){
    		rearCam = true;
    		frontCam = false;
    		cameraFront.stopCapture();
    		cameraMoveable.startCapture();
    	}
    }
    
    public void intakeMech(){
        if(mechDriver.getRawButton(6)){
        
        	if(topSwitch.get()){
        		mech.set(0.0);
        	}else{
        		mech.set(1.0);
        	}
            
        
        }
        
        if(mechDriver.getRawButton(5)){
        	mech.set(-1.0);
        }else{
        	mech.set(0.0);
        }
        
    	
    }
    
    public void camBracket(){
    	//Camera toggle movement
    	if(mechDriver.getRawButton(9) || camLeftValue == false){
    		camLeftValue = true;	
    	}else{
    		camLeftValue = false;	
    	}
    	
    	//Camera left Right
    	if(camLeftValue == true){
    		camLeftRight.set(mechDriver.getX());
    	}
    			
    	//Camera up and down
    	
    	if(camLeftValue == true){
    		camUpDown.set(mechDriver.getY());
    	}
    }
    
}
