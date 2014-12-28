
package org.chimeras1684.year2014.iterative.aaroot;


import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

/*
 * @author Arhowk
*/

public class RobotTemplate extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    Targeting targeting = null;
    
    Compressor compressor;
    XboxControllers controllers;
    
    DriveTrain driveTrain;
    Intake intake;
    Kicker kicker;
    UpperStructure upperStructure;
    
    Teleoperated teleop;
    Autonomous auton;
    
        DeadSwitch dead = new DeadSwitch(4);
    Vector subTables;
    Vector prevValues;
    int gchk = 0;
    int fchs = 0;
    
    class StringIntPair{
        String str;
        int i;
    }
    
    public void robotInit() 
    {
        
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser1, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser3, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser4, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser5, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, "DONT ENABLE ROBOT DONT ENABLE ROBOT");
        
        DriverStationLCD.getInstance().updateLCD();
        
        //NO DEPENDENCY OBJECTS
        controllers = new XboxControllers(1,2);
        compressor = new Compressor();
        driveTrain = new DriveTrain();
        intake = new Intake();
        targeting = new Targeting();
        kicker = new Kicker();
        upperStructure = new UpperStructure();
        
        //DEPENDENCY OBJECTS
        teleop = new Teleoperated(targeting, compressor, driveTrain, intake, kicker, upperStructure, controllers);
        auton = new Autonomous(true, compressor,driveTrain, kicker, intake, upperStructure, targeting);
        
        //AUTON INITIALIZATION
        auton.initFromFiles();
        auton.toggleAuton();
        auton.toggleStartingPosition();
        autonTable = NetworkTable.getTable("auton");
        subTables = new Vector();
        
        autonTable.addSubTableListener(new ITableListener() {
            public void valueChanged(ITable itable, final String string, Object o, boolean bln) {
                subTables.addElement(string);
//                new Thread(){
//                    public void run(){
//                        try{
//                            System.out.println("sleeping");
//                            sleep(100);
//                            System.out.println("waking up");
//                            auton.add(new AutonMode(string));
//                            System.out.println("fin");
//                        }catch(Exception e){e.printStackTrace();}
//                    }
//                }.start();
            }
        });
        updateConfigFile();
        autonThread.start();
        telopThread.start();
        
        SmartDashboard.putNumber("Trigger Scale", 0.3);
        
    }

    public void updateConfigFile(){
        
        FileRead f = new FileRead("config.txt");
        kicker.setkP(f.getDouble("kicker-kP"));
        kicker.kg_kickerHold = f.getDouble("kicker-kg");
        kicker.kv_kickerHold = f.getDouble("kicker-kv");
        kicker.ki_kickerHold = f.getDouble("kicker-ki");
        kicker.setHome(f.getDouble("kicker-home"));
        kicker.kWrap_kickerHold = f.getDouble("kicker-wrap");
        
        upperStructure.setkP(f.getDouble("upper-kP"));
        upperStructure.setkI(f.getDouble("upper-kI"));
        upperStructure.setkD(f.getDouble("upper-kD"));
        upperStructure.setkG(f.getDouble("upper-kG"));
        upperStructure.setHome((int)f.getDouble("upper-home"));
        upperStructure.setBack((int)f.getDouble("upper-back"));
        upperStructure.setFront((int)f.getDouble("upper-front"));
//        upperStructure.setFront(113);
        upperStructure.setpointBackward();
    }
    
    public void updateDashboard()
    {
     
//        SmartDashboard.putBoolean("Front Sensor", driveTrain.getFrontLineSensor());
        SmartDashboard.putBoolean("Rear Sensor", driveTrain.getRearLineSensor());
        SmartDashboard.putBoolean("Front Intake", intake.getFrontIntake());
        SmartDashboard.putBoolean("Rear Sensor", intake.getRearIntake());
        
        SmartDashboard.putBoolean("Shifted", driveTrain.isShifted());
                
        if(driveTrain.isHighGear()){
            SmartDashboard.putString("Shift", "SHIFT HIGH");
        }else{
            SmartDashboard.putString("Shift", "SHIFT LOW");
        }   
        
        SmartDashboard.putNumber("Upper Structure", upperStructure.positionEncoder.get());
        SmartDashboard.putNumber("Kicker", kicker.getKickerAngle());
        SmartDashboard.putNumber("Kicker2", kicker.getKickerAngle()+151);
        SmartDashboard.putNumber("Drive Left", driveTrain.getLeftDrive());
        SmartDashboard.putNumber("Drive Right", driveTrain.getRightDrive());
        SmartDashboard.putNumber("connection", Math.sin(Timer.getFPGATimestamp()));
    }
    
    public void updateAutons(){
        
        auton.clear();
        Enumeration e = subTables.elements();
        while(e.hasMoreElements()){
            String s = (String) e.nextElement();
            NetworkTable t = NetworkTable.getTable("auton/"+s);
            if(t.getNumber("CHK", 0) == gchk){
                auton.add(new AutonMode(s));
            }
        }
        auton.toggleAuton();
        
    }
    
    public void zeroMotors(){
        runTelop = false;
        kicker.set(0);
        driveTrain.arcadeDrive(0, 0, false);
        intake.disableRollers();
        intake.rearIntakeRaise();
        intake.frontIntakeRaise();
        upperStructure.manual(0);
    }
    
    public void autonomousInit()
    {
        System.out.println("Auton!");
        zeroMotors();
        updateConfigFile();
        runAuton = true;
        auton.init();
    }
    
    
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
//        auton.update();
//        updateDashboard();
    }

    public void teleopInit()
    {
        runTelop = true;
        System.out.println("Teleop!");
        driveTrain.resetEncoders();
        updateConfigFile();
        teleop.init();
    }
    
    /**
     * This function is called periodically during operator control
     */
    boolean runTelop = false;
    Thread telopThread = new Thread(){
        public void run(){
            while(true){
                if(runTelop){
                    teleop.update(dead.good());
                    updateDashboard();//teleopPeriodic();
                }
                try{
                    sleep(10);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
    boolean runAuton = false;
    Thread autonThread = new Thread(){
        public void run(){
            while(true){
                if(runAuton){
                    auton.update();
//                    updateDashboard();//teleopPeriodic();
                }
                try{
                    sleep(10);
                }catch(Exception e){
                    
                }
            }
        }
    };
    public void teleopPeriodic() 
    {
//        teleop.update();
//        updateDashboard();
    }
    
    public void disabledInit()
    {
        zeroMotors();
        runAuton = false;
        runTelop = false;
        System.out.println("Disabled!");
        dead.disabledInit();
    }
    
    
    /**
     * This function is called periodically during disabled mode
     */
    boolean driverStartPressed = false;
    boolean driverSelectPressed = false;
    boolean driverAPressed = false;
    boolean driverYPressed = false;
    boolean driverXPressed = false;
    boolean driverBPressed = false;
    boolean driverLDPADPressed = false;
    boolean driverRDPADPressed = false;
    int inc = 0;
    NetworkTable autonTable;
    
    public void disabledPeriodic() 
    {
        updateDashboard();
        auton.updateDisabled();
        if(autonTable.getNumber("GCHK", gchk) != gchk){
            gchk = (int)autonTable.getNumber("GCHK", gchk);
            System.out.println("Updating Autons");
            updateAutons();
            auton.orderAutons();
            if(autonTable.getNumber("FCHS", fchs) != fchs){
                System.out.println("Saving Autons");
                auton.saveAutons();
                fchs = (int) autonTable.getNumber("FCHS", fchs);
            }
        }
        
//        System.out.println("Disabled Periodic");
        
        if(controllers.driverLeftClick()){
            teleop.hasOperator = false;
        }
        if(controllers.driverRightClick()){
            teleop.hasOperator = true;
        }
        
        
        if(controllers.driverA())
        {
            if(!driverAPressed)
            {
                driverAPressed = true;
                auton.incrementDelay(-1);
            }
        }else driverAPressed = false;
        
        if(controllers.driverB())
        {
            if(!driverBPressed)
            {
                driverBPressed = true;
                auton.incrementDelay(-0.1);
            }
        }else driverBPressed = false;
        
        if(controllers.driverY())
        {
            if(!driverYPressed)
            {
                driverYPressed = true;
                auton.incrementDelay(1);
            }
        }else driverYPressed = false;
        
        if(controllers.driverX())
        {
            if(!driverXPressed)
            {
                driverXPressed = true;
                auton.incrementDelay(0.1);
            }
        }else driverXPressed = false;
        
        if(controllers.driverStart())
        {
            if(!driverStartPressed)
            {
                driverStartPressed = true;
                auton.toggleAuton();
            }
        }else driverStartPressed = false;
        
        if(controllers.driverSelect())
        {
            if(!driverSelectPressed)
            {
                driverSelectPressed = true;
                auton.reverseAuton();
            }
        }else driverSelectPressed = false;
        
        if(controllers.driverLeftDPAD())
        {
            if(!driverLDPADPressed)
            {
                driverLDPADPressed = true;
                auton.reversePosition();
            }
        }else driverLDPADPressed = false;
        
        if(controllers.driverRightDPAD())
        {
            if(!driverRDPADPressed)
            {
                driverRDPADPressed = true;
                auton.toggleStartingPosition();
            }
        }else driverRDPADPressed = false;
        
            
        //if(Math.abs(controllers.driverLeftMove()) > 0.1){
        //    auton.incrementDelay(controllers.driverLeftMove() / (1/50));
        //}
        
        
        double delay = auton.currentDelay;
        String print;
        if(delay == (int)delay){
            print = ""+(int)delay;
        }else{
            print = ""+delay;
            try{
                String twoAfterDecimal = print.substring(print.indexOf(".")+2, print.indexOf(".")+3);
                if(twoAfterDecimal.equalsIgnoreCase("9")){
                    int oneAfterDecimal = Integer.parseInt(print.substring(print.indexOf(".")+1, print.indexOf(".")+2));
                    if(++oneAfterDecimal == 10){
                        int beforeDecimal = Integer.parseInt(print.substring(0, print.indexOf(".")));
                        print = ""+(++beforeDecimal);
                    }
                }
            }catch(Exception e){
                
            }
        }
        if(auton.currentDelay > 0){
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser5, 1, "Delay : " + print + auton.spaces);
        }else{
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser5, 1, auton.spaces + auton.spaces);
        }
        
        
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser4, 1, "Deadband: " + dead.pollForPressed() + "     ");
        
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, "Operator: " + teleop.hasOperator + "              ");
        DriverStationLCD.getInstance().updateLCD();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {
       // compressor.compress();
    }
    
    public void practicePeriodic()
    {
        
    }
}
