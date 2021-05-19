package javaLanguage.basic.socket.another;

//================= StreamSwitch.java =======================================//

 
import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * a simple message switch using stream based socket i/o
 * a very simple text message switching program
 * default command line is java StreamSwitch -p5050 -i600
 * -p port to listen on
 * -i default idle time in seconds
 * user commands start with $ and consist of blank seperated arguments
 * other lines sent by the user are forwarded
 * $on nickname targets
 *    sign on as nickname, sending to targets
 * $to targets
 *    change target list, reports current value
 * $list nicknames
 *    list status of specified nicknames
 * $list
 *    list all connected users
 * $off
 *    sign off
 *
 * @author PKWooster
 * @version 1.0 June 15,2004
 */
public class StreamSwitch
{
        private UserTable perUser = new UserTable();
        private Timer idleTimer;
        private Connection conn;
        
        private void listen(int port, int idleTime)
        {
                idleTimer = new Timer();
                idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){oneSec();}},0,1000);
 
                try
                {
                        ServerSocket ss = new ServerSocket(port);
                        Functions.dout(12,"listening on port "+port);
                        while(true)
                        {
                                Socket us = ss.accept();
                                conn = new StreamConnection(us);                        
                                new User(conn, perUser, idleTime);
                        }
                }
                catch(Exception e){     e.printStackTrace();}
                idleTimer.cancel();
        }
 
        static public void main(String [] args)
        {
                int port = 5050;
                int idleTime = 600;
                int level = 2;
 
                for(int i = 0; i<args.length; i++)
                {
                        if(args[i].startsWith("-p"))port = Functions.toInt(args[i].substring(2),port);
                        if(args[i].startsWith("-i"))idleTime = Functions.toInt(args[i].substring(2),idleTime);
                        if(args[i].startsWith("-d"))level = Functions.toInt(args[i].substring(2),level);
                }
                Functions.setDebugLevel(level);
                new StreamSwitch().listen(port,idleTime);
        }
 
        private void oneSec()
        {
                Object[] uv = perUser.allUsers();
                for(int i=0; i<uv.length; i++)((User)uv[i]).oneSec();
        }
}
