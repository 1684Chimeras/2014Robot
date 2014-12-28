/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.root;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Enumeration;
import java.util.Vector;
import org.chimeras1684.year2014.iterative.aaroot.FileRead;
import org.chimeras1684.year2014.iterative.auton.commandlist.OnConditional;
import org.chimeras1684.year2014.iterative.auton.commandlist.OnRequestEnd;
import org.chimeras1684.year2014.iterative.auton.commandlist.OnRequestInit;
import org.chimeras1684.year2014.iterative.auton.commandlist.OnRequestPeriodic;
import org.chimeras1684.year2014.iterative.auton.commandlist.RequestConstants;

/**
 *
 * @author Arhowk
 * 
 * API
 *  Functions
 *          add(int requestType -> check RequestConstant.java for all the types of requests
 *              optional double arg -> Passes an argument to the request, ex. shooter speed or drive distance
 *                  OR optional string arg -> Passes a key to read from \auton\config.txt
 *              double startTime -> what time in the auton period to begin. ex. 3 will start at the 3 second mark
 *              double duration -> how long to run the command. 
 * 
 *          addIf(float condition -> constant corresponding to the boolean to return
 *              addElseIf(float condition)
 *              addElse()
 *              addEndIf()
 * 
 *          addWhile(float conditon -> constant corresponding to the boolean. Shouldn't be something like true or false, something that will change after a few seconds
 *                  optional double arg -> Passes an argument to the request, ex. shooter speed or drive distance
 *                  OR optional string arg -> Passes a key to read from \auton\config.txt
 *                  double startTime -> what time to start. No duration, will stop nce it returns false
 *                  double timeout -> max amount of time to run
 * 
 *              addPause() -> indicates a while block
 *              addEndPause() -> ends the block. If a while returned false, the timer will stop and the command will exit untill next iteration
 *              addPauseWhile(...) -> same as addWhile, except it automatically includes a pause and endpause
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

public class AutonFramework {
    
    public static abstract class TimedCommandGroup implements RequestConstants{
        
        private final Vector    storageVector; //Used to store all of the date about the commands to be run.   
        
        private final Stopwatch loopTimer;   //Main timer for getting times to execute commands correctly

        private FileRead file;
        private boolean finished = false; 
        private boolean loopTimerStopped = false;
        
        public TimedCommandGroup()
        {
            storageVector = new Vector();
            loopTimer = new Stopwatch();
        }
        
        public boolean isFinished()
        {
            return finished;
        }
        boolean init = false;
        public void execute()
        {
            if(!init){
                loopTimer.start();
                loopTimer.reset();
                init = true;
            }
            if(finished) return;
            SmartDashboard.putNumber("loopTimer", loopTimer.get());
            int numOfRunningCommands = 0; //Used to make sure that it still has commands to be run and doesn't loop forever
            long nestedIfs = 0;
            long nestedIfFalse = 0;
            long nestedIfTrue = 0;
            long nestedPauseStop = 0;
            long nestedPause = 0;
            
            Enumeration runningCommandsIteration = storageVector.elements(); //List of objects in the storage hashtable
            Structure nextCommand; //Temporary storage for the next object in the hash
            while(runningCommandsIteration.hasMoreElements()){ 
                nextCommand = (Structure)(runningCommandsIteration.nextElement()); //Retrieves the next element Object and typecasts it to TimedCommnad
                
                if(nextCommand.conditionalStatus != 0)
                {
                    Conditional cond = (Conditional)nextCommand.command;
                    switch(nextCommand.conditionalStatus)
                    {
                        case Structure.conditionalPause:
                            nestedPause++;
                            break;
                            
                        case Structure.conditionalEndPause:
                            if(nestedPauseStop >> (nestedPause - 1) == 1)
                            {
                                loopTimer.stop();
                                loopTimerStopped = true;
                                return;
                            }else{
                                nestedPause--;
                                nestedPauseStop = nestedPauseStop &~ (1 << nestedPause);
                                if(loopTimerStopped && nestedPauseStop == 0)
                                {
                                    loopTimer.start();
                                    loopTimerStopped = false;
                                }
                            }
                            break;
                            
                        case Structure.conditionalWhile:
                            if(nestedIfFalse == 0 //No nested if false?
                                    && !nextCommand.finished //Not finished?
                                    && nextCommand.start < loopTimer.get()  //Time is proper?
                                    && !cond.shouldRun(file)){ //Run the command. If it pauses, than pause everything else.
                                numOfRunningCommands++;
                                nestedPauseStop = nestedPauseStop | (1 << (nestedPause-1));
                            }else if(!nextCommand.finished && //Else if it should've run but didnt the command returned false
                                    nextCommand.start < loopTimer.get()){ //so disable the while.
                                
                                nextCommand.finished = true;
                            }else{
                            }
                            break;
                        case Structure.conditionalIf:
                            if(!cond.shouldRun(file))
                            {
                                nestedIfFalse = nestedIfFalse | (1 << nestedIfs);
                            }else{
                                nestedIfTrue = nestedIfTrue | (1 << nestedIfs);
                            }
                            
                            nestedIfs++;
                            break;
                            
                        case Structure.conditionalEndIf:
                            nestedIfFalse = nestedIfFalse &~ (1 << (nestedIfs - 1)); //removes the bit from nestedIfFalse
                            nestedIfTrue = nestedIfTrue &~ (1 << (nestedIfs - 1));
                            
                            nestedIfs--;
                            break;
                            
                        case Structure.conditionalElse:
                            /*if(nestedIfFalse >> (nestedIfs - 1) == 1)
                            {
                                nestedIfFalse = nestedIfFalse &~ (1 << (nestedIfs - 1));
                            }else{
                                nestedIfFalse = nestedIfFalse | (1 << (nestedIfs-1));
                            }*/
                            break;
                            
                        case Structure.conditionalElseIf:
                            if(nestedIfTrue >> (nestedIfs - 1) == 0){ ///making sure that another else wasn't returned.
                                if(nestedIfFalse >> (nestedIfs - 1) == 1)
                                {
                                    if(cond.shouldRun(file))
                                    {
                                        nestedIfFalse = nestedIfFalse &~ (1 << (nestedIfs - 1));
                                        nestedIfTrue = nestedIfTrue | (1 << (nestedIfs - 1));
                                    }
                                }else if(nestedIfFalse >> (nestedIfs - 1) == 0){
                                    nestedIfFalse = nestedIfFalse | (1 << (nestedIfs-1));
                                }
                            }else{
                                nestedIfFalse = nestedIfFalse | (1 << (nestedIfs-1));
                            }
                            
                            break;
                            
                        default:
                            System.out.println("[AUTON] Conditional type passed that does not exist. O.O");
                    }
                    
                }else if(nestedIfFalse == 0 //no false if statements
                        && loopTimer.get() < nextCommand.timeout + nextCommand.start){ //within time frame
                    
                    if(loopTimer.get() > nextCommand.start){ //if the command should be running,
                        if(nextCommand.command == null)
                        {
                            System.out.println("[AUTON] Null Command Passed! Woops. How'd that happen? Report to Jake");
                        }else{
                            nextCommand.command.execute(file);
                            numOfRunningCommands += 1;
                        }
                        nextCommand.hasStarted = true;
                    }else{ //else, the command would be running but it hasn't met its start point so the tcg should still run because it hasnt run that command yet
                        numOfRunningCommands += 1;
                    }
                    
                }else if(nextCommand.hasStarted && !nextCommand.hasRunFinale && nextCommand.timeout + nextCommand.start < loopTimer.get()){
                    nextCommand.hasRunFinale = true;
                    if(nextCommand.command != null)
                    {
                        nextCommand.command.end();
                    }
                }
                    
            }
            if(numOfRunningCommands == 0)
            {
                finished = true; //If theres no commands left, exit
            }
        }
       
        public void initialize(){
            loopTimer.start();
            loopTimerStopped = false;
            loopTimer.reset();
            finished = false;
            init = false;
            
            Enumeration e = storageVector.elements();
            file = new FileRead("auton.txt");
            while(e.hasMoreElements())
            {
                Structure tempStorage = (Structure)e.nextElement();
         //       if(tempStorage.command != null) tempStorage.command.end();
                tempStorage.reset();
            }
            
        }
        
        protected void add(final int command, double start, double timeout)
        {
            Structure tcmd = new Structure();
            tcmd.start = start;
            tcmd.timeout = timeout;
            tcmd.command = new Request(command);
            storageVector.addElement(tcmd);
        }
        protected void add(final int command, double arg, double start, double timeout)
        {
            Structure tcmd = new Structure();
            tcmd.start = start;
            tcmd.timeout = timeout;
            tcmd.command = new Request(command,arg);
            storageVector.addElement(tcmd);
        }
        protected void add(final int command, String arg, double start, double timeout)
        {
            Structure tcmd = new Structure();
            tcmd.start = start;
            tcmd.timeout = timeout;
            tcmd.command = new Request(command,arg);
            storageVector.addElement(tcmd);
        }
        protected void addIf(final float condition)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalIf;
            tcmd.command = new ConditionalRequest(condition,0,false,999);
            storageVector.addElement(tcmd);
        }
        protected void addElseIf(final float condition)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalElseIf;
            tcmd.command = new ConditionalRequest(condition,0,false,999);
            storageVector.addElement(tcmd);
        }
        protected void addElse()
        {
           addElseIf(RequestConstants.ifTrue);
        }
        protected void addEndIf()
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalEndIf;
            storageVector.addElement(tcmd);
        }
        protected void addWhile(final float command, double start)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalWhile;
            tcmd.start = start;
            tcmd.command = new ConditionalRequest(command,0,true,999);
            storageVector.addElement(tcmd);
        }
        protected void addWhile(final float command, double arg, double start)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalWhile;
            tcmd.start = start;
            tcmd.command = new ConditionalRequest(command,arg,true,999);
            storageVector.addElement(tcmd);
        }
        protected void addWhile(final float command, String arg, double start)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalWhile;
            tcmd.start = start;
            tcmd.command = new ConditionalRequest(command,arg,true,999);
            storageVector.addElement(tcmd);
        }
        protected void addWhile(final float command, double arg, double start, double maxDuration)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalWhile;
            tcmd.start = start;
            tcmd.command = new ConditionalRequest(command,arg,true,maxDuration);
            storageVector.addElement(tcmd);
        }
        protected void addWhile(final float command, String arg, double start, double maxDuration)
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalWhile;
            tcmd.start = start;
            tcmd.command = new ConditionalRequest(command,arg,true,maxDuration);
            storageVector.addElement(tcmd);
        }
        protected void addPauseWhile(final float command, double arg, double start, double maxDuration)
        {
            addPause();
            addWhile(command, arg, start, maxDuration);
            addEndPause();
        }
        protected void addPauseWhile(final float command, double start, double maxDuration)
        {
            addPause();
            addWhile(command, start, maxDuration);
            addEndPause();
        }
        protected void addPauseWhile(final float command, String arg, double start, double maxDuration)
        {
            addPause();
            addWhile(command, arg, start, maxDuration);
            addEndPause();
        }
        protected void addPause()
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalPause;
            storageVector.addElement(tcmd);
        }
        protected void addEndPause()
        {
            Structure tcmd = new Structure();
            tcmd.conditionalStatus = Structure.conditionalEndPause;
            storageVector.addElement(tcmd);
        }
        
        private class Structure
        {
            public double start;
            public double timeout;
            public AutonCommand command;
            public boolean hasStarted = false;
            boolean hasRunFinale;
            public int conditionalStatus = 0;
            public boolean finished = false;
            
            public void reset()
            {
                hasStarted = false;
                hasRunFinale = false;
                finished = false;
            }
            
            public static final int 
                    conditionalIf = 1,
                    conditionalElseIf = 2,
                    conditionalElse = 3,
                    conditionalEndIf = 4,
                    conditionalPause = 5,
                    conditionalEndPause = 6,
                    conditionalWhile = 7,
                    
                    END = 9999;
        }
    }
    
    public static class Request extends AutonCommand{
        int embeddedRequest;
        boolean returnFinished;
        boolean ranStart = false;
        double arg;
        String key = "error";
        
        public Request(int request)
        {
            embeddedRequest = request;
            arg=0;
        }
        public Request(int request, double arg)
        {
            embeddedRequest = request;
            this.arg = arg;
        }
        public Request(int request, String key)
        {
            embeddedRequest = request;
            this.key = key;
        }
        
        public void end()
        {
            OnRequestEnd.onRequestFinish(embeddedRequest,arg);
            ranStart = false;
        }
        
        public void execute(FileRead f) 
        {
            if(!ranStart)
            {
                if(!key.equalsIgnoreCase("error")){
                    arg = f.getDouble(key);
                }
                OnRequestInit.onRequestStart(embeddedRequest,arg);
                ranStart = true;
            }
            OnRequestPeriodic.runRequest(embeddedRequest,arg);
        }
    }
    
    public static class ConditionalRequest extends Conditional{
        
        float type;
        double arg;
        double timeout;
        
        boolean isWhile;
        boolean hasInit;
        Stopwatch watch;
        String key = "error";
        
        public ConditionalRequest(float id, double arg, boolean isWhile, double whileTimeout)
        {
            this.isWhile = isWhile;
            this.type    = id;
            this.arg     = arg;
            this.hasInit = false;
            this.timeout = whileTimeout;
            if(isWhile)
            {
                watch = new Stopwatch();
            }
        }
        public ConditionalRequest(float id, String arg, boolean isWhile, double whileTimeout)
        {
            this.isWhile = isWhile;
            this.type    = id;
            this.key     = arg;
            this.hasInit = false;
            this.timeout = whileTimeout;
            if(isWhile)
            {
                watch = new Stopwatch();
            }
        }


        public void setKey(String key){
            this.key= key;
        }
        public void end(){
            this.hasInit = false;
            OnConditional.whileEnd((int)type, arg);
        }
        public boolean shouldRun(FileRead file) 
        {
            if(isWhile)
            {
                watch.start();
                if(!hasInit)
                {
                    watch.reset();
                    if(!key.equalsIgnoreCase("error")){
                        arg = file.getDouble(key);
                    }
                    OnConditional.whileStart((int)type, arg);
                    hasInit = true;
                }
                if(OnConditional.checkConditional((int)type,arg) || watch.get() > timeout)
                {
                    watch.stop();
                    hasInit = false;
                    OnConditional.whileEnd((int)type, arg);
                    return true;
                }
                return false;
            }else{
                return OnConditional.checkConditional((int)type,arg);
            }
        }
        
    }
    
    public static abstract class AutonCommand{
        public abstract void execute(FileRead f);
        public abstract void end();
    }
    
    public static abstract class Conditional extends AutonCommand{
        public abstract boolean shouldRun(FileRead f);
        
        public abstract void end();
        public void execute(FileRead f){};
        public void initialize(){};
    }
    
}
