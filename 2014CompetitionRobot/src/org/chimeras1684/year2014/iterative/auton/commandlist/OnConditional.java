/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.commandlist;

import org.chimeras1684.year2014.iterative.aaroot.WaitAuton;
import org.chimeras1684.year2014.iterative.auton.root.OnRequest;

/**
 *
 * @author Arhowk
 */
public class OnConditional extends OnRequest{
    //Cast from float to int so you cant pass conditions as requests and vice versa
    
    public static boolean checkConditional(int request, double arg)
    {
        switch(request)
        {
            case (int) ifKickerMacro:
                return kicker.kickerMacro();
                
            case (int) ifStartpointCenter:
                return auton.isStartingCenter();
                
            case (int) ifStartpointLeft:
                return auton.isStartingLeft();
               
            case (int) ifStartpointRight:
                return auton.isStartingRight();
                
            case (int) ifWait:
                //return WaitAuton.waitSeconds(arg);
                return false;
                
            case (int) ifDriveFeet:
                return drive.runPID();
                
            case (int) ifFalse:
                return false;
                
            case (int) ifToTheLeft:
                return targeting.toTheLeft();
                
            case (int) ifToTheRight:
                return targeting.toTheRight();
                
            case (int) ifInTheCenter:
                return targeting.inTheCenter();
                
            default:
                return true;
        }
    }
    public static void whileStart(int request, double arg)
    {
        
        switch(request)
        {
            case (int) ifDriveFeet:
                if(arg == 0) throwNoArgError("DriveFeet");
                drive.initDrivePID(arg*12);
                break;
                
        }
    }
    
    public static void whileEnd(int request, double arg)
    {
        switch(request)
        {
            case (int) ifDriveFeet:
                drive.arcadeDrive(0,0,false);
                break;
              
            case (int) ifKickerMacro:
                kicker.set(0);
                break;
        }
    }
    
}
