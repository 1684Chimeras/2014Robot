
package org.chimeras1684.year2014.iterative.aaroot;

/**
 *
 * @author kellymalone & Arhowk
 */
public class RobotMap 
{
    
    //PNEUMATICS
    public static final int driveShift = 1,
                            frontLeftIntake = 2,
                            frontRightIntake = 3,
                            rearLeftIntake = 4,
                            rearRightIntake = 5,
                            unusedPneumatic1 = 6,
                            unusedPneumatic2 = 7,
                            unusedPneumatic3 = 8;
    
    //PWM OUTPUTS
    public static final int leftDrive   = 1;
    public static final int rightDrive  = 2;
    public static final int kicker   = 3;
    public static final int tower  = 4;
    public static final int frontIntake = 5;
    public static final int rearIntake  = 6;
    public static final int unusedPWM1  = 7;
    public static final int unusedPWM2 = 8;
    public static final int unusedPWM3  = 9;
    public static final int unusedPWM4  = 10;
    
    //GPIO
    public static final int pressureSwitch     = 1;
    public static final int leftDriveEncoderA  = 2;
    public static final int leftDriveEncoderB  = 3;
    public static final int rightDriveEncoderA = 4;
    public static final int rightDriveEncoderB = 5;
    public static final int SPIBus_csKicker    = 6;
    public static final int SPIBus_csTower     = 7;
    public static final int SPIBus_clk         = 8;
    public static final int SPIBus_miso        = 9;
    public static final int SPIBus_mosi        = 10;
    public static final int unusedGPIO4        = 11;
    public static final int unusedGPIO5        = 12;
    public static final int frontLineSensor    = 13;
    public static final int rearLineSensor     = 14;

    //RELAYS
    public static final int compressor   = 1;
    public static final int directionLight = 2;
    public static final int unusedRelay2 = 3;
    public static final int unusedRelay3 = 4;
    public static final int unusedRelay4 = 5;
    public static final int unusedRelay5 = 6;
    public static final int unusedRelay6 = 7;
    public static final int unusedRelay7 = 8;
    
    //ANALOG INPUTS
    public static final int gyro          = 1;
    public static final int tiltLimit1  = 2;
    public static final int tiltLimit2 = 3;
    public static final int unusedAnalog1 = 4;
    public static final int unusedAnalog2 = 5;
    public static final int unusedAnalog3 = 6;
    public static final int unusedAnalog4 = 7;
    public static final int battery       = 8;
}
