

package org.chimeras1684.year2014.iterative.aaroot;

import com.sun.squawk.debugger.PacketOutputStream;
import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.SortedVector;
import org.chimeras1684.year2014.iterative.auton.root.Stopwatch;

/**
 * API for the DriveTrain class
 * 
 * 
 *  Usage :
 *      function1:
 *          describe the steps for performing function 1
 *
 *  Functions :
 *      functionName(arguments) -> returnValue (no arrow if no return)
 *          describe the function
 *          Arguments
 *              1) describe the arguments
 *
 *  TODO :   
 *      actually make this API becase i scrolled through the functions and fainted
 *
 * @author Arhowk
*/

public class DriveTrain 
{
    private final Talon leftDrive, rightDrive;
    private final Encoder leftEncoder, rightEncoder;
//    private final Solenoid unusedShifter, driveShifter;
    private Gyro gyro;
    private final ADXL345_I2C accel;
    PacketOutputStream pack;
    
    private final DigitalInput frontLineSensor, rearLineSensor;
    
    private final boolean inverseFrontSensor = true, invertRearSensor = false, shiftHighIsFalse = false;
    
    private final double kp_Lockdown = 0.1, kc_Lockdown = 0.05, kp_Rotate = -0.07;
    
    
    private boolean enabled = false, initialized = false;
    
    private final static double baseEncoderDistance = 108, //how far to drive, in inches
            baseAccelEnd = 25.27, //how long to stop feeding acceleration and just coast
            baseDecelStart = 70.1, //how long to begin decelerating. (Some tests were using high numbers for decel, would make it faster but harsh stop and glitchy inertia
            baseCoastPeriod = 90.3, //how long to just run the motors at 0.15, running on the inertia wave
            
            //all of the above numbers are for 9 feet
            
            inchesToEncoderTicks = 10.235/56,
            encoderTicksToInches = 56/10.235,
            maxPIDSpeed = 1,
            invertLeftEncoder = 1,
            invertRightEncoder = -1;
            
    private double
            driveDistance = 108,
            driveVal = 0,
            accelStartConstant = 0.3,
            minimumDecel = 0.5,
            accelEnd = 0,
            decelStart = 0,
            coastPeriod = 0;
    
    private boolean driveForward = true;
//    private double driveDistance = 108;
//    private double driveVal = 0;
//    final double accelStartConstant = 0.3;
//    final double minimumDecel = 0.5;
//
//    private double accelEnd = 0;
//    private double decelStart = 0;
//    private double coastPeriod = 0;
//    
    private Stopwatch lockdownCap;
    
