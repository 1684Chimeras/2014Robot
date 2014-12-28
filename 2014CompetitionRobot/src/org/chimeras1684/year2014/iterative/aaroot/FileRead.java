

package org.chimeras1684.year2014.iterative.aaroot;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * API for the FileRead class
 * 
 *  Usage :
 *      Loading a file to the robot
 *          0) FileZilla is recommended because of its ease of auto uploading changes
 *          1) Connect to the robot's FTP, 10.16.84.2, using FRC/FRC as the ftp password
 *          2) Open the \auton\$target$.txt file
 *          3) Make Changes
 *          4) Reupload
 * 
 *      Adding and deleting values on the file
 *          * indicates a comment
 *          leading and trailing whitespace are discarded
 *          use : to seperate the key from the value
 *          newlines for every new value
 *
 *      Loading a file in java:
 *          1) Construct a new FileRead class, passing the name of the file in the \auton\ folder
 *          2) Call the newly constructed file using file.getDouble("yourKey")
 *          3) Auton commands can also be passed string arguments that are read from \auton\config.tt
 *
 *
 *  TODO :   
 *      Make usage of the BufferedReader class.
 *      Accept directories other than auton
 *
 * @author Arhowk
 */
public class FileRead {
    final String dir = "auton/";
    final static char deviceEscape = 0x0A;
    final static String comment = "*";
    final static String divChar = ":";
    final char space = 20;
    final char tab = 9;
    
    PrintStream out;
    DataInputStream theFile;
    FileConnection fc;
    Hashtable dataMap;
    
    /**
     * what why is this a public function
     * @param s
     * @return
     */
    
    /**
     * Opens a file and enters it to the database
     * @param file url of the file to read
     */
    public FileRead(String file){
        try{
            fc = (FileConnection) Connector.open("file:///"+dir+file, Connector.READ);
            theFile = fc.openDataInputStream();
            dataMap = new Hashtable();
            
            while(true){
                try{
                    String next = readLine(theFile);
                    if(next.length() > 0){
                       dataMap.put(next.substring(0, next.indexOf(divChar)).trim(), next.substring(next.indexOf(divChar) + divChar.length(), next.length()).trim());
                    }else{
                        break;
                    }
                }catch(Exception e){
                    
                }
            }
            
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

    /**
     * reads an int out of the file
     * @param key what key
     * @return return int. default 0
     */
    public int getInt(String key){
        try {
            Enumeration e = dataMap.keys();

            while(e.hasMoreElements()){
                String t = (String)e.nextElement();
                if(t.equals(key)){
                    return Integer.parseInt(removeWhitespace(((String)dataMap.get(t))));
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    /**
     * reads a double from the file
     * @param key read key
     * @return double
     */
    public double getDouble(String key){
        try{
            Enumeration e = dataMap.keys();

            while(e.hasMoreElements()){
                String t = (String)e.nextElement();
                if(t.equals(key)){
                    return Double.parseDouble(removeWhitespace(((String)dataMap.get(t))));
                }
            }
        }catch(Exception e){
            
        }
        
        return 0;
    }
    
    /**
     * Gets a string from the file
     * @param key key 
     * @return return
     */
    public String getString(String key){
        try {
            Enumeration e = dataMap.keys();

            while(e.hasMoreElements()){
                String t = (String)e.nextElement();
                if(t.equals(key)){
                    return (String)dataMap.get(t);
                }
            }
        } catch (Exception e) {
        }
            return null;
    }
    
    private String readLine(DataInputStream s){
        String ret = "";
        boolean isComment = false;
        try{ 
            while(true){
                char next = (char)s.readByte();
                if(next == deviceEscape){
                    if (ret.length() == 0){
                        return readLine(s);
                    }
                    else{
                        return ret;
                    }
                }
                if(comment.indexOf(next) != -1){
                    isComment = true;
                    ret = "";
                }
                if(!isComment){
                    ret += next;
                }
            }
        }catch(Exception e){
            
        }
        if(!isComment){
            return ret;
        }else{
            return readLine(s);
        }
                    
    }
}
