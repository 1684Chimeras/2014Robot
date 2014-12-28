/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.commandlist;

import org.chimeras1684.year2014.iterative.auton.root.OnRequest;

/**
 *
 * @author Arhowk
 */
public class OnRequestEnd extends OnRequest{
    
    public static void onRequestFinish(int request, double arg)
    {
        switch(request)
        {
            case leftDrive:
                drive.leftDrive(0);
                break;
                
            case rightDrive:
                drive.rightDrive(0);
                break;
                
            case upperStructureEnable:
                upperStructure.manual(0);
                break;
            case intakeOut:
            case intakeIn:
                intake.disableRollers();
                break;
            
            case frontIntakeOut:
            case frontIntakeIn:
                intake.frontRollerOff();
                
            case rearIntakeOut:
            case rearIntakeIn:
                intake.rearRollerOff();
                
            case kickerBackward:
            case kickerForward:
            case kickerHold:
                kicker.set(0);
                
            case RequestConstants.driveTurnLeft:
            case RequestConstants.driveTurnRight:
            case RequestConstants.driveOff:
            case RequestConstants.driveForward:
            case RequestConstants.driveFeet:
                drive.arcadeDrive(0,0,false);
                break;
            
            case intakeOn:
                
                break;
                
        }
    }
}
