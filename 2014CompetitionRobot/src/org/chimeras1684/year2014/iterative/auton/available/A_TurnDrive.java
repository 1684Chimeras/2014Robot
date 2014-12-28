
package org.chimeras1684.year2014.iterative.auton.available;

import org.chimeras1684.year2014.iterative.auton.root.AutonFramework.TimedCommandGroup;

/**
 *
 * @author Arhowk
 * 
 * API
 *  Functions
 *          add(int requestType -> check RequestConstant.java for all the types of requests
 *              optional double arg -> Passes an argument to the request, ex. shooter speed or drive distance
 *              double startTime -> what time in the auton period to begin. ex. 3 will start at the 3 second mark
 *              double duration -> how long to run the command. 
 * 
 *          addIf(float condition -> constant corresponding to the boolean to return
 *              addElseIf(float condition)
 *              addElse()
 *              addEndIf()
 * 
 *          addWhile(float conditon -> constant corresponding to the boolean. Shouldn't be something like true or false, something that will change after a few seconds
 *                      double startTime -> what time to start. No duration, will stop nce it returns false
 *              addPause() -> indicates a while block
 *              addEndPause() -> ends the block. If a while returned false, the timer will stop and the command will exit untill next iteration
 * 
 * 
 *  Adding Request Types
 *      1) Add constant to "RequestConstants.java"
 *      2) Add respective code to OnRequestEnd, Init, and Periodic
 * 
 *  Adding Condition Types / While Types
 *      1) Add constant to "RequestConstants.java"
 *      2) Add respective code to OnConditional
 * 
 *  Constructing an Auton
 *      1) Create a new auton java file named based on what you want the auton to do
 *      2) Make the new auton class extend "TimedCommandGroup". If it gives you an error, hit Ctrl+Shift+I
 *      3) All code will go into the constructor
 *  
 * 
 */

public class A_TurnDrive extends TimedCommandGroup {
    public A_TurnDrive() {
        add(intakeDown, 0, 1);
        add(shiftHigh,0,1);
        
        addIf(ifStartpointRight);
            add(driveTurnLeftPID, 90, 1, 2);
        addElseIf(ifStartpointLeft);
            add(driveTurnRightPID, 90, 1, 2);
        addEndIf();
        
        addPauseWhile(ifDriveFeet, 6, 3, 3);
        
    }
}