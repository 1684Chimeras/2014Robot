package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Arhowk 
 */
public class XboxControllers 
{
    /*
     * API
     *      #prefix##suffix#() -> boolean [example. driverA()]
     * 
     *          returns true if the button is presse down on the specified controller. Built for XBOX controllers
     * 
     *          Prefix list:
     *              driver : Driver controller, port 1
     *              operator : Operator controller, port 2
     * 
     *          Suffix List:
     *              A : A button 
     *              B : B button
     *              X : X button
     *              Y : Y button
     *              LB : left bumper button
     *              RB : right bumper button
     *              Select : select button
     *              Start : start button
     * 
     *              //Note : positive = up or right
     *              LeftMovePos : left Y axis, up
     *              LeftMoveNeg : left Y axis, down
     *              LeftRotatePos : left X axis, right
     *              LeftRotateNeg : left X axis, left
     *              RightMovePos : right Y axis, up
     *              RightMoveNeg : right Y axis, down
     *              RightRotatePos : right X axis, right
     *              RightRotateNeg : right X axis, left
     *              LeftTrigger : left trigger
     *              RightTrigger : right trigger
     *              LeftDPAD : left on the D-PAD
     *              RightDPAD : right on the DPAD
     * 
     *      #prefix##suffix#() -> double [ex. driverLeftMove()]
     *  
     *          returns the value of the given axis
     * 
     *          Prefix list:
     *              driver : Driver controller, port 1
     *              operator : Operator controller, port 2
     * 
     *          Suffix List:
     *              LeftMove : left axis, move up and down
     *              LeftRotate : left axis, move left and right
     *              RightMove : right axis, move up and down
     *              RightRotate : right axis, move left an right
     *              Triggers : trigger axis. left is negative, right is positive
     *              HoriDPAD : recommended to use LeftDPAD and RightDPAD
     */            
    
    /* How far an axis must be pushed to be considered "pressed" */
    private final double determinant = 0.25;
    
    /* Port configurations */
    private final int buttonA = 1,
            buttonB = 2,
            buttonX = 3,
            buttonY = 4,
            buttonLB = 5,
            buttonRB = 6,
            buttonSelect = 7,
            buttonStart = 8,
            buttonLeftClick = 9,
            buttonRightClick = 10,
            
            axisLeftRotate = 1,
            axisLeftMove = 2,
            axisTriggers = 3,
            axisRightRotate = 4,
            axisRightMove = 5,
            axisHoriDPAD = 6,
            
            /* Multiplied to the return value to correct for direction */
            /* Triggers and dpad have no negator to preserve left-right logic */
            _axisLeftMove = -1,
            _axisLeftRotate = 1,
            _axisRightMove = -1,
            _axisRightRotate = 1;
    
    Joystick driverXboxController;
    Joystick operatorXboxController;
    
    public XboxControllers(int JS1, int JS2) 
    {
        operatorXboxController = new Joystick(JS2);
        driverXboxController = new Joystick(JS1);
    }
    
    public boolean driverA()
    {
        return driverXboxController.getRawButton(buttonA);
    }
    public boolean driverB()
    {
        return driverXboxController.getRawButton(buttonB);
    }
    public boolean driverX()
    {
        return driverXboxController.getRawButton(buttonX);
    }
    public boolean driverY()
    {
        return driverXboxController.getRawButton(buttonY);
    }
    public boolean driverLB()
    {
        return driverXboxController.getRawButton(buttonLB);
    }
    public boolean driverRB()
    {
        return driverXboxController.getRawButton(buttonRB);
    }
    public boolean driverSelect()
    {
        return driverXboxController.getRawButton(buttonSelect);
    }
    public boolean driverStart()
    {
        return driverXboxController.getRawButton(buttonStart);
    }
    public boolean driverLeftClick()
    {
        return driverXboxController.getRawButton(buttonLeftClick);
    }
    public boolean driverRightClick()
    {
        return driverXboxController.getRawButton(buttonRightClick);
    }
    public boolean driverLeftMovePos()
    {
        return driverLeftMove() > determinant;
    }
    public boolean driverLeftMoveNeg()
    {
        return driverLeftMove() < -determinant;
    }
    public boolean driverLeftRotatePos()
    {
        return driverLeftRotate() > determinant;
    }
    public boolean driverLeftRotateNeg()
    {
        return driverLeftRotate() < -determinant;
    }
    public boolean driverRightMovePos()
    {
        return driverRightMove() > determinant;
    }
    public boolean driverRightMoveNeg()
    {
        return driverRightMove() < -determinant;
    }
    public boolean driverRightRotatePos()
    {
        return driverRightRotate() > determinant;
    }
    public boolean driverRightRotateNeg()
    {
        return driverRightRotate() < -determinant;
    }
    public boolean driverLeftTrigger()
    {
        return driverTriggers() < -determinant;
    }
    public boolean driverRightTrigger()
    {
        return driverTriggers() > determinant;
    }
    public boolean driverLeftDPAD()
    {
        return driverHoriDPAD()< -determinant;
    }
    public boolean driverRightDPAD()
    {
        return driverHoriDPAD()> determinant;
    }
    
