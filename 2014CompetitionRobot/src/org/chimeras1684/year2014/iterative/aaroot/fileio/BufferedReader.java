

package org.chimeras1684.year2014.iterative.aaroot.fileio;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.microedition.io.Connector;

public class BufferedReader {
    final String dir = "auton/";
    final static char deviceEscape = 0x0A;
    final static String comment = "*";
    final static String divChar = ":";
    final char space = 20;
    final char tab = 9;
    
    PrintStream out;
    DataInputStream theFile;
    FileConnection fc;
    Vector dataMap;
    
    /**
     * what why is this a public function
     * @param s
     * @return
     */
    public String readLine(){
        DataInputStream s = theFile;
        String ret = "";
        boolean isComment = false;
        try{ 
            while(true){
                char next = (char)s.readByte();
                if(next == deviceEscape){
                    //System.out.println("next is device escape!");
                    if (ret.length() == 0){
                  //      System.out.println("new line");
                        return readLine();
                    }
                    else{
                   //     System.out.println("returnin : " + ret + ":");
                        return ret;
                    }
                }
                if(ret.length() == 0 && comment.indexOf(next) != -1){
                    isComment = true;
                }
                if(!isComment){
                    ret += next;
                }
            }
        }catch(Exception e){
            
            //e.printStackTrace();
        }
        if(!isComment){
            return ret;
        }else{
            return readLine();
        }
                    
    }
    
    public void close() throws Exception{
        fc.close();
        theFile.close();
    }
    
    /**
     * Opens a file and enters it to the database
     * @param file url of the file to read
     */
    public BufferedReader(String file){
        try{
            fc = (FileConnection) Connector.open("file:///"+dir+file, Connector.READ);
            theFile = fc.openDataInputStream();
            
        }catch(Exception e){
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
