/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.commandlist;

import edu.wpi.first.wpilibj.Relay;
import org.chimeras1684.year2014.iterative.aaroot.Teleoperated;
import org.chimeras1684.year2014.iterative.aaroot.UpperStructure;
import org.chimeras1684.year2014.iterative.auton.root.OnRequest;

/**
 *
 * @author Arhowk
 */
public class OnRequestInit extends OnRequest
{
    public static void onRequestStart(int request, double arg)
    {
        switch(request)
        {
            case snapshot:
                targeting.snapshotYellowLED();
                break;
                
            case ledOn:
                Teleoperated.lightRelay.set(Relay.Value.kOn);
                break;
                
            case shiftHigh:
//                drive.shiftHigh();
                kicker.punchOut();
                break;
            case shiftLow:
//                drive.shiftLow();
                kicker.punchIn();
                break;
              
            case driveTurnLeftPID:
            case driveTurnRightPID:
                drive.resetGyro();
                break;
                
            case targetingReflectiveTarget:
                targeting.snapshotHorizontalGoal();
                break;
                
            case targetingYellowGoal:
                targeting.snapshotYellowLED();
                break;
                
            case upperStructureBackwardForce:
                upperStructure.setpointBackwardWithForce();
                break;
                
            case upperStructureBackward:
                upperStructure.setpointBackward();
                break;
                
            case upperStructureForward:
                upperStructure.setpointForward();
                break;
                
            case upperStructureHome:
                upperStructure.setpointHome();
                break;
                
            case driveFeet:
                drive.initDrivePID((arg == 0 ? 5 : arg)*12);
                break;
        }
    }
}
