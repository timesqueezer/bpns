package javaLanguage.basic.socket.another;

/**
 * describes a client that uses a Connection
 * part of the pkwnet package
 *
 * @author PKWooster
 * @version 1.0 June 17,2004
 */
public interface Client
{
        public void setAddress(String address);
        public String getAddress();
        public void setPort(int port);
        public int getPort();
        public void setIdleTime(int idleTime);
        public int getIdleTime();
        public void connect();
        public void disconnect();
        public void send(String msg);
}