package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Trabant is an implementation of the IRobotCreateInterface.
 *
 * @author Erik...with Stanley's talking code and fail fix...I think!
 */
public class Trabant extends IRobotCreateAdapter
{
    private static final String TAG = "Ferrari";
    protected final Dashboard dashboard;
    /*
     * State variables:
     */
    private int speed = 100; // The normal speed of the Trabant when going straight
    private boolean running = true;
    private final int[][] stateTable =
    {
        {
            0, 1, 2, 3
        },
        {
            1, 1, 2, 3
        },
        {
            2, 1, 2, 3
        },
        {
            3, 1, 2, 3
        }
    };//State table to avoid obstacles
    private int statePointer = 0;
    private int presentState = 0;
    private int howFarBacked = 200;
    private final int howFarToGoBackWhenBumped = 200;
    public UltraSonicSensors sonar;

    /**
     * Constructs a Trabant, an amazing machine!
     *
     * @param ioio the IOIO instance that the Trabant can use to communicate
     * with other peripherals such as sensors
     * @param create an implementation of an iRobot
     * @param dashboard the Dashboard instance that is connected to the Trabant
     * @throws ConnectionLostException
     */
    public Trabant(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException
    {
        super(create);
        this.dashboard = dashboard;
        sonar = new UltraSonicSensors(ioio);
        song(0, new int[]
                {
                    58, 10
                });
    }

    /**
     * Main method that gets the Trabant going.
     */
    public void go() throws InterruptedException
    {
               goRobot();
    }

    /*
     * THE FUN STARTS HERE!
     */
    public void goRobot()
    {
        dashboard.log("main project is running");
        try
        {
            
            stateControler();
        } catch (Exception ex)
        {
            Logger.getLogger(MyRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stateControler() throws Exception
    {
        while (true)
        {
            checkSensors();
            switch (stateTable[presentState][statePointer])
            {
                case 0: //case A, not bumped
                    presentState = 0;
                    spinMove();
                    break;
                
                case 1: //case B, right bumped
                    presentState = 1;
                    bumpReaction("right");
                    break;
                case 2: //case C, left bumped
                    presentState = 0;
                    bumpReaction("left");
                    break;
                case 3: //case D, bump bolth
                    presentState = 0;
                    bumpReaction("both");
                    break;
                
            }
        }
    }

    public void bumpReaction(String bumpDirection) throws Exception
    {
        if (bumpDirection.equals("right"))
        {
            driveDirect(-100, -200);
        }
        if (bumpDirection.equals("left"))
        {
            driveDirect(-200, -100);
        }
        if (bumpDirection.equals("both"))
        {
            driveDirect(-100, -100);
        }
        SystemClock.sleep(2000);
        driveDirect(100, 100);
        statePointer = 0;
        presentState = 0;
    }

    public void checkSensors() throws Exception
    {
        readSensors(SENSORS_GROUP_ID6);
        if (isBumpRight() && isBumpLeft())
        {
            statePointer = 3;            
            dashboard.log("Front Bump");
        }
        if (!isBumpRight() && !isBumpLeft())
        {
            statePointer = 0;            
        }
        if (isBumpRight() && !isBumpLeft())
        {
            statePointer = 1;
            dashboard.log("bump right");
        }
        if (!isBumpRight() && isBumpLeft())
        {
            statePointer = 2;            
            dashboard.log("bump left");
        }
        
    }

//    /**
//     * **************************************************************************
//     * Vic's Awesome API
//     * **************************************************************************
//     */
//    public void stateController() throws Exception
//    {
//        setStatePointer();
//        switch (stateTable[presentState][statePointer])
//        {
//            case 0:
//                presentState = 0;
//                break;
//            case 1:
//                presentState = 1;
//                backingUp("right");
//                break;
//            case 2:
//                presentState = 2;
//                backingUp("left");
//                break;
//            case 3:
//                presentState = 3;
//                backingUp("straight");
//                break;
//        }
//    }
//
//    public void setStatePointer() throws ConnectionLostException
//    {
//        readSensors(SENSORS_GROUP_ID6);
//
//        if (isBumpRight() && !isBumpLeft())//Right
//        {
//            statePointer = 1;
//        }
//        if (isBumpLeft() && !isBumpRight())//left
//        {
//            statePointer = 2;
//        }
//        if (isBumpRight() && isBumpLeft())//straight
//        {
//            statePointer = 3;
//        }
//        if (!isBumpLeft() && !isBumpRight())//none
//        {
//            statePointer = 0;
//        }
//    }
//
//    private void backingUp(String direction) throws Exception
//    {
//        if (direction.equals("right"))
//        {
//            driveDirect(-(speed/4), -(speed));
//            dashboard.speak("backing right");
//        }
//        if (direction.equals("left"))
//        {
//            driveDirect(-(speed), -(speed/4));
//            dashboard.speak("backing left");
//        }
//        if (direction.equals("straight"))
//        {
//            driveDirect(-(speed), -(speed));
//            dashboard.speak("backing straight");
//        }
//        howFarBacked += getDistance();
//        if (howFarBacked < 0)
//        {
//            driveDirect(speed, speed);
//            howFarBacked = howFarToGoBackWhenBumped;
//            statePointer = 0;
//            presentState = 0;
//        }
//    }
    /**
     * Closes down all the connections of the Trabant, including the connection
     * to the iRobot Create and the connections to all the sensors.
     */
    public void shutDown()
    {
        closeConnection(); // close the connection to the Create
    }

    /**
     * Checks if the Trabant is running
     *
     * @return true if the Trabant is running
     */
    public synchronized boolean isRunning()
    {
        return running;
    }
    
    private synchronized void setRunning(boolean b)
    {
        running = false;
    }
    
    private void spinMove() throws ConnectionLostException
    {
        
        driveDirect(-200, 200);
        if (getInfraredByte() == 255)
        {
            driveDirect(200, 200);
            dashboard.speak("nothing");
        }
        if (getInfraredByte() == 248)
        {
            driveDirect(200, 200);
            dashboard.speak("red");
        }
        if (getInfraredByte() == 242)
        {
            driveDirect(200, 200);
            dashboard.speak("force");
        }
        if (getInfraredByte() == 244)
        {
            driveDirect(200, 200);
            dashboard.speak("green");
        }
        
    }
}
