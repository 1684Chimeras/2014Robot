
    /*
     * Interface for the "Targeting" class 
     * 
     * Usage :
     *      public void init(){
     *          call Targeting.snapshotSuttin()
     *      }
     * 
     *      public void iterate(){
     *          if targeter.toTheLeft(){
     *              //do stuff
     *          }else if targeter.toTheRight(){
     *              //do stuff
     *          }else if targeter.inTheCenter(){
     *              //shoooooooot!
     *          }else{
     *              //no hot goal found. assume in the center
     *          }
     *       }
     * 
     * API :
     *      construct() -> nonmultiplicative
     *          Standard constructor
     * 
     *      toTheLeft() : boolean -> iterative
     *          Returns true if the hot goal is to the left of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      toTheRight() : boolean -> iterative
     *          Returns true if the hot goal is to the right of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      inTheCenter() : boolean -> iterative
     *          Returns true if the hot goal is directly in the center of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      snapshotYellowLED() -> noniterative
     *          Interprets the position of the hot goal based on the yellow LEDs. 
     *          Should only be called once. Hot goal changes can be estimated
     *          Return values can be obtained from the getter functions above
     * 
     *      snapshotHorizontalTarget() -> noniterative
     *          Interprets the position of the hot goal based on the horizontal goal. 
     *          Should only be called once. Hot goal changes can be estimated
     *          Based off of the 2014 Vision Template
     *          Return values can be obtained from the getter functions above
     * 
     * 
     */
    /*
     * Interface for the "Targeting" class 
     * 
     * Usage :
     *      public void init(){
     *          call Targeting.snapshotSuttin()
     *      }
     * 
     *      public void iterate(){
     *          if targeter.toTheLeft(){
     *              //do stuff
     *          }else if targeter.toTheRight(){
     *              //do stuff
     *          }else if targeter.inTheCenter(){
     *              //shoooooooot!
     *          }else{
     *              //no hot goal found. assume in the center
     *          }
     *       }
     * 
     * API :
     *      construct() -> nonmultiplicative
     *          Standard constructor
     * 
     *      toTheLeft() : boolean -> iterative
     *          Returns true if the hot goal is to the left of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      toTheRight() : boolean -> iterative
     *          Returns true if the hot goal is to the right of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      inTheCenter() : boolean -> iterative
     *          Returns true if the hot goal is directly in the center of the robot's facing
     *          snapshot#something#() must be called first
     * 
     *      snapshotYellowLED() -> noniterative
     *          Interprets the position of the hot goal based on the yellow LEDs. 
     *          Should only be called once. Hot goal changes can be estimated
     *          Return values can be obtained from the getter functions above
     * 
     *      snapshotHorizontalTarget() -> noniterative
     *          Interprets the position of the hot goal based on the horizontal goal. 
     *          Should only be called once. Hot goal changes can be estimated
     *          Based off of the 2014 Vision Template
     *          Return values can be obtained from the getter functions above
     * 
     * 
     */

package org.chimeras1684.year2014.iterative.aaroot;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision;
//import edu.wpi.first.wpilibj.image.NIVision;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.image.RGBImage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.chimeras1684.year2014.vision.ARVision;

/**
 *
 * @author Arhowk
 */
public class Targeting {
    
    
    final boolean useCameraImage = false;
    final String testImage = "/testImages/10ft_Center.png";
    int X_IMAGE_RES = 640;		//X Image resolution in pixels, should be 120, 240 or 480
    int MIN_CENTER_X = 0;
    int MAX_CENTER_X = 0;
    
    final double MIN_CENTER_X_FACTOR = 0.33; //How close an object's center of mass must be to be considered "in" the center. (left side)
    final double MAX_CENTER_X_FACTOR = 0.66; //See above. (right side)
    
    
    final int [] tintsDark = { //Psuedo-vision targets
      0,45,230,255,230,255  
    };
    final int [] tintsBright = { //"GamePieces1.jpg"
      225,250,235,255,235,255  
    };
    
    final int [] appTint = tintsDark;
    
    final int [] tintsTestHighlighter = { //"GamePieces1.jpg"
      140,180,140,180,100,130
    };
    final int [] tintsTestYellowPaper= { //"GamePieces1.jpg"
      230,255,220,250,230,250
    };
    
    
    final int [] appYellow = tintsTestYellowPaper;
    
    final int MAX_RECTANGULARITY = 40; //Determines how rectangular a target is.
    //If a target is rectangular, than the # of pixels in the target will be (REC_LIMIT)% of its width * its height
    
    //The minimum aspect ratio to be determined a horizontal or vertical goal
    final int MAX_ASPECT_RATIO = 55;

    //Minimum area of particles to be considered
    final int MIN_AREA = 150;

    //Maximum number of particles to process
    final int MAX_PARTICLES = 7;

