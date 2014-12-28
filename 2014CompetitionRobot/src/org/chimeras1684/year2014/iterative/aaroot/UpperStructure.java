
package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Arhowk
 */
public class UpperStructure implements PIDSource, PIDOutput
{
    private final Talon leftMotor;//, rightMotor;
    
    SPIEncoder positionEncoder;
    
    AnalogChannel frontLimit;
    AnalogChannel rearLimit;
    
    int currentPosition = 0;
    int error = 0;
    double correction = 0.0;
    int homeSetpoint = 550;
    int setpoint = homeSetpoint;
    double kP_hold = 0.008;
    double kI_hold = 0.001;
    double kD_hold = 0.1;
    double kG_hold = 0.001;
    final double max = 0.35;
    PIDController upperPID;
    
    public UpperStructure()
    {
        leftMotor = new Talon(RobotMap.tower);
       // rightMotor = new Talon(RobotMap.rightUpper);
        positionEncoder = new SPIEncoder(RobotMap.SPIBus_csTower, false);
        frontLimit = new AnalogChannel(RobotMap.tiltLimit1);
        rearLimit = new AnalogChannel(RobotMap.tiltLimit2);
        upperPID = new PIDController(kP_hold, kI_hold, kD_hold, this,this);
        SmartDashboard.putData("Upper Structure PID", upperPID);
        upperPID.setContinuous(false);
        upperPID.setOutputRange(-max, max);
        upperPID.setInputRange(0,1024);
        upperPID.setSetpoint(setpoint);
        upperPID.disable();
    }
    double pidSpeed = 0;
    public void pidWrite(double d) {
        d += (setpoint - homeSetpoint) * kG_hold;
        if(d > max) d = max;
        if(d < -max) d = -max;
        
        pidSpeed = d;
    }
    
    public double pidGet() {
        return positionEncoder.get();
    }
    public void manual(double speed){
//        leftMotor.set(speed);
    }
    int prev;
    public double getError(){
        return (positionEncoder.get() - setpoint) * kP_hold;
    }
    public void update()
    {
//        leftMotor.set(pidSpeed);
        //currentPosition = positionEncoder.getAverageValue();
//        System.out.println("setpoint : " + setpoint);
        currentPosition = positionEncoder.get();
        if(currentPosition < 512) currentPosition += 1024;
//        System.out.println("currentPosition : " + currentPosition);
        error = currentPosition - setpoint;
//        System.out.println("error : " + error);
     //   if(error > 50) error = 0;
       // if(error > 20) error = 15;
        double gravitational = (setpoint - homeSetpoint) * kG_hold;
        double derivative = positionEncoder.getRate() * kD_hold;
        correction = kP_hold * error;
        System.out.println("kp correction : " + correction);
        double apply = correction + gravitational;

        if(Math.abs(error) < 20){
            apply += derivative;
        }
        
        double amount = Math.abs(apply);
        
        if(amount > max){
            if (amount != apply){
                apply = -max;
            }else{
                apply = max;
            }
        }else{
 //           apply = correction + integral;
        }
//        leftMotor.set(apply);
        System.out.println("final : " + apply);
        
    }
    boolean isSetpointTruss = false;
    int setpointFront = 200;
    int setpointBack = 200;
    public void setFrontTeamLine()
    {
        isSetpointTruss = false;
        setpoint = setpointFront;
        upperPID.setSetpoint(setpoint);
        upperPID.setPID(kP_hold, kI_hold, kD_hold);
    }
    
    public void setkP(double kP){
        kP_hold = kP;
        upperPID.setPID(kP, upperPID.getI(), upperPID.getD());
    }
    
    public void setBack(int set){
        setpointBack = set;
    }
    
    public void setFront(int set){
        setpointFront = set;
    }
    
    public void setkI(double kI)
    {
        kI_hold = kI;
        upperPID.setPID(upperPID.getP(), kI, upperPID.getD());
    }
    
    public void setkD(double kD)
    {
        kD_hold = kD;
        upperPID.setPID(upperPID.getP(), upperPID.getI(), upperPID.getD());
    }
    
    
    public void setHome(int point){
        homeSetpoint = point;
    }
            
    
    
    public void setpointHome()
    {
        isSetpointTruss = false;
        setpoint = homeSetpoint;
        upperPID.setSetpoint(setpoint);
        upperPID.setPID(kP_hold, kI_hold, kD_hold);
    }
    
    public void setpointBackward()
    {
        isSetpointTruss = true;
        setpoint = setpointBack;
        upperPID.setSetpoint(setpoint);
        upperPID.setPID(kP_hold, kI_hold, kD_hold);
    }
    
    public void setpointBackwardWithForce()
    {
        isSetpointTruss = false;
        setpoint = setpointBack + ((setpointBack - homeSetpoint) / 2);
        upperPID.setSetpoint(setpoint);
        upperPID.setPID(kP_hold, 0, kD_hold);
    }
    
    public void setpointForward()
    {
        isSetpointTruss = false;
        setpoint = setpointFront;
        upperPID.setSetpoint(setpoint);
        upperPID.setPID(kP_hold, kI_hold, kD_hold);
    }
    
    public void setkG(double kg){
        kG_hold = kg;
    }
    
    public boolean isTrussSetpoint()
    {
        return isSetpointTruss;
    }


}