    /**
     * how drive train
     */
    public DriveTrain()
    {
        SortedVector k;
        lockdownCap = new Stopwatch();
        lockdownCap.start();
        lockdownCap.reset();
        frontLineSensor = new DigitalInput(RobotMap.frontLineSensor);
        rearLineSensor = new DigitalInput(RobotMap.rearLineSensor);
        
        leftDrive = new Talon(RobotMap.leftDrive);
        rightDrive = new Talon(RobotMap.rightDrive);
        
//        unusedShifter = new Solenoid(RobotMap.unusedPWM1);
//        driveShifter = new Solenoid(RobotMap.driveShift);
        
        leftEncoder = new Encoder(RobotMap.leftDriveEncoderA, RobotMap.leftDriveEncoderB);
        rightEncoder = new Encoder(RobotMap.rightDriveEncoderA, RobotMap.rightDriveEncoderB);
        leftEncoder.setDistancePerPulse(-0.00407355); 
        leftEncoder.start();
        leftEncoder.reset();
        rightEncoder.setDistancePerPulse(0.00407355); 
        rightEncoder.start();
        rightEncoder.reset();
        gyro = null;
        new Thread(){
            public void run(){
                gyro = new Gyro(RobotMap.gyro);
                gyro.reset();
                gyro.setSensitivity(1.1);
                System.out.println("start");
            }
        }.start();
        
        accel = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k2G);
        
    }
    
    public boolean getFrontLineSensor(){
        if(inverseFrontSensor){
            return !frontLineSensor.get();
        }else{
            return frontLineSensor.get();
        }
    }
    
    public boolean getRearLineSensor(){
        if(invertRearSensor){
            return !rearLineSensor.get();
        }else{
            return rearLineSensor.get();
        }
    }
    

    /**
     * Gets the gyro
     * @return gyro reading in degrees
     */
    public double getGyro(){
        if(gyro != null)
        {
            return gyro.getAngle()* 169.69696969696;
        }else
        {
            return 0;
        }
    }
    
    public double getLeftDrive(){
        return leftEncoder.getDistance() * invertLeftEncoder;
    }
    
    public double getRightDrive(){
        return leftEncoder.getDistance() * invertRightEncoder;
    }
    
    public boolean isShifted(){
        return false;
//        return driveShifter.get();
    }
    
    
    public boolean isLowGear(){
        return false;
//        if(shiftHighIsFalse){
//            return !driveShifter.get();
//        }else{
//            return driveShifter.get();
//        }
    }
    public boolean isHighGear(){
        return false;
//        if(shiftHighIsFalse){
//            return driveShifter.get();
//        }else{
//            return !driveShifter.get();
//        }
    }
    
    /**
     * Shifts into low gear
     */
    public void shiftLow(){
//        driveShifter.set(!shiftHighIsFalse);
    }

    /**
     * Shifts into high gear
     */
    
    public void shiftHigh(){
//        driveShifter.set(shiftHighIsFalse);
    }

    /**
     * Holds the robot at the current position
     */
    public void lockdown()
    {
        System.out.println("Enter Lockdown!");
        double leftCorrection = kp_Lockdown * (getLeftDrive());
        double rightCorrection = kp_Lockdown * (getRightDrive());
        
        double max = Math.min(1,lockdownCap.get()*0.9);
        
        if(leftCorrection > 0)  leftCorrection  += kc_Lockdown;
        else                    leftCorrection  -= kc_Lockdown;
        
        if(rightCorrection > 0) rightCorrection += kc_Lockdown;
        else                    rightCorrection -= kc_Lockdown;
        
        
        leftCorrection = Math.max(-max, Math.min(max, leftCorrection));
        rightCorrection = Math.max(-max, Math.min(max, rightCorrection));
        
        
        SmartDashboard.putNumber("Rigth : ", rightCorrection);
        SmartDashboard.putNumber("Left : ", leftCorrection);
        
        leftDrive.set(leftCorrection);
        rightDrive.set(-rightCorrection);
    }
    
    /**
     * moves the robot
     * @param moveValue how far to move
     * @param rotateValue in what direction
     * @param sqrd Square the inputs
     */
    public void arcadeDrive(double moveValue, double rotateValue, boolean sqrd)
    {     
        double leftMotorSpeed;
        double rightMotorSpeed;
        double movevalue;
        double rotatevalue;
        
        
//        if(isHighGear()){
//            if(rotateValue > 0.6){
//                rotateValue = 0.6;
//            }else if(rotateValue < -0.6){
//                rotateValue = -0.6;
//            }
//        }
            
        if (moveValue > 1.0){
            moveValue = 1.0;
        }
        
        if (moveValue < -1.0){
            moveValue = -1.0;
        }
        
        if (rotateValue > 1.0){
            rotateValue = 1.0;
        }
        
        if (rotateValue < -1.0){
            rotateValue = -1.0;
        }

        if (moveValue > 0.0) {
            if (rotateValue > 0.0) {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = Math.max(moveValue, rotateValue);
            } else {
                leftMotorSpeed = Math.max(moveValue, -rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            }
        } else {
            if (rotateValue > 0.0) {
                leftMotorSpeed = -Math.max(-moveValue, rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            } else {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
            }
        }
        SmartDashboard.putNumber("moveValue", moveValue);
        SmartDashboard.putNumber("rValue", rotateValue);
        SmartDashboard.putNumber("lspeed", leftMotorSpeed);
        SmartDashboard.putNumber("rspeed", rightMotorSpeed);
        
        if(leftMotorSpeed > 0.85) leftMotorSpeed = 1;
        else if(leftMotorSpeed < -0.85) leftMotorSpeed = -1;
        
        if(rightMotorSpeed > 0.85) rightMotorSpeed = 1;
        else if(rightMotorSpeed < -0.85) rightMotorSpeed = -1;
        
        SmartDashboard.putNumber("lroundspeed", leftMotorSpeed);
        SmartDashboard.putNumber("rroundspeed", rightMotorSpeed);
        
//        if(Math.abs(leftMotorSpeed) > 0.1){
            leftDrive.set(leftMotorSpeed);
//        }else if(Math.abs(leftMotorSpeed) > 0.9){
//            leftDrive.set(leftMotorSpeed * (Math.abs(leftMotorSpeed) / leftMotorSpeed));
//        }else{
//            leftDrive.set(0);
//        }
//        if(Math.abs(rightMotorSpeed) > 0.1){
            rightDrive.set(-rightMotorSpeed);
            DriverStationLCD lcd = DriverStationLCD.getInstance();
            lcd.println(DriverStationLCD.Line.kUser1, 1, "L:"+leftMotorSpeed+"       ");
            lcd.println(DriverStationLCD.Line.kUser2, 1, "R:"+rightMotorSpeed+"        ");
//            System.out.println("r : " + (""+rotateValue) + " L : " + leftMotorSpeed + " R : " + rightMotorSpeed);
            lcd.updateLCD();
//        }else if(Math.abs(rightMotorSpeed) > 0.9){
//            leftDrive.set(-rightMotorSpeed * (Math.abs(rightMotorSpeed) / rightMotorSpeed));
//        }else{
//            rightDrive.set(0);
//        }
    }
    
        
    /**
     * Resets the encoders
     */
    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
        lockdownCap.reset();
    }
    
    /**
     * Resets the gyro
     */
    public void resetGyro(){
        if(gyro != null) gyro.reset();
    }
    
    /**
     * ROTATE pid the robot
     * @param degrees how much to rotate. be sure to reset on init
     */
    
    public void leftDrive(double speed){
        leftDrive.set(speed);
    }
    public void rightDrive(double speed){
        rightDrive.set(-speed);
    }
    
    public void rotate(double degrees){
        double error = undoRotations(getGyro()) - degrees;
        double corr = error * kp_Rotate;
        if(corr > 0.5) corr = 0.5;
        if(corr < -0.5) corr = -0.5;
      //  System.out.println("gyro : " + undoRotations(getGyro()));
        arcadeDrive(0,error*kp_Rotate,false);
    }

    /**
     * Initializes the drive PID
     * @param inches how far to drive. negative to go backwards
     */
    public void initDrivePID(double inches){
        enabled = true;
        defaultPIDValues(inches > 0);
        driveDistance = Math.abs(inches);
        resetEncoders();
    }
    /* returns true if finished */

    /** 
     * runs the pid
     * @return finished
     */
    
   public boolean runPID() {
       if(!enabled){
           return true;
       }
       double get = (getLeftDrive()+getRightDrive()) / 2; //average of the encoders 
       //TODO : Compare getDistance values to getRaw
       
       
        if((get <  0 && driveForward) || (get > 0 && !driveForward)){ //if its momentum is going the wrong way, reset the encoders
            get = 0; //poisition = starting
            leftEncoder.reset(); //reset
            rightEncoder.reset(); //encoders
        }
        if(!driveForward){
            get = -get; //abs the get so that its compatible with sqrt funcs
        }
        
        get = (get / driveDistance) * 100; //convert get to a percentage of distance driven
        SmartDashboard.putNumber("get", get);
        SmartDashboard.putNumber("driveDist", driveDistance);
       double speed = 0; //applied speed
       if(get < accelEnd){ //accelEnd is a percentage. REMEMBER: so is get
           speed = (get / accelEnd) + accelStartConstant; //get the percentage until coasting speed, add 0.
           speed = Math.sqrt(speed); //Curve it, helps it ramp up better
           speed = (speed > maxPIDSpeed) ? maxPIDSpeed : speed; //caps it to maxPIDSpeed
       }else if(get > coastPeriod && get < 100){
           speed = 0; //coaaaaaaaaasting
       }else if(get > 100){
           //isFinished = true; //foiiiiiiiiiiiiinished
       }else if(get > decelStart){
           speed = 0.9 - ((1 - minimumDecel) * ((get-decelStart)/(100-decelStart))); //The latter end of the trapezoidal bezier.
           if(speed < minimumDecel){
              speed = minimumDecel; //So the robot.. actually... moves...
           }else{
             speed =  speed > maxPIDSpeed ? maxPIDSpeed : speed; //caps the speed
           }
       }else{
           speed = maxPIDSpeed; //flatspeed
       }
       if(!driveForward){
           speed = -speed; //inverts the speed if your not driving forward, undoes the -get call
       }
       if(speed < 0.15 && driveForward){
           speed = 0.15; //0.15 speedcaps helps the robot from locking the wheels on coast period
       }
       if(speed > -0.18 && !driveForward){
           speed = -0.18;
       }
       if(speed > 0){
           arcadeDrive(-speed,-0.06, false); //-0.06 to correct for the weight imbalance, keep true
       }else{ //-0.06 CANNOT be based on encoder values because of a massive, random amount of skipped encoder ticks
           arcadeDrive(-speed,-0.06, false); //-0.06 to correct for the weight imbalance, keep true
       }
       if(get > 100){
           enabled = false;
       }
       SmartDashboard.putNumber("PID Speed", speed);
       return !enabled;
    }
    
    private double undoRotations(double d){
        if(d > 0){
            while(d > 360) d -= 360;
        }else if(d < 0){
            while(d < -360) d += 360;
        }
        return d;
    }
           
    private void initializePIDWith(boolean dir, double inches, double accel, double decel, double coast){
        driveDistance = inches * inchesToEncoderTicks;
        accelEnd = accel;
        decelStart = decel;
        coastPeriod = coast;
        driveForward = dir;
    }
    
    private void defaultPIDValues(boolean dir){
        initializePIDWith(dir, baseEncoderDistance * (dir ? 1 : -1), baseAccelEnd, baseDecelStart, baseCoastPeriod);
    }
    
}
