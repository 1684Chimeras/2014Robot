/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chimeras1684.year2014.iterative.aaroot;

import org.chimeras1684.year2014.iterative.aaroot.fileio.BufferedReader;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.chimeras1684.year2014.iterative.aaroot.fileio.BufferedWriter;
import org.chimeras1684.year2014.iterative.auton.root.AutonFramework.TimedCommandGroup;

/**
 *
 * @author Arhowk
 */
public class AutonMode {
    
    
    
    private static final char IF = '`', ENDIF = '~', ELSE = '!', ELSEIF = '@', PAUSE = '$', ENDPAUSE='%', 
            WHILE='^', WHILEDUR='&', ACTION='(';
    
    String name;
    String ds[];
    String bin;
    boolean isHardcoded = false;
    
    int chk;
    int fchs;
    
    NetworkTable table;
    
    AutonMode(String name){
        this.name = name;
        ds = new String[6];
        table = NetworkTable.getTable("auton/" + name);
        forceUpdate();
    }
    
    private AutonMode(){ds = new String[4];};
    
    public static AutonMode loadStatic(TimedCommandGroup tcg, String [] desc){
        AutonMode x = new AutonMode();
        x.ds[0] = desc[0];
        x.ds[1] = desc[1];
        x.ds[2] = desc[2];
        x.ds[3] = desc[3];
        x.isHardcoded = true;
        x.group = tcg;
        return x;
    }
    
    public static AutonMode loadFromFile(String autonName){
        try{
            System.out.println("File Loading Auton : " + autonName);
            BufferedReader br = new BufferedReader(autonName+".txt");
            AutonMode mode = new AutonMode();
            mode.table = NetworkTable.getTable("auton/"+autonName);
            mode.ds[0] = br.readLine();
            mode.ds[1] = br.readLine();
            mode.ds[2] = br.readLine();
            mode.ds[3] = br.readLine();
            mode.bin = br.readLine();
        mode.group = new DashboardAutonGroup(mode.bin); 
            br.close();
            return mode;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public void update(){
        if(isHardcoded) return;
        if(table.getNumber("CHK") != chk){
            if(table.getNumber("FCHS") != fchs){
//                System.out.println("save");
                forceUpdate();
                forceSave();
            }else{
//                System.out.println("nah. just update bro");
                forceUpdate();
            }
        }
    }
    
    public final void forceSave(){
        if(isHardcoded) return;
        BufferedWriter bw = new BufferedWriter(name+".txt");
        bw.pushLine(ds[0]);
        bw.pushLine(ds[1]);
        bw.pushLine(ds[2]);
        bw.pushLine(ds[3]);
        bw.pushLine(bin);
        bw.close();
        fchs = (int)table.getNumber("FCHS", 0);
    }
    public void updateDS(){
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser1, 1, ds[0]);
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2, 1, ds[1]);
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser3, 1, ds[2]);
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser4, 1, ds[3]);
        
        DriverStationLCD.getInstance().updateLCD();
    }
    
    
    static final String spaces = "                                      ";
    public final void forceUpdate(){
        if(isHardcoded) return;
        ds[0] = table.getString("DS1", "") + spaces;
        ds[1] = table.getString("DS2", "") + spaces;
        ds[2] = table.getString("DS3", "") + spaces;
        ds[3] = table.getString("DS4", "") + spaces;
        chk = (int)table.getNumber("CHK", 0);
        bin = table.getString("BIN", "");
        group = new DashboardAutonGroup(bin); 
    }
    
    private static class DashboardAutonGroup extends TimedCommandGroup{
        private String skip(String s, int c){
            while(c --> 0){
                int id = s.indexOf(":");
                if(id == -1){
                    return "";
                }else{
                    s = s.substring(id+1,s.length());
                }
            }
            return s;
        }
        public DashboardAutonGroup(String bin){
            boolean b2 = false;
            try{
                whileLoop: while(bin.length() > 0){
                    
                    //bin = bin.substring(0, bin.indexOf(":") == -1 ? bin.indexOf(":") : bin.length());
                    String strproc = bin.substring(0, bin.indexOf(":") != -1 ? bin.indexOf(":") : bin.length());
                    char proc = bin.charAt(0);
                    boolean b = false;

                    if(bin.charAt(0) == '+'){
                        
                        b = true;
                        proc = bin.charAt(1);
                    }
                    switch(proc){
                        case IF:
                            if(b){
                                addIf(Integer.parseInt(strproc.substring(2,strproc.length())));
                                bin = skip(bin,2);
                            }else{
                                addIf(Integer.parseInt(strproc.substring(1,strproc.length())));
                                bin = skip(bin,1);
                            }
                            break;
                        case ELSEIF:
                            if(b){
                                addElseIf(Integer.parseInt(strproc.substring(2,strproc.length())));
                                bin = skip(bin,2);
                            }else{
                                addElseIf(Integer.parseInt(strproc.substring(1,strproc.length())));
                                bin = skip(bin,1);
                            }
                            break;
                        case ELSE:
                            addElse();
                            bin = skip(bin,1);
                            break;
                        case ACTION:
                            int actionRequest = Integer.parseInt(strproc.substring(b ? 2 : 1,strproc.length()));
                            strproc = skip(bin,1);
                            strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                            double start = Double.parseDouble(strproc);
                            strproc = skip(bin,2);
                            strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                            double timeout = Double.parseDouble(strproc);
                            if(b){
                                strproc = skip(bin,3);
                                strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                                double arg = Double.parseDouble(strproc);
                                add(actionRequest,arg,start,timeout);
                                bin = skip(bin, 4);
                            }else{
                                add(actionRequest,start,timeout);
                                bin = skip(bin, 3);
                            }
                            break;
                        case ENDIF:
                            bin = skip(bin, 1);
                            addEndIf();
                            break;
                        case ENDPAUSE:
                            bin = skip(bin, 1);
                            addEndPause();
                            break;
                        case PAUSE:
                            bin = skip(bin, 1);
                            addPause();
                            break;
                        case WHILE:
                            int req = Integer.parseInt(strproc.substring(b ? 2 : 1,strproc.length()));
                            strproc = skip(bin,1);
                            strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                            start = Double.parseDouble(strproc);
                            if(b){
                                strproc = skip(bin,2);
                                strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                                double arg = Double.parseDouble(strproc);
                                addWhile(req, arg, start);
                                bin = skip(bin, 3);
                            }else{
                                bin = skip(bin, 2);
                                addWhile(req,start);
                            }
                            break;
                        case WHILEDUR:
                            req = Integer.parseInt(strproc.substring(b?2:1,strproc.length()));
                            strproc = skip(bin,1);
                            strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                            start = Double.parseDouble(strproc);
                            double arg = -3402434;
                            int i = 2;
                            if(b){
                                strproc = skip(bin,2);
                                i++;
                                strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                                arg = Double.parseDouble(strproc);
                            }
                            strproc = skip(bin,i);
                            strproc = strproc.substring(0, strproc.indexOf(":") != -1 ? strproc.indexOf(":") : strproc.length());
                            double dur = Double.parseDouble(strproc);
                            if(arg == -3402434){
                                addWhile(req, 0, start, dur);
                                bin = skip(bin, 3);
                            }else{
                                bin = skip(bin, 4);
                                addWhile(req,arg,start,dur);
                            }
                            break;
                        default:
                            bin = skip(bin, 1);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    TimedCommandGroup group;
    public TimedCommandGroup toAutonSequence(){
        return group;
    }
}
