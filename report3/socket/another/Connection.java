package javaLanguage.basic.socket.another;

/**
 * describes a connection between a user and a network tranport
 * @author PKWooster
 * @version 1.0 June 15,2004
 */
public interface Connection
{
        static public final int CLOSED=0;
        static public final int OPENING=1;
        static public final int OPENED=2;
        static public final int CLOSING=3;
        
        public void attach(ConnectionUser cu);
        public void send(String str);
        public void close();
        public int getState();
        public void setName(String name);
        public String getName();
}
 
