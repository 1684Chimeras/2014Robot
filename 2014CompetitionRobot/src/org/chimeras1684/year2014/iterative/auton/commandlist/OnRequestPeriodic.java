/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.commandlist;

import org.chimeras1684.year2014.iterative.aaroot.Teleoperated;
import org.chimeras1684.year2014.iterative.auton.root.OnRequest;

/**
 *
 * @author Arhowk
 */
public class OnRequestPeriodic extends OnRequest{
    
    public static void runRequest(int request, double arg)
    {
        switch(request)
        {
            case intakeCustomSpeed:
                intake.rollersAt(arg);
                break;
                
            case intakeIn:
                intake.frontRollerOut();
                intake.rearRollerOut();
                break;
                
            case intakeOut:
                intake.frontRollerIn();
                intake.rearRollerIn();
                break;
            
            case frontIntakeIn:
                intake.frontRollerOut();
                break;
                
            case frontIntakeOut:
                intake.frontRollerIn();
                break;
                
            case rearIntakeIn:
                intake.rearRollerOut();
                break;
                
            case rearIntakeOut:
                intake.rearRollerIn();
                break;
                
            case intakeUp:
                intake.frontIntakeRaise();
                intake.rearIntakeRaise();
                break;
                
            case intakeDown:
                intake.frontIntakeLower();
                intake.rearIntakeLower();
                break;
                
            case frontIntakeUp:
                intake.frontIntakeRaise();
                break;
                
            case frontIntakeDown:
                intake.frontIntakeLower();
                break;
                
            case rearIntakeUp:
                intake.rearIntakeRaise();
                break;
                
            case rearIntakeDown:
                intake.rearIntakeLower();
                break;
                
            case kickerHold:
                kicker.halt(arg  == 0 ? 180 : arg);
                break;
                
            case kickerBackward:
                kicker.set(arg == 0 ? -0.5 : -arg);
                break;
                
            case kickerForward:
                double ang = kicker.getKickerAngle();
                if(ang > Teleoperated.hardstopStart && ang < Teleoperated.hardstopStop){
                    kicker.set(-1);
                }else{
                    kicker.set(arg == 0 ? 0.5 : arg);
                }
                break;
                
            case compressorToggle:
                compressor.compress();
                break;
                
            case upperStructureEnable:
                upperStructure.update();
                break;
                
            case driveTurnLeftPID:
                drive.rotate(arg == 0 ? -15 : -arg);
                break;
                
            case driveTurnRightPID:
                drive.rotate(arg == 0 ? 15 : arg);
                break;
                
            case upperStructureManual:
                upperStructure.manual(arg);
                break;
                
            case driveTurnLeft:
                drive.arcadeDrive(0, -1, true);
                break;
                
            case driveTurnRight:
                drive.arcadeDrive(0, 1, true);
                break;
                
            case leftDrive:
                drive.leftDrive(arg);
                break;
                
                
            case rightDrive:
                drive.rightDrive(arg);
                break;
            
            case driveForward:
                drive.arcadeDrive(arg == 0 ? 1 : arg, 0,false);
                break;
                
            case driveFeet:
                drive.runPID();
                break;
                
            case driveOff:
                drive.arcadeDrive(0,0,false);
                break;
                
            default:
              //  System.out.println("[OnRequestPeriodic.java] Error : Set Request does not exist");
        }
    } //WHile false : stop!
}