    public double driverLeftMove()
    {
        return driverXboxController.getRawAxis(axisLeftMove) * _axisLeftMove;
    }
    public double driverLeftRotate()
    {
        return driverXboxController.getRawAxis(axisLeftRotate) * _axisLeftRotate;
    }
    public double driverRightMove()
    {
        return driverXboxController.getRawAxis(axisRightMove) * _axisRightMove;
    }
    public double driverRightRotate()
    {
        return driverXboxController.getRawAxis(axisRightRotate) * _axisRightRotate;
    }
    public double driverTriggers()
    {
        return driverXboxController.getRawAxis(axisTriggers);
    }
    public double driverHoriDPAD()
    {
        return driverXboxController.getRawAxis(axisHoriDPAD);
    }
    
    public boolean operatorA()
    {
        return operatorXboxController.getRawButton(buttonA);
    }
    public boolean operatorB()
    {
        return operatorXboxController.getRawButton(buttonB);
    }
    public boolean operatorX()
    {
        return operatorXboxController.getRawButton(buttonX);
    }
    public boolean operatorY()
    {
        return operatorXboxController.getRawButton(buttonY);
    }
    public boolean operatorLB()
    {
        return operatorXboxController.getRawButton(buttonLB);
    }
    public boolean operatorRB()
    {
        return operatorXboxController.getRawButton(buttonRB);
    }
    public boolean operatorLeftClick()
    {
        return operatorXboxController.getRawButton(buttonLeftClick);
    }
    public boolean operatorRightClick()
    {
        return operatorXboxController.getRawButton(buttonRightClick);
    }
    public boolean operatorSelect()
    {
        return operatorXboxController.getRawButton(buttonSelect);
    }
    public boolean operatorStart()
    {
        return operatorXboxController.getRawButton(buttonStart);
    }
    public boolean operatorLeftMovePos()
    {
        return operatorLeftMove() > determinant;
    }
    public boolean operatorLeftMoveNeg()
    {
        return operatorLeftMove() < -determinant;
    }
    public boolean operatorLeftRotatePos()
    {
        return operatorLeftRotate() > determinant;
    }
    public boolean operatorLeftRotateNeg()
    {
        return operatorLeftRotate() < -determinant;
    }
    public boolean operatorRightMovePos()
    {
        return operatorRightMove() > determinant;
    }
    public boolean operatorRightMoveNeg()
    {
        return operatorRightMove() < -determinant;
    }
    public boolean operatorRightRotatePos()
    {
        return operatorRightRotate() > determinant;
    }
    public boolean operatorRightRotateNeg()
    {
        return operatorRightRotate() < -determinant;
    }
    public boolean operatorLeftTrigger()
    {
        return operatorTriggers() < -determinant;
    }
    public boolean operatorRightTrigger()
    {
        return operatorTriggers() > determinant;
    }
    public boolean operatorLeftDPAD()
    {
        return operatorHoriDPAD()< -determinant;
    }
    public boolean operatorRightDPAD()
    {
        return operatorHoriDPAD()> determinant;
    }
    
    public double operatorLeftMove()
    {
        return operatorXboxController.getRawAxis(axisLeftMove) * _axisLeftMove;
    }
    public double operatorLeftRotate()
    {
        return operatorXboxController.getRawAxis(axisLeftRotate) * _axisLeftRotate;
    }
    public double operatorRightMove()
    {
        return operatorXboxController.getRawAxis(axisRightMove) * _axisRightMove;
    }
    public double operatorRightRotate()
    {
        return operatorXboxController.getRawAxis(axisRightRotate) * _axisRightRotate;
    }
    public double operatorTriggers()
    {
        return operatorXboxController.getRawAxis(axisTriggers);
    }
    public double operatorHoriDPAD()
    {
        return operatorXboxController.getRawAxis(axisHoriDPAD);
    }
    
}