package javaLanguage.basic.socket.another;

import java.io.*;
import java.util.*;
/**
 * defines the processing for a message switch user
 * @author PKWooster
 * @version 1.0 June 15,2004
 */
public class User implements ConnectionUser
{
        private UserTable perUser;
        private String name ="";
        private boolean signedOn = false;
        private String[] targets;
        private int remainingTime;
        private int idleTime;
        Connection conn;
        
        /**
         * construct a user, link it to its connection and put it in the perUser table
         */
        User(Connection c, UserTable p, int idle)
        {
                conn = c;
                conn.attach(this);
                name = conn.getName();
                perUser = p;
                idleTime = idle;
                remainingTime = idleTime;
                perUser.add(name,this);
                targets = new String[0];
        }
 
        /**
         * process state changes
         */
        public void stateChange(int state)
        {
                if(state == Connection.CLOSED)close();
        }
        
        public void oneSec(){if(idleTime != 0 && 1 > --remainingTime)close();}
 
        public void send(String msg){conn.send(msg);}
        
        /**
         * receive string messages and distiguish between control and data
         */
        public void receive(String msg)
        {
                if(msg.startsWith("$"))doCommand(msg);
                else forward(msg);
                remainingTime = idleTime;
        }
        
        /**
         * forward data messages to other users
         */
        private void forward(String txt)
        {
                txt = name+": "+txt;
                if(0 < targets.length)
                {
                        for(int i = 0; i<targets.length; i++)
                        {
                                User u = perUser.get(targets[i]);
                                if(u != null)u.send(txt);
                        }
                }
                else
                {
                        User[] uv = perUser.allUsers();
                        for(int i= 0; i<uv.length;i++)
                        {
                                if(uv[i] != this)uv[i].send(txt);
                        }
                }
        }
 
        /**
         * execute command messages
         */
        private void doCommand(String inbuf)
        {
                boolean good = false;
                inbuf = inbuf.substring(1).trim();      // discard leading $ and blanks
                if(inbuf.length() > 0)
                {
                        String [] args = inbuf.split("\\s+");   // split up on blanks
                        if(args[0].equals("on"))good = signOn(args);
                        else if(args[0].equals("off"))good = signOff(args);
                        else if(args[0].equals("list"))good = listUsers(args);
                        else if(args[0].equals("to"))good = setTargets(args,1);
                        else if(args[0].equals("idle"))good = setIdle(args);
                }
                if(!good)
                {
                        send("invalid command="+inbuf+"\n");
                }
        }
 
        /**
         * sign on command
         */
        private boolean signOn(String [] args)
        {
                boolean good = false;
                if(args.length >1)
                {
                        String nm = args[1];
 
                        if(perUser.rename(name,nm))
                        {
                                name = nm;
                                send("Signed on as "+nm+"\n");
                                signedOn = true;
                                good = true;
                                setTargets(args,2);
                                conn.setName(nm);
                        }
                        else send("name="+nm+" already signed on\n");
                }
                return good;
        }
 
        /**
         * set forwarding targets
         */
        private boolean setTargets(String [] args, int i)
        {
                int j;
                if(i < args.length)
                {
                        targets = new String[args.length-i];
                        for(j = 0; j < args.length-i;j++){targets[j] = args[i+j];}
                }
                String str = "to=";
                for(j = 0; j < targets.length; j++)str += (targets[j]+" ");
                send(str+"\n");
                return true;
        }
 
        /** 
         * set idle timeout
         */
        private boolean setIdle(String[] args)
        {
                try
                {
                        int n = new Integer(args[1]).intValue();
                        idleTime=n;
                        send("idle time set to "+idleTime+"\r\n");
                        return true;
                }
                catch(NumberFormatException exc){return false;}
        }
 
        /**
         * sign off
         */
        private boolean signOff(String [] args)
        {
                close();
                return true;
        }
 
        /**
         * list connected users
         */
        private boolean listUsers(String [] args)
        {
                TreeSet allUsers = new TreeSet(perUser.keySet());
                HashSet t = new HashSet(Arrays.asList(targets));
                Object[] users;
                String resp = "On,Target,Nickname\n";
                int i=0;
                if(args.length < 2)users = allUsers.toArray();
                else                    // all users
                {
                        users = args;
                        i = 1;
                }
 
                for(;i<users.length; i++)
                {
                        String u = (String)users[i];
                        System.out.println("users["+i+"]="+u);
                        if(u.equals(name))resp+="*,";
                        else resp += (allUsers.contains(u)?"y,":"n,");
                        resp += (t.contains(u)?"y,":"n,");
                        resp += (u+"\n");
                }
                send(resp);
                return true;
        }
 
        /**
         * delete from perUser and close our connection
         */
        private void close()
        {
                System.out.println ("closing user "+name);
                perUser.delete(name);
                conn.close();
        }
}
 

