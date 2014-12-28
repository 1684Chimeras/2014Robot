/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chimeras1684.year2014.iterative.aaroot;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Arhowk
 */
public class DeadSwitch {
    
    final static int MAX_NUM_BUTTONS = 10;
    final static int MAX_NUM_AXES = 6;
    final static double TRIGGER_POLL = 0.5;
    
    m_DR reciever;
    
    private static class m_DR{
        private Joystick djoy = null;
        
        public boolean pollButton(int axis){
            if(djoy != null) return djoy.getRawButton(axis);
            else return false;
        } 
        
        public boolean pollRightTrigger(){
            if(djoy != null){
//                System.out.println("right trigger : " + (djoy.getRawAxis(3) < -TRIGGER_POLL));
                return djoy.getRawAxis(3) < -TRIGGER_POLL;
            }
            return false;
        }
        public boolean pollLeftTrigger(){
            if(djoy != null){
//                System.out.println("left trigger : " + (djoy.getRawAxis(3) > TRIGGER_POLL));
                return djoy.getRawAxis(3) > TRIGGER_POLL;
            }
            return false;
        }
        
        public static m_DR joystick(Joystick j){
            m_DR m = new m_DR();
            m.djoy = j;
            return m;
        }
    }
    
    private boolean [] axes;
    private double [] joys;
    
    private boolean leftTrigger, rightTrigger;
    
    private int last = -1;
    
    public DeadSwitch(int port){
        reciever = m_DR.joystick(new Joystick(port));
        axes = new boolean [MAX_NUM_BUTTONS];
        joys = new double [MAX_NUM_AXES];
    }
    
    public int pollForPressed(){
        
        for(int i = 0; i < MAX_NUM_BUTTONS; i++){
            if(reciever.pollButton(i)){
                if(!axes[i]){
                    last = i;
                    axes[i] = true;
                }
            }else axes[i] = false;
        }
        
        if(reciever.pollLeftTrigger()){
            if(!leftTrigger){
                leftTrigger = true;
                last = 100;
            }
        }else leftTrigger = false;
        
        if(reciever.pollRightTrigger()){
            if(!rightTrigger){
                rightTrigger = true;
                last = 99;
            }
        }else rightTrigger = false;
        
        return last;
    }
    
    public boolean good(){
        if(last == -1) return true;
        else if(last == 99) return reciever.pollRightTrigger();
        else if(last == 100) return reciever.pollLeftTrigger();
        else return reciever.pollButton(last);
    }
    
    public void disabledInit(){
        pollForPressed();
        last = -1;
    }
    
}
