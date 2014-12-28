


package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.DriverStationLCD;
import org.chimeras1684.year2014.iterative.aaroot.fileio.BufferedReader;
import org.chimeras1684.year2014.iterative.aaroot.fileio.BufferedWriter;
import org.chimeras1684.year2014.iterative.auton.available.BloomfieldComp_OneBall;
import org.chimeras1684.year2014.iterative.auton.available.BloomfieldComp_TwoBall;
import org.chimeras1684.year2014.iterative.auton.root.AutonFramework.TimedCommandGroup;
import org.chimeras1684.year2014.iterative.auton.root.OnRequest;
import org.chimeras1684.year2014.iterative.auton.root.Stopwatch;
/**
 * 
 * 
 * API for the Auton class
 * 
 *  Usage :
 *      Adding an auton:
 *
 *      Toggling Autons:
 *          1) call "toggleAuton"
 *          2) It will automatically go through all the autons and show them on the DS
 *         
 *      Calling Auton:
 *          1) on autonomousInit, call auton.initialize()
 *          2) during autonomousPeriodic, call auton.execute()
 *
 *  TODO :   
 * 
 * @author Arhowk
*/

public class Autonomous {
    
    //ENTER AUTONS HERE
    
    private AutonMode[] autons;
//    private RobotTemplate host;
    
    //private int addingAutonIndex = 0;
    private int autonIndex = -1;
    private int positionIndex = -1;
    
    double currentDelay  = 0;
    
    Stopwatch delayWatch = null;
    boolean initialized = false, useHardcoded = false;
    
    public Autonomous (boolean useHardcoded, Compressor c, DriveTrain d, Kicker k, Intake i, UpperStructure u, Targeting t)
    {
        this.useHardcoded = useHardcoded;
        OnRequest.initialize(this, c, d, t, k, i, u);
        autons = new AutonMode[0];
        addAutons();
    }
    
    public void init()
    {
        if(autons.length > 0){
            initialized = false;
            if(currentDelay > 0 ){
                if(delayWatch == null) delayWatch = new Stopwatch();
                delayWatch.start();
                delayWatch.reset();

            }else{
                initialized = true;
                autons[autonIndex].toAutonSequence().initialize();
            }
        }
    }

    /**
     * update el auton
     */
    public void update()
    {
        if(autons.length > 0){
            if(currentDelay > 0){
                if(currentDelay > delayWatch.get()){
                    return;
                }
            }
            if(!initialized){
                initialized = true;
                autons[autonIndex].toAutonSequence().initialize();
            }
            autons[autonIndex].toAutonSequence().execute();
        }
    }
    
