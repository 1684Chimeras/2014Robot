/*
 * API for the SPIEncoder class
 * 
 *  Usage :
 *      Construction:
 *          1) Pass the chip select and the information wether or not it is the master encoder (ask tony for which one is master)
 *      
 *      Get :
 *          1) Encoder.get()
 *      
 *      getDegrees:
 *          1) encoder.get() * 360/1024
 *
 *  TODO :   
 *      getRate() -> was done previously but scrapped for unknown reasons
 *      steady rollovers
*/

package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SPIDevice;

/**
 *
 * @author Arhowk
 */
public class SPIEncoder {
    
    
    SPIDevice host;
    DigitalOutput cs;
    Accumulator accum;
    boolean wrap = false;
    
    public SPIEncoder(int port, boolean isMaster)
    {
        if(isMaster)
        {
            DigitalOutput mosi = new DigitalOutput(RobotMap.SPIBus_mosi);
            DigitalInput miso = new DigitalInput(RobotMap.SPIBus_miso);
            DigitalOutput clk = new DigitalOutput(RobotMap.SPIBus_clk);
            cs = new DigitalOutput(port);
            
            host = new SPIDevice(clk,mosi,miso,cs,false);
            host.setClockRate(5000);
            host.setClockPolarity(false);
            host.setFrameMode(SPIDevice.FRAME_MODE_CHIP_SELECT);
            
        }else{
            cs = new DigitalOutput(port);
            host = new SPIDevice(cs,false);
            host.setClockRate(5000);
            host.setClockPolarity(false);
            host.setFrameMode(SPIDevice.FRAME_MODE_CHIP_SELECT);
        }
        new Thread(){
            public void run(){
                while(true){
                   // get();
                    try{
                        sleep(10);
                    }catch(Exception e){}
                }
            }
        }.start();
        accum = new Accumulator(15);
    }
    
    long prev = 91919191;
    int rate = 0;
    
    public void doesWrap(boolean doeswrap){
        wrap = doeswrap;
    }
    
    public double getRate(){
        return rate;//accum.get();
    }
    
    int cycles = -1;
    public void reset(){
        cycles = -1;
    }
    
    public boolean isConnected(){
        return host.transfer(0x55AA, 16) != 65535;
    }
    
    public int get(){
        
        long ret = prev;
        long temp1 = 0;
        long temp2 = 0;
  
        temp1 = host.transfer(0x55AA, 16);
        if (temp1 != 65535) {
            ret = temp1 & 0x1F9E;
            temp2 = (ret >> 1) & 0xF;
            ret >>= 7;
            ret <<= 4;
            ret |= temp2;
            
            /*      
            
        if (temp1 != 65535) {
            ret = temp1 & 0x1F9B;
            temp2 = ret & 0x3;
            ret >>= 5;
            ret |= temp2;*/
            
         //   if(prev == -91919191){
        //        prev = ret;
         //   }
            
         //   prev -= cycles * 256;
            
         //   if(ret - prev > 128){
         //       cycles--;
         //   }else if(ret - prev < -128){
         //       cycles++;
         //   }
         //   ret += cycles * 256;
            rate = (int)(ret - prev);
            
            if(rate > 512){
                rate = 1024 - rate;
            }
            if(rate < -512) rate = 1024 + rate;
            
            accum.add(rate);
          //  if(rate > 512) rate -= 1024;
            prev=ret;
        }
        
        return (int)ret;
    }
   
}
