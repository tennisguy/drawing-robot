package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

public class MyRobot extends Trabant
{
    MyRobot(IOIO ioio, IRobotCreateInterface iRobotCreate, Dashboard dashboard) throws ConnectionLostException
    {
        super(ioio, iRobotCreate, dashboard);
    }

    /*
     * THE FUN STARTS HERE!
     */
    public void goRobot()
    {
        dashboard.speak("hello luke. hello joey. what would you like me to draw?");
        driveDirect(40, 40);
        SystemClock.sleep(2000);
        
        driveDirect(-20, 40)
        SystemClock.sleep(1000);
        //Turtle.move(100);
         driveDirect(40, 40);
        SystemClock.sleep(2000);
        //Turtle.turn(-90);
        driveDirect(-20, 40)
        SystemClock.sleep(1000);
        
    }
}