    //Grabs images in real-time
    AxisCamera camera;         
    
    //Assists with particle filtering
    CriteriaCollection cc;    
    
    //Stores information about where the targeting is
    boolean left;
    boolean right;
    boolean center;
    
    /**
     * Creates a new targeting singleton lol
     */
    public Targeting() {
        if(useCameraImage){
            camera = AxisCamera.getInstance();  // get an instance of the camera
            
        }
        
        SmartDashboard.putNumber("R Min", appYellow[0]);
        SmartDashboard.putNumber("R Max", appYellow[1]);
        SmartDashboard.putNumber("G Min", appYellow[2]);
        SmartDashboard.putNumber("G Max", appYellow[3]);
        SmartDashboard.putNumber("B Min", appYellow[4]);
        SmartDashboard.putNumber("B Max", appYellow[5]);
        cc = new CriteriaCollection();      // create the criteria for the particle filter
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, MIN_AREA, 65535, false); //Only accept particles from areas (MIN_AREA) to enormous
    }
    
    public void update(){
        appYellow[0] = (int) SmartDashboard.getNumber("R Min", appYellow[0]);
        appYellow[1] = (int) SmartDashboard.getNumber("R Max", appYellow[1]);
        appYellow[2] = (int) SmartDashboard.getNumber("G Min", appYellow[2]);
        appYellow[3] = (int) SmartDashboard.getNumber("G Max", appYellow[3]);
        appYellow[4] = (int) SmartDashboard.getNumber("B Min", appYellow[4]);
        appYellow[5] = (int) SmartDashboard.getNumber("B Max", appYellow[5]);
    }
    
    /**
     * to the left
     * @return true if the target is to the left
     */
    public boolean toTheLeft(){
        return left;
    }

    /**
     * to the right
     * @return true if target is to the riht
     */
    public boolean toTheRight(){
        return right;
    }

    /**
     * to the center
     * @return true if the target is in the center
     */
    public boolean inTheCenter(){
        return center;
    }
    
    /**
     * takes a picture, looking for yellow LEDs
     */
    public void snapshotYellowLED(){
        right = false;
        left = false;
        center = false;
        
        update();
        ColorImage image = null;
        BinaryImage thresholdImage = null;
        BinaryImage convexHullImage = null;
        BinaryImage filteredImage = null;
        
        try {
            if(useCameraImage){
                image = camera.getImage();    
              //  image.write("testimages/"+(new Random().nextInt()));
            }else{
                image = new RGBImage(testImage);   
            }
            X_IMAGE_RES = image.getWidth();
            System.out.println("res : " + X_IMAGE_RES);
            MIN_CENTER_X = (int)(X_IMAGE_RES * MIN_CENTER_X_FACTOR);
            MAX_CENTER_X = (int)(X_IMAGE_RES * MAX_CENTER_X_FACTOR);
          //  image.write("/images/testImage2.png");
            System.out.println("appYellow[0] = " + appYellow[0]);
            thresholdImage = image.thresholdRGB(appYellow[0], appYellow[1], appYellow[2], appYellow[3], appYellow[4], appYellow[5]); //Pretty open range for "orange"
//            thresholdImage.write("/images/testImage2.png");
            BinaryImage dilateImage = thresholdImage.removeSmallObjects(false, 0);
            ARVision.dilate(dilateImage.image, thresholdImage.image, 3);
//            dilateImage.write("/images/testImage4.png");
            convexHullImage = dilateImage.convexHull(false);  //fills in rectangles
//            convexHullImage.write("/images/testImage1.png");
            filteredImage = convexHullImage.particleFilter(cc);  // filter out small particles
//          thresholdImage.write("/images/testImage2.png");
//              filteredImage.write("/images/testImage3.png");
            
            double largestMass = 0; //Stores the currently largest particle's mass
            ParticleAnalysisReport biggestTarget = null; //Stores the currently largest particle
            
            if(filteredImage.getNumberParticles() > 0){
                for(int i = 0; i < MAX_PARTICLES && i < filteredImage.getNumberParticles(); i++){
                    ParticleAnalysisReport target = filteredImage.getParticleAnalysisReport(i); //Get the i target
                    
                    if(target.particleArea > largestMass){
                        largestMass = target.particleArea; //If its bigger, set the largest target to the current iterable
                        biggestTarget = target;
                    }
                }
                if(biggestTarget != null){
                    if(biggestTarget.center_mass_x > MIN_CENTER_X && biggestTarget.center_mass_x < MAX_CENTER_X){
                        left = false;
                        right = false;
                        center = true;
                        image.free(); //free dem data structures up
                        thresholdImage.free();
                        convexHullImage.free();
                        filteredImage.free();
                        return;
                    }else if(biggestTarget.center_mass_x > MIN_CENTER_X){
                        right = true;
                        left = false;
                        center = false;
                        image.free(); //free dem data structures up
                        thresholdImage.free();
                        convexHullImage.free();
                        filteredImage.free();
                        return;
                    }else{
                        right = false;
                        left = true;
                        center = false;
                        image.free(); //free dem data structures up
                        thresholdImage.free();
                        convexHullImage.free();
                        filteredImage.free();
                        return;
                    }
                }
            }else{
                System.out.println("no particles");
            }
            image.free(); //free dem data structures up
            thresholdImage.free();
            convexHullImage.free();
            filteredImage.free();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * takes a picture looking for hori goals
     */
    public void snapshotHorizontalGoal() {
        try {
            ColorImage image;
            if(useCameraImage){
                image = camera.getImage();         // comment if using stored images
            }else{
                image = new RGBImage(testImage);   // get the sample image from the cRIO flash
            }
            X_IMAGE_RES = image.getWidth();
            MIN_CENTER_X = (int)(X_IMAGE_RES * MIN_CENTER_X_FACTOR);
            MAX_CENTER_X = (int)(X_IMAGE_RES * MAX_CENTER_X_FACTOR);
            
            BinaryImage thresholdImage = image.thresholdRGB(appTint[0], appTint[1], appTint[2], appTint[3], appTint[4], appTint[5]);   // keep only green objects
            BinaryImage convexHull = thresholdImage.convexHull(false);
            BinaryImage filteredImage = convexHull.particleFilter(cc);           // filter out small particle
            
            if(filteredImage.getNumberParticles() > 0)
            {
                for (int i = 0; i < MAX_PARTICLES && i < filteredImage.getNumberParticles(); i++) {
                    ParticleAnalysisReport report = filteredImage.getParticleAnalysisReport(i);
                    double rect = getRectangularity(report); //determine if its a rectangle
                    double vert = getAspectRatio(filteredImage, report, i, true); //get its vertical aspect ratio
                    double horiz = getAspectRatio(filteredImage, report, i, false); //get its horizontal aspect ratio

                    //Check if the particle is a horizontal target, if not, check if it's a vertical target
                    if(determineTarget(rect,vert,horiz, false))
                    {
                        if(report.center_mass_x > MIN_CENTER_X && report.center_mass_x < MAX_CENTER_X){
                            left = false;
                            right = false;
                            center = true;
                            image.free(); //free dem data structures up
                            thresholdImage.free();
                            convexHull.free();
                            filteredImage.free();
                            return;
                        }else if(report.center_mass_x > MIN_CENTER_X){
                            right = true;
                            left = false;
                            center = false;
                            image.free(); //free dem data structures up
                            thresholdImage.free();
                            convexHull.free();
                            filteredImage.free();
                            return;
                        }else{
                            right = false;
                            left = true;
                            center = false;
                            image.free(); //free dem data structures up
                            thresholdImage.free();
                            convexHull.free();
                            filteredImage.free();
                            return;
                        }
                    }
                }
            }else{
                System.out.println("no particles");
            }

            convexHull.free();
            thresholdImage.free();
            filteredImage.free();
            image.free();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //Scores the aspect ratio of a target. The aspect ratio of a target is it's sides over each other.
    private double getAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean vertical) throws NIVisionException
    {
        double rectLong, rectShort, aspectRatio = 0, idealAspectRatio;

        //The longer sie of the particle
        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        //The shorter side of the particle
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        idealAspectRatio = vertical ? (4.0/32) : (23.5/4);	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall
	
        //Divide width by height to measure aspect ratio
        if(report.boundingRectWidth > report.boundingRectHeight){
            //particle is wider than it is tall, divide long by short
            aspectRatio = getRatioScore((rectLong/rectShort)/idealAspectRatio);
        }
	return aspectRatio;
    }
    
    
    //Determines if a particle is vertical or horizontal.
    boolean determineTarget(double rect, double vert, double horz, boolean vertical){
	boolean isTarget = true;

	isTarget = isTarget && rect > MAX_RECTANGULARITY;
	if(vertical){
  //          isTarget = isTarget && vert > MAX_ASPECT_RATIO;
	} else {
    //        isTarget = isTarget && horz > MAX_ASPECT_RATIO;
	}

	return isTarget;
    }
    
    /*
     * Determines if a target is rectangular or not.
     * If the target is rectangular, than its # of pixels should be a constant % of its width * its height.
     */
    double getRectangularity(ParticleAnalysisReport report){
        if(report.boundingRectWidth*report.boundingRectHeight !=0){
                return 100*report.particleArea/(report.boundingRectWidth*report.boundingRectHeight);
        } else {
                return 0;
        }	
    }
    
    /*
     * Converts a ratio into a score.
     * Desirably, the ratio should be at about 1. At 1, this function will return 100. At 0 or 2, this function will return 0. Else, it will interpolate.
     */
    double getRatioScore(double ratio)
    {
        return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
    }
}
