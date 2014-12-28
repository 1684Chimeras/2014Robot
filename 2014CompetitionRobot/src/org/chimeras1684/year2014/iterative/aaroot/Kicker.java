/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.chimeras1684.year2014.iterative.auton.root.Stopwatch;

/**
 *
 * @author Arhowk
 */
public class Kicker{
    
    /*
     * Interface for the "Kicker" object 
     * 
     * NOTE :
     *      Alot of the features of this were stripped, mainly because they were bug prone and no purpose.
     *      Now, all control is left to whoever calls set()
     * 
     * TODO :
     *      Fix hold()
     *    
     * API :
     *    construct()
     *        standard non-multiplicative constructor
     * 
     *    set(double) -> iterative
     *        Runs the arm to move at (double) speed. Positive values correlate to a kick where negative values correlate to winding up the kicker
     *         
     *    hold() -> iterative
     *        Holds the arm at the upright setpoint.
     * 
     *    disable() -> iterative
     *        Turns off the motors. Different from running at 0 because it doesn't make the motors push back
     *      
     */
    
    private final Victor leftKicker;
    private final Solenoid puncher;
    public final SPIEncoder encoderKicker;
    
    double kp_kickerHold = 0.0055; //proportional
    //double kd_kickerHold = 0.01;   //derivative
    double kv_kickerHold = 0.03;   //standing velocity
    double kg_kickerHold = 0.28;   //gravitational 
    double ki_kickerHold = 0.003;   //gravitational 
    
    double kWrap_kickerHold = 200; //degree at which the kicker wraps around
   // private double kf_kickerHold = 0.00;
    
    public Kicker(){
        leftKicker = new Victor(RobotMap.kicker);
        puncher = new Solenoid(1);
        encoderKicker = new SPIEncoder(RobotMap.SPIBus_csKicker, true);
    }
    
    public void punchOut(){
        puncher.set(true);
    }
    
    public void punchIn(){
        puncher.set(false);
    }
    
    public boolean isPunched(){
        return puncher.get();
    }
    
    public void setkP(double kP){
        this.kp_kickerHold = kP;
    }
    
    public void setHome(double homepoint){
 //       this.kh_kickerHold = homepoint;
    };
    
    double holdSpeed = 0;
    
    public double getKickerAngle(){
        double get = 1024-encoderKicker.get();
        return (double)((((double)get) * (360./1024.)) + kWrap_kickerHold) % 360;
    }
    
    public double encoderToAngle(double e){
        return (double)(e*360/1024 + kWrap_kickerHold) % 360;
    }
    
    Stopwatch integralWatch = new Stopwatch();
    double last = 0;
    double integralError = 0;
    
    public void halt(double angle, double extraForce){
        leftKicker.set(getPIDAngle(angle) + extraForce / 5);
    }
    
    public void halt(double angle){
        leftKicker.set(getPIDAngle(angle));
    }
    
    boolean softStopped = false;
    Stopwatch softStopWatch = new Stopwatch();
    
    
    public void softStop(){
        if(!softStopped){
            softStopped = true;
            softStopWatch.start();
            softStopWatch.reset();
        }
//        System.out.println("soft! value : " + (softStopWatch.get() * timeToStop));
        //set(-softStopWatch.get() / Teleoperated.timeToStop - 0.3);
    //   set(-0.); 
        halt(0);
       
        
    }
    boolean hardStop = false;
    boolean hardStopFlag = true;
    Thread pidThread = new Thread(){
        public void run(){
            while(true){
//                System.out.println("pid run");
                if(hardStop){
//                    System.out.println("hardStop");
                    hardStop = false;
                    Stopwatch s = new Stopwatch();
                    s.reset();
                    s.start();
                    while(s.get() < 1){
                        hardStopFlag = true;
//                        System.out.println("pid : " + s.get());
                        if(s.get() < 0.5){
                            halt(30);
                        }else{
                            set(-0.2);
                        }
                        hardStopFlag = false;
                        try {
                            sleep(10);
                        } catch (Exception e) {
                        }
                    }
                    hardStopFlag = true;
                }else{
                    hardStopFlag = true;
                    try {
                        sleep(10);
                    } catch (Exception e) {
                    }
                }
            }
        }
    };
    boolean hardStopEnabled = false;
    
    public void backwardPressure(){
//        System.out.println("forward");
        set(-0.15);
    }
    
    public void forwardPressure(){
//        System.out.println("forward");
        set(0.15);
    }
    
    public boolean kickerMacro(){
        
        double ang = getKickerAngle();
        System.out.println("km");
        if(softStopped && !(ang > Teleoperated.hardstopStart && ang < Teleoperated.hardstopStop)){   // if soft stop detected and already processed
            
//            System.out.println("finished!");
            System.out.println("stop");
            hardStop = true;
            if(!hardStopEnabled){
                hardStopEnabled = true;
                pidThread.start();
            }
            softStopped = false;
            return true;
            
        }else{
            if(!(ang > Teleoperated.hardstopStart && ang < Teleoperated.hardstopStop)){
//                System.out.println("not within. start : " + Teleoperated.hardstopStart);
                set(1);
                System.out.println("kick");
                softStopped = false;
            }else{
                System.out.println("softt");
                softStopped = true;
//                System.out.println("softstop");
                softStop();
            }
        }
        return false;
    }
    
    private double getPIDAngle(double angle){
        
        double encoder = getKickerAngle();
        double error = angle - encoder;
        if(error > 180){
            error -= 360;
        }else if(error < -180){
            error += 360;
        }
        
        if(!(integralWatch.get() - last > 0.3)){
            if(error > 5){
                integralError += error * (integralWatch.get() - last);
//                System.out.println("integral error : " + integralError);
            }else{
//                System.out.println("error not large enough");
                integralError = 0;
            }
        }else{
//            System.out.println("too mch timer delay : " + (integralWatch.get() - last));
            integralError = 0;
        }
        if(Math.abs(last - integralWatch.get()) > 0.1){
            last = integralWatch.get();
        }
        
        double proportional = error * kp_kickerHold;
        double gravitational = Math.cos(angle * Math.PI / 180) * kg_kickerHold;
       // double derivative = encoderKicker.getRate() * kd_kickerHold;
        double standingVelocity = encoderKicker.getRate() * kv_kickerHold;
        double integral = Math.max(-0.3,Math.min(0.3,integralError));
        
        SmartDashboard.putNumber("Set", angle);
        SmartDashboard.putNumber("Error", error);
        SmartDashboard.putNumber("Encoder", encoder);
        SmartDashboard.putNumber("Angle", encoderToAngle(encoder));
        SmartDashboard.putNumber("Prop", proportional);
        SmartDashboard.putNumber("Grav", gravitational);
        SmartDashboard.putNumber("SV", standingVelocity);
        SmartDashboard.putNumber("Integral", integral);
        SmartDashboard.putNumber("Final", proportional + gravitational - standingVelocity + integral);
        
        return Math.max(-0.7,Math.min(0.7,proportional + gravitational - standingVelocity + integral));
    }
    
    public double getEncoder(){
        return encoderKicker.get();
    }
    
    public void set(double val){
//        val = -val;
        if(hardStopFlag){
            if(val > -0.05 && val < 0.05){
                leftKicker.disable();
            }else{
    //            System.out.println("set: " + val);
                leftKicker.set(val);
            
            }
        }
    }
    
    public void disable(){
        leftKicker.disable();
    }

}
