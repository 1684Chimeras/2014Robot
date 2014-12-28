
package org.chimeras1684.year2014.iterative.auton.commandlist;

/**
 *
 * @author Arhowk
 */
public interface RequestConstants{

    final static int
            
            driveForward = 0,
            driveOff = 1,
            intakeOn = 2,
            intakeOff = 3,
            driveFeet = 4,
            targetingYellowGoal = 5,
            targetingReflectiveTarget = 6,
            snapshot = 35,
            driveTurnLeft = 7,
            driveTurnRight = 8,
            driveTurnLeftPID = 29,
            driveTurnRightPID = 30,
            kickerForward = 9,
            kickerBackward = 10,
            kickerHold = 11,
            upperStructureForward = 12,
            upperStructureBackward = 13,
            upperStructureBackwardForce = 28,
            upperStructureHome = 14,
            upperStructureEnable = 15,
            upperStructureManual = 33,
            intakeIn = 16,
            intakeOut = 17,
            shiftLow = 31,
            shiftHigh = 32,
            frontIntakeUp = 18,
            frontIntakeDown = 19,
            rearIntakeUp = 20,
            rearIntakeDown = 21,
            intakeUp = 22,
            intakeDown = 23,
            frontIntakeIn = 24,
            frontIntakeOut = 25,
            rearIntakeIn = 26,
            rearIntakeOut = 27,
            compressorToggle = 34,
            leftDrive = 36,
            rightDrive = 37,
            intakeCustomSpeed = 38,
            ledOn = 39,
            next = 40,
            
            
            END = 9999;

    final static float
            ifWait = 1,
            ifToTheLeft = 2,
            ifToTheRight = 3,
            ifInTheCenter =4,
            ifDriveFeet =5,
            ifFalse = 6,
            ifTrue = 7,
            ifStartpointLeft = 8,
            ifStartpointCenter = 9,
            ifStartpointRight = 10,
            ifKickerMacro = 11,
            
            ifEND = 9999;
}