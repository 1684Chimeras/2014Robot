/*
 * API for the $clazz$ class
 * 
 *  Usage :
 *      Toggling the compressor:
 *          1) call compressor.compress()
 *
 *  TODO :   
 *      Delete this class and use the "Compressor" object
*/

package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;


/**
 *  Custom interface for compressors
 *  because we're too cool to use WPI classes
 * 
 * @author Arhowk
 */
public class Compressor 
{
    Relay compressor;
    DigitalInput pressureSwitch;
    
    /**
     * Constructs a new singleton compressor because we can.
     */
    public Compressor()
    {
        compressor = new Relay(RobotMap.compressor);
        pressureSwitch = new DigitalInput(RobotMap.pressureSwitch);
    }
    
    /**
     * Compresses the compressor if the relay is relaying
     */
    
    public void compress()
    {
        if(pressureSwitch.get())
        {
            compressor.set(Relay.Value.kOff);
        }else{
            compressor.set(Relay.Value.kForward);
        }    
    }
    
    public void disable(){
        compressor.set(Relay.Value.kOff);
    }
    
}
