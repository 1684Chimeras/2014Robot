package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.chimeras1684.year2014.iterative.auton.root.Stopwatch;

/**
 *
 * @author Arhowk
 */
public class Teleoperated 
{
    /*
    
     *      CONTROL SCEMATICS
     *
     *  Driver
     *      Left Joystick - Robot Move (arcade drive)
     *      Right Joystick - 
     * 
     *      A - Shift Toggle
     *      B - Lockdown
     *      X - 
     *      Y - 
     *      LB - Front Manipulator Toggle (default up)
     *      RB - Rear Manipulator Toggle (default up)
     *      LT - Rollers Out
     *      RT - Rollers In
     *      DPAD Left - 
     *      DPAD Right - 
     *      Left Click - 
     *      Right Click -
     *      Select - Drive Direction 
     *      Start - Bag Position
     * 
     *  Operator
     *      Left Joystick - 
     *      Right Joystick - Tower Override
     * 
     *      A - Setpoint - Backward
     *      
     *      X - kick
     *      Y - Setpoint - Forward
            b - compressor off
     *      LB - Tower PID
     *      RB - Kicker PID
     *      LT - Kicker Rewind
     *      RT - Kicker Swing 
     *      DPAD Left - Low Gear Underride
     *      DPAD Right - Manipulator Underride
     *      Left Click - 
     *      Right Click - 
     * 
     */
    Targeting targeting;
    Compressor pneumatics;
    XboxControllers controllers;
    DriveTrain driveTrain;
    Intake intake;
    Kicker kicker;
    UpperStructure upperStructure;
    Joystick testController;
    Stopwatch softStopWatch;
    public static Relay lightRelay;
    public Stopwatch macroStopwatch = new Stopwatch();
    //68
    public static double hardstopStart = 48, hardstopStop = 120, timeToStop = 0.1;
    boolean softStopped = false, kickerOn = false;
    boolean hasOperator = true;
    
    String _start = "Hardstop Start", _stop = "Hardstop Stop", _soft = "Time To Stop";
    
    public Teleoperated(Targeting T, Compressor P, DriveTrain DT, Intake I, Kicker K, UpperStructure US, XboxControllers XB)
    {
        targeting = T;
        pneumatics = P;
        controllers = XB;
        driveTrain = DT;
        intake = I;
        kicker = K;
        upperStructure = US;
        testController = new Joystick(3);
        lightRelay = new Relay(RobotMap.directionLight);
        softStopWatch = new Stopwatch();
        SmartDashboard.putNumber(_start, hardstopStart);
        SmartDashboard.putNumber(_stop, hardstopStop);
        SmartDashboard.putNumber(_soft, timeToStop);
        SmartDashboard.putNumber("Kicker Speeeeeeeeeeed", 0.3);
        
   }
    int moveInversion = 1;
    
    boolean operatorLeftClickPressed  = false;
    boolean driverLeftBumperPressed = false;
    boolean driverRightBumperPressed = false;
    boolean driverAPressed = false;
    boolean driverBPressed = false;
    boolean driverSelectPressed = false;
    boolean operatorLBPressed = false;
    boolean operatorXPressed = false;
    boolean upperStructurePID = false;
    boolean humanPassWatchSet = false;
    
    boolean sensorDetected = false;
    boolean shiftLow = false;
    boolean frontArmUp = true;
    boolean rearArmUp = true;
    Stopwatch backPressureWatch = null;
    Stopwatch kickerWatch = null;
    Stopwatch humanPassWatch = null;
    boolean kicking = false;
    boolean pressureEnabled = false;
    boolean macroing = false;
    
