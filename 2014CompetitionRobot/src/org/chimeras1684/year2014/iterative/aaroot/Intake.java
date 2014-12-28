package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Arhowk
 */
public class Intake 
{
    private final Solenoid frontRightIntake,
            frontLeftIntake,
            rearLeftIntake,
            rearRightIntake;
    
    private final Talon frontRoller;
    private final Talon rearRoller;
    
    private final boolean invertFrontIntake = false, invertRearIntake = false;

    /**
     * Constructs the intake
     */
    
    public Intake()
    {
        
        frontRightIntake = new Solenoid(RobotMap.frontRightIntake);
        frontLeftIntake = new Solenoid(RobotMap.frontLeftIntake);
        rearRightIntake = new Solenoid(RobotMap.rearRightIntake);
        rearLeftIntake = new Solenoid(RobotMap.rearLeftIntake);
        frontRoller = new Talon(RobotMap.frontIntake);
        rearRoller = new Talon(RobotMap.rearIntake);
    }

    /**
     * Raises the front intake
     */
    
    public boolean getFrontIntake(){
        if(invertFrontIntake){
            return !frontLeftIntake.get();
        }
        return frontLeftIntake.get();
    }
    public boolean getRearIntake(){
        if(invertRearIntake){
            return !rearLeftIntake.get();
        }
        return rearLeftIntake.get();
    }
    
    
    
    public void frontIntakeRaise()
    {
        frontRightIntake.set(false);
        frontLeftIntake.set(false);
    }
    
    /**
     * Lowers the front intake
     */
    public void frontIntakeLower()
    {
        frontRightIntake.set(true);
        frontLeftIntake.set(true);
    }
    
    /**
     * Raises the rear intake
     */
    public void rearIntakeRaise()
    {
        rearRightIntake.set(false);
        rearLeftIntake.set(false);
    }
    
    /**
     * Lowers the rear intake
     */
    public void rearIntakeLower()
    {
        rearRightIntake.set(true);
        rearLeftIntake.set(true);
    }

    
    public void rollersAt(double arg){
        frontRoller.set(arg);
        rearRoller.set(-arg);
    }
    
    /**
     * Disables the intakes
     */
    public void disableRollers(){
        frontRoller.set(0);
        rearRoller.set(0);
    }
    
    /**
     * Disablse the front intake
     */
    public void frontRollerOff(){
        frontRoller.set(0);
    }
    
    /**
     * Disables the rear intake
     */
    public void rearRollerOff(){
        rearRoller.set(0);
    }
    
    /**
     * Puts the front rollers in
     */
    public void frontRollerIn()
    {
        frontRoller.set(-1);
    }
    
    /**
     * Puts the front rollers out
     */
    public void frontRollerOut()
    {
        frontRoller.set(1);
    }
    
    /**
     * Takes the rear rollers in
     */
    public void rearRollerIn()
    {
        rearRoller.set(-1);
    }
    
    /**
     *
     */
    public void rearRollerOut()
    {
        rearRoller.set(1);
    }
}

