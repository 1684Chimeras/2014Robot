/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.root;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Arhowk
 */
public class Stopwatch {
    /* all times are in seconds */
    
    double startTime = 0;
    double stopTime = 0;
    boolean stopped = true;
    boolean started = false;
    
    public Stopwatch(){}
    
    public void start(){
        if(!stopped) return;
        stopped = false;
        startTime = (Timer.getFPGATimestamp() - stopTime);
        stopTime = 0;
    }
    
    public void stop(){
        if(stopped) return;
        stopTime = get();
        stopped = true;
    }
    
    public void reset(){
        startTime = Timer.getFPGATimestamp();
    }
    
    public double get(){
        if(stopped){
            return stopTime;
        }else{
            return Timer.getFPGATimestamp() - startTime;
        }
    }
    
    public void skip(double seconds){
        startTime -= seconds;
    }
    
}