    public void updatePosition()
    {
        if(positionIndex == 0){
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, "Startpoint: Left" + spaces);
        }else if(positionIndex == 1){
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, "Startpoint: Center" + spaces);
        }else if(positionIndex == 2){
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, "Startpoint: Right" + spaces);
        }
    }
    
    public void updateDisabled(){
        if(autons.length != 0)
        autons[autonIndex].updateDS();
    }
    
    public void clear(){
        if(useHardcoded) return;
        autons = new AutonMode[0];
        autonIndex = -1;
    }
    
    public void add(AutonMode mode){
        autons = extendArray(autons);
        autons[autons.length-1] = mode;
    }
    
    public void incrementDelay(double amount)
    {
        currentDelay += amount;
        if     (currentDelay < 0)  currentDelay = 0;
        else if(currentDelay > 10) currentDelay = 10;
    }
    
    public void toggleStartingPosition(){
        if(++positionIndex > 2){
            positionIndex = 0;
        }
       updatePosition();
    }
    
    public boolean isStartingCenter(){
        return positionIndex == 1;
    }
    public boolean isStartingLeft(){
        return positionIndex == 0;
    }
    public boolean isStartingRight(){
        return positionIndex == 2;
    }
    
    /**
     * Toggles the autons through the array
     */
    public void toggleAuton(){
        try{
            if(++autonIndex >= autons.length){
                autonIndex = 0;
            }
        }catch(Exception e){
            autonIndex = 0;
        }
    }
    
    public void reverseAuton(){
        try{
            if(--autonIndex < 0){
                autonIndex = autons.length - 1;
            }
        }catch(Exception e){
            
        }
    }
    
    public void initFromFiles(){
        if(useHardcoded) return;
        System.out.println("Loading Autons From Files");
        BufferedReader reader = new BufferedReader("autonList.txt");
        clear();
        String [] s = new String[100];
        
        for(int i = 0; i < 100; i++){
            s[i] = reader.readLine();
            if(s[i].length() == 0) break;
        }
        for(int i = 0; i < 15; i++){
            String next = s[i];
            if(next != null){
                if(next.length() > 0){
                    AutonMode mode = AutonMode.loadFromFile(next);
                    
                    if(mode != null){
                        add(mode);
                    }
                }
            }else{
                break;
            }
        }
        
//        orderAutons();
    }
    
    public void saveAutons(){
        if(useHardcoded) return;
        System.out.println("Saving Autons To file auton/autonList.txt");
        BufferedWriter writer = new BufferedWriter("autonList.txt");
        for(int i = 0; i < autons.length; i++){
            writer.pushLine(autons[i].name);
        }
        writer.close();
        for(int i = 0; i < autons.length; i++){
            
            autons[i].forceSave();
        }
    }
    
    public void orderAutons(){
        AutonMode[] newModes = new AutonMode[autons.length];
        AutonMode[] tempModes = new AutonMode[autons.length];
        int pos = 0;
        for(int i = 0; i < autons.length; i++){
            try{
                int id = Integer.parseInt(getWord(autons[i].name,0));
                newModes[id] = autons[i];
            }catch(Exception e){
                tempModes[pos++] = autons[i];
            }
        }
        for(int i = 0; i < pos; i++){
            for(int i2 = 0; i2 < newModes.length; i2++){
                if(newModes[i2] == null){
                    newModes[i2] = tempModes[i];
                }
            }
        }
        autons = newModes;
    }
    
    public void reversePosition(){
        if(--positionIndex < 0){
            positionIndex = 2;
        }
        updatePosition();
    }
    String spaces = "                                               ";
    
    
    private String getWord(String s, int wordNo){
        int prevWhite = -1;
        int numWords = 0;
        int lastSpace = 0;
        for(int i = 0; i < s.length(); i++){
            char proc = s.charAt(i);
            if(proc == ' ' || proc == ' '){
                if(prevWhite == i - 1){
                    prevWhite = i;
                }else{
                    if(wordNo == numWords){
                        return s.substring(lastSpace, i).trim();
                    }else{
                        lastSpace = i;
                        numWords++;
                    }
                }
            }
            if(i == s.length() - 1){
                return s.substring(lastSpace, s.length()).trim();
            }
        }
        return "";
    }
    
    
    private AutonMode[] extendArray(AutonMode[] l){
        AutonMode[] n = new AutonMode[l.length+1];
        System.arraycopy(l,0,n,0,l.length);
        return n;
    }
    
    public void addAuton(TimedCommandGroup g, String s, String b, String c, String d){
        add(AutonMode.loadStatic(g, new String[]{s+spaces,b+spaces,c+spaces,d+spaces}));
    }
    
    private void addAutons(){
        
        addAuton(new BloomfieldComp_OneBall(), "0 One Ball",
                 "Drive forward",
                 "Shoot.",
                 "");
        
        addAuton(new BloomfieldComp_TwoBall(), "1 Two Ball",
                 "Drag Pickup",
                 "like old two ball",
                 "but new since kelly sux");
        
//        addAuton(new A_KickMoveTargeting(), "2 Move and Kick Loaded",
//                 "Kicks and drives",
//                 "Uses camera",
//                 "Without PID Drive");
//        
//        addAuton(new A_KickMoveTargetingPID(), "3 Move and Kick PID",
//                 "Kicks and drives",
//                 "Uses camera",
//                 "PID Kicker");
//        
//        addAuton(new A_KickLoadKick(), "4 Kick Load Kick",
//                 "Doesnt use camera",
//                 "(not enough time)",
//                 "Uses PID Drive");
//        
//        addAuton(new A_KickLoadKickTargeting(), "5 KLK Targeting",
//                 "Kick Load Kick",
//                 "(from rear pickup)",
//                 "PID drive + camera");
//        
//        addAuton(new A_TurnDrive(), "6 Turn Drive",
//                 "Turn left or right",
//                 "Drive forward",
//                 "Used if not shooting");
    }
    
}