    public void init()
    {
        if(backPressureWatch == null) backPressureWatch = new Stopwatch();
        if(kickerWatch == null) kickerWatch = new Stopwatch();
        if(humanPassWatch == null) humanPassWatch = new Stopwatch();
        driveTrain.resetEncoders();
        pressureEnabled = false;
        softStopped = false;
        macroing = false;
        kicking = false;
        humanPassWatchSet = false;
        macroStopwatch.start();
        macroStopwatch.reset();
        backPressureWatch.start();
        backPressureWatch.reset();
        softStopWatch.start();
        softStopWatch.reset();
        kickerWatch.start();
        kickerWatch.reset();
        humanPassWatch.start();
        humanPassWatch.reset();
        sensorDetected = false;
        frontArmUp = true;
        rearArmUp = true;
        shiftLow = false;//driveTrain.driveShifter.get();
        upperStructurePID = false;
        driverLeftBumperPressed = false;
        kickerOn = false;
        driverRightBumperPressed = false;
        driverAPressed = false;
        driverSelectPressed = false;
        operatorLBPressed = false;
        operatorXPressed = false;
        moveInversion = -1;
        hardstopStart = SmartDashboard.getNumber(_start, hardstopStart);
        hardstopStop = SmartDashboard.getNumber(_stop, hardstopStop);
        timeToStop = SmartDashboard.getNumber(_soft, timeToStop);
        upperStructure.setpointHome();
//        targeting.snapshotYellowLED();
    }
    
    public void softStop(){
        if(!softStopped){
            softStopped = true;
            softStopWatch.reset();
        }
//        System.out.println("soft! value : " + (softStopWatch.get() * timeToStop));
        kicker.set(-softStopWatch.get() / timeToStop - 0.3);
        
    }
    
