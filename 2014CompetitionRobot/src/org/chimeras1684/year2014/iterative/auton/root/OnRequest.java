/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chimeras1684.year2014.iterative.auton.root;

import org.chimeras1684.year2014.iterative.aaroot.Autonomous;
import org.chimeras1684.year2014.iterative.aaroot.Compressor;
import org.chimeras1684.year2014.iterative.aaroot.DriveTrain;
import org.chimeras1684.year2014.iterative.aaroot.Intake;
import org.chimeras1684.year2014.iterative.aaroot.Kicker;
import org.chimeras1684.year2014.iterative.aaroot.Targeting;
import org.chimeras1684.year2014.iterative.aaroot.UpperStructure;
import org.chimeras1684.year2014.iterative.auton.commandlist.RequestConstants;

/**
 *
 * @author Arhowk
 */
public class OnRequest implements RequestConstants{
    
    protected static DriveTrain drive;
    protected static Targeting targeting;
    protected static Kicker kicker;
    protected static Compressor compressor;
    protected static Intake intake;
    protected static UpperStructure upperStructure;
    protected static Autonomous auton;
    
    public static void throwNoArgError(String fieldName)
    {
        System.out.println("[AUTON] Error : Arg passed as 0 for field " + fieldName);
    }
    
    public static void initialize(Autonomous a, Compressor c, DriveTrain d, Targeting t, Kicker k, Intake i, UpperStructure u){
        auton=a;
        compressor=c;
        drive=d;
        targeting=t;
        kicker=k;
        intake=i;
        upperStructure=u;
    }
}
