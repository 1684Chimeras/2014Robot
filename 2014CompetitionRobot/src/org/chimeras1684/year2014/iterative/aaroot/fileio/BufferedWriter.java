

package org.chimeras1684.year2014.iterative.aaroot.fileio;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.microedition.io.Connector;


public class BufferedWriter {
    final String dir = "auton/";
    final static char deviceEscape = 0x0A;
    final static String comment = "*";
    final static String divChar = ":";
    final char space = 20;
    final char tab = 9;
    
    PrintStream out;
    DataOutputStream theFile;
    FileConnection fc;
    Vector dataMap;
    
    public void pushLine(String s){
        try{
            theFile.write(s.getBytes());
            theFile.write('\n');
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void close(){
        try{
            fc.close();
            theFile.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a file and enters it to the database
     * @param file url of the file to read
     */
    
    
    public BufferedWriter(String file){
        try{
            fc = (FileConnection) Connector.open("file:///"+dir+file);
            fc.create();
            
//            if(!fc.exists()){
//                fc.create();
//                System.out.println("Had to create " + file);
//            }else{
//                System.out.println("File Exists : " + file);
//            }
            theFile = fc.openDataOutputStream();
            
        }catch(Exception e){
            e.printStackTrace();
     //       e.printStackTrace();
        }        
    }
    private String removeWhitespace(String s){
        for(int i = 0; i < s.length(); i++){
            if(s.substring(i,i+1).equalsIgnoreCase(""+space) || s.substring(i,i+1).equalsIgnoreCase(""+tab)){
                s = s.substring(0,i) + s.substring(i+1,s.length());
            }
        }
        
        return s;
    }

    
}