    public void update(boolean goodToGo)
    {
        if(!goodToGo){
            driveTrain.shiftLow();
            shiftLow = true;
            kicker.set(0);
            kicker.punchIn();
            kicking = false;
            driveTrain.arcadeDrive(0, 0, false);
            intake.rollersAt(0);
            kicking = false;
            macroing = false;
            kicker.set(0);
            upperStructure.manual(0);
            return;
        }
//        if(targeting.inTheCenter()){
//            System.out.println("c");
//        }else if(targeting.toTheLeft()){
//            System.out.println("l");
//        }else if(targeting.toTheRight()){
//            System.out.println("r");
//        }else{
//            System.out.println("no targ");
//        }
     //   upperStructure.setkP(SmartDashboard.getNumber("kP", 0.008));
        
        if(controllers.driverA())
        {
            if(!driverAPressed)
            {
               shiftLow = !shiftLow;
               if(shiftLow)
               {
                   SmartDashboard.putBoolean("lowGear", true);
                   driveTrain.shiftLow();
               }else{
                   SmartDashboard.putBoolean("lowGear", false);
                   driveTrain.shiftHigh();
               }
               driverAPressed = true;
            }
        }else driverAPressed = false;
        boolean b3 = false;
        if(controllers.driverB())
        {
//            if(!sensorDetected){
//                sensorDetected = true;
//                driveTrain.resetEncoders();
//            }
//            shiftLow = true;
//            driveTrain.lockdown();
//            if(/*driveTrain.getFrontLineSensor() || */driveTrain.getRearLineSensor() && !sensorDetected){
//                driveTrain.resetEncoders();
//                sensorDetected = true;
//            }
//            if(sensorDetected){
//                SmartDashboard.putBoolean("lineSensor", true);
//                b3 = true;
//                driveTrain.lockdown();
//            }
        }else sensorDetected = false;
        if(!b3){
                SmartDashboard.putBoolean("lineSensor", false);
        }
        
//        if(!controllers.driverB()){
//            if(testController.getRawButton(1)){
//                driveTrain.leftDrive(1);
//                driveTrain.rightDrive(-1);
//            }else if(testController.getRawButton(4)){
//                driveTrain.leftDrive(-1);
//                driveTrain.rightDrive(1);
//            }else{
                if(Math.abs(controllers.driverLeftMove()) > 0.1 || Math.abs(controllers.driverLeftRotate()) > 0.1){
                    double move = controllers.driverLeftMove();
                    if(move < -0.75){
                        move = -1;
                    }
                    driveTrain.arcadeDrive(move * moveInversion, controllers.driverLeftRotate(),true);
                }else{
                    driveTrain.arcadeDrive(0,0,false);
                }
                SmartDashboard.putNumber("D Move", controllers.driverLeftMove());
                SmartDashboard.putNumber("D Rotate", controllers.driverLeftRotate());
                
//            }
//        }
        
//        if(controllers.driverB() && !hasOperator && !kicking && !macroing)
//        {
//            kicker.set(0.2);
//        }
        
        if(controllers.driverY())
        {
            
  
        }
        if(controllers.driverRB())
        {
            if(!driverLeftBumperPressed)
            {
                if(false/*moveInversion == -1*/){
                    rearArmUp = !rearArmUp;
                    if(rearArmUp)
                    {
                        intake.rearIntakeRaise();
                    }else{
                        intake.rearIntakeLower();
                    }
                }else{
                    frontArmUp = !frontArmUp;
                    if(frontArmUp)
                    {
                        intake.frontIntakeRaise();
                    }else{
                        intake.frontIntakeLower();
                    }
                }
                driverLeftBumperPressed = true;
            }
        }else driverLeftBumperPressed = false;
        
        if(controllers.driverLB() || controllers.operatorLeftDPAD())
        {
            if(!driverRightBumperPressed)
            {
                driverRightBumperPressed = true;
                if(true/*moveInversion == 1*/){
                    rearArmUp = !rearArmUp;
                    if(rearArmUp)
                    {
                        intake.rearIntakeRaise();
                    }else{
                        intake.rearIntakeLower();
                    }
                }else{
                    frontArmUp = !frontArmUp;
                    if(frontArmUp)
                    {
                        intake.frontIntakeRaise();
                    }else{
                        intake.frontIntakeLower();
                    }
                }
            }
        }else driverRightBumperPressed = false;
        
        if(controllers.driverLeftTrigger())
        {
            intake.rollersAt(controllers.driverTriggers());
            //intake.rearRollerOut();
        }else if(controllers.driverRightTrigger())
        {
            intake.rollersAt(controllers.driverTriggers());
            //intake.frontRollerIn();
           // intake.rearRollerIn();
        }else{
            intake.disableRollers();
        }
        
        if(controllers.driverStart()){
//            frontArmUp = true;
//            rearArmUp = true;
//            intake.frontIntakeRaise();
//            intake.rearIntakeRaise();
//            upperStructure.setpointHome();
        }
        if(/*controllers.driverSelect()*/controllers.driverY())
        {
            if(!driverSelectPressed){
                moveInversion *= -1;
                driverSelectPressed = true;
            }
        } else driverSelectPressed = false;
        
        /*OPERATOR NEW*/
        if(controllers.operatorA()){
            if(!operatorXPressed){
                operatorXPressed = true;
                kickerOn = !kickerOn;
            }
        }else operatorXPressed = false;
        
        if(kickerOn){
            kicker.set(1);
        }else if(controllers.operatorB()){
            kicker.set(-0.55);
        }else if(controllers.operatorY()){
            kicker.set(0.6);
        }else{
            kicker.set(0);
        }
        
        if(controllers.operatorX()){
            if(!kicking){
                kicking = true;
                kickerWatch.start();
                kickerWatch.reset();
            }
        }
        
        if(kicking && kickerWatch.get() < 3){
            kicker.punchOut();
        }else{
            kicker.punchIn();
            kicking = false;
        }
        
        
        
//////        /* OPERATOR */
//////        if(controllers.operatorX() || (controllers.driverX() && !hasOperator)){
//////            rearArmUp = false;
//////            if(!operatorXPressed){
//////                operatorXPressed = true;
//////                kicking = !kicking;
//////                if(kicking){
//////                    rearArmUp = false;
//////                    frontArmUp = false;
//////                }
//////                kickerWatch.reset();
//////            }
//////        }else operatorXPressed = false;
//////        
//////        if(kicking && kickerWatch.get() < 0.9){
//////            if(kicker.kickerMacro()){
//////                kicking = false;
//////            }
//////        }else kicking = false;
//////        
////////        if(kicking && kickerWatch.get() < 1.5){
////////            System.out.println("Should be Kicking!");
////////            double ang = kicker.getKickerAngle();
////////            if(softStopped && !(ang > hardstopStart && ang < hardstopStop)){   // if soft stop detected and already processed
//////////                System.out.println("Outside of hardstop, exit");
////////                kicker.set(-0.25);
////////                kicking = false;
////////            }else{
////////                if(!(ang > hardstopStart && ang < hardstopStop) ){
//////////                    System.out.println("Not within hardstop, exit!");
////////                    kicker.set(1);
////////                    softStopped = false;
////////                }else{
//////////                    System.out.println("Softstop!");
//////////                    softStopped = true;
////////                    kicker.set(0);
////////                    softStop();
////////                }
////////            }
////////        }else kicking = false;
//////        
//////        
//////        
//////        boolean b = false;
//////        
//////        if(!kicking ){
////////            if(controllers.
//////            ;
//////            
//////            if(!macroing && !controllers.driverX()){
//////                if(controllers.operatorRB() || (controllers.driverB() && !hasOperator)/* && (upperStructure.setpoint < upperStructure.setpointBack)*/)
//////                {
////////                    kicker.halt(287);
//////                    kicker.set(0.4);
//////                    SmartDashboard.putBoolean("kickerForwardHold", true);
//////                    b = true;
//////                    softStopped = false;
//////                }else if(controllers.operatorLeftClick()){
////////                    kicker.halt(240);
//////                }else if(controllers.operatorTriggers() > 0.1 || controllers.operatorTriggers() < -0.1)
//////                {
//////                    double ang = kicker.getKickerAngle();
//////                    boolean forward = controllers.operatorTriggers() < 0;
//////                    if(forward && Math.abs(controllers.operatorTriggers()) > 0.4){
//////                        if(!humanPassWatchSet || humanPassWatch.get() > 6){
//////                            humanPassWatchSet = true;
//////                            humanPassWatch.start();
//////                            humanPassWatch.reset();
//////                        }
//////                        if(humanPassWatch.get() > 0.35){
//////                            kicker.set(-0.3);
//////                        }else{
//////                            
//////                            kicker.set(0.75);
//////                        }
//////                    }else if(false/*ang > hardstopStart && ang < hardstopStop && Math.abs(controllers.operatorTriggers()) > 0.75*/){
//////    //                    kicker.set(-1);
//////                        softStop();
//////    //                    kicker.set(-controllers.operatorTriggers());
//////                    }else{
//////                        softStopped = false;
//////                        kicker.set(-controllers.operatorTriggers() * SmartDashboard.getNumber("Trigger Scale"));
//////                    }
//////                }else/* if(!controllers.operatorRB())*/{
//////    //                kicker.set(0);
//////                    kicker.set(0);
//////                    softStopped = false;
//////                    humanPassWatchSet = false;
//////                }
//////            }
//////        }
//////        
//////        if(!b){
//////            SmartDashboard.putBoolean("kickerForwardHold", false);
//////        }
//////        
//////        if(controllers.operatorA())
//////        {
//////            upperStructure.setpointHome();
//////        }
//////        
//////        if(controllers.operatorY())
//////        {
//////            upperStructure.setpointForward();
//////        }
//////        
//////        if(controllers.operatorX())
//////        {
////////            upperStructure.setpointHome();
//////        }
//////        
//////        if(!controllers.operatorB() && !kicking)
//////        {
//////            pneumatics.compress();
//////        }else{
//////            pneumatics.disable();
//////        }
//////        if(controllers.operatorB()){
//////            rearArmUp = false;
//////        }
//////        
//////        if(controllers.operatorLB() || (controllers.driverStart() && !hasOperator))
//////        {
//////            if(!operatorLBPressed){
//////                operatorLBPressed = true;
//////                upperStructurePID = !upperStructurePID;
//////            }
//////        }else operatorLBPressed = false;
//////        
//////        
//////         b = false;
//////        /*if(controllers.operatorRightMove() > 0.1 || controllers.operatorRightMove() < -0.1){
//////            upperStructure.manual(controllers.operatorRightMove());
//////         
//////        }else */
//////         
//////         if(!hasOperator){
//////            if(controllers.driverSelect() && !hasOperator){
//////               upperStructure.setpointHome();
//////               kicker.set(0.4);
//////            }else{
//////                upperStructure.setpointForward();
//////            }
//////         }
//////         
//////        if(upperStructurePID){
//////            upperStructure.update();
//////            b = true;
//////            SmartDashboard.putBoolean("towerHold", true);
//////            
//////        }else if(kicking){
//////            upperStructure.manual(-0.2);
//////        }else if(Math.abs(controllers.operatorTriggers()) > 0.1 || sensorDetected){
//////            upperStructure.manual(-0.2);
//////            pressureEnabled = true;
//////            backPressureWatch.reset();
//////        }else if(pressureEnabled){
//////            if(backPressureWatch.get() > 1){
//////                pressureEnabled = false;
//////                upperStructure.manual(0);
//////            }else{
//////                upperStructure.manual(-0.2);
//////            }
//////        }else{
//////            upperStructure.manual(controllers.operatorRightMove());
//////        }
//////        if(!b){
//////            SmartDashboard.putBoolean("towerHold", false);
//////        }
//////        
////////        if(controllers.operatorLeftClick())
////////        {
////////            if(!operatorLeftClickPressed)
////////            {
////////                operatorLeftClickPressed = true;
////////                kickerWatch.reset();
////////                kicking = true;
////////            }
////////        }else operatorLeftClickPressed = false;
//////        
//////        if(controllers.operatorLeftDPAD())
//////        {
////////            if(!driverAPressed){
////////                driveTrain.shiftLow();
////////            }
//////        }
//////        if(shiftLow){
//////            driveTrain.shiftLow();
//////        }else{
//////            driveTrain.shiftHigh();
//////        }
        
//        if(controllers.operatorRightDPAD())
//        {
//            if(!driverLeftBumperPressed)
//            {
//                SmartDashboard.putBoolean("leftArmUp", true);
//                intake.rearIntakeRaise();
//            }
//            if(!driverRightBumperPressed)
//            {
//                SmartDashboard.putBoolean("rightArmUp", true);
//                intake.frontIntakeRaise();
//            }
//        }else{
            if(frontArmUp){
                SmartDashboard.putBoolean("rightArmUp", true);
                intake.frontIntakeRaise();
            }else{
                SmartDashboard.putBoolean("rightArmUp", false);
                intake.frontIntakeLower();
            }
            if(rearArmUp){
                SmartDashboard.putBoolean("leftArmUp", true);
                intake.rearIntakeRaise();
            }else{
                SmartDashboard.putBoolean("leftArmUp", false);
                intake.rearIntakeLower();
            }
////        }
        
        if(moveInversion == 1){
            lightRelay.set(Relay.Value.kForward);
        }else{
            lightRelay.set(Relay.Value.kReverse);
        }
        
            DriverStationLCD lcd = DriverStationLCD.getInstance();
        if(upperStructurePID){
            lcd.println(DriverStationLCD.Line.kUser1, 1, "PID ON PID ON PID ON PID ON");
            lcd.println(DriverStationLCD.Line.kUser2, 1, "PID ON PID ON PID ON PID ON");
            lcd.println(DriverStationLCD.Line.kUser3, 1, "PID ON PID ON PID ON PID ON");
            lcd.println(DriverStationLCD.Line.kUser4, 1, "PID ON PID ON PID ON PID ON");
            lcd.println(DriverStationLCD.Line.kUser5, 1, "PID ON PID ON PID ON PID ON");
            lcd.println(DriverStationLCD.Line.kUser6, 1, "PID ON PID ON PID ON PID ON");
        }else{
            lcd.println(DriverStationLCD.Line.kUser1, 1, "                             ");
            lcd.println(DriverStationLCD.Line.kUser2, 1, "                             ");
            lcd.println(DriverStationLCD.Line.kUser3, 1, "                             ");
            lcd.println(DriverStationLCD.Line.kUser4, 1, "                             ");
            lcd.println(DriverStationLCD.Line.kUser5, 1, "                             ");
            lcd.println(DriverStationLCD.Line.kUser6, 1, "                             ");
        }
        lcd.updateLCD();
//        if(kickerWatch.get() > 0.5) kicking = false;
//        
//        /*if(kicking && kickerWatch.get() < 0.5)
//        {
//          //  kicker.set(1);
//            backPressureWatch.reset();
//        }else */
   //     upperStructure.update();
        pneumatics.compress();
    }
}
