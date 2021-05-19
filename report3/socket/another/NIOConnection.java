package javaLanguage.basic.socket.another;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
 
public class NIOConnection implements Connection
{
	private SelectionKey sk;
	private ConnectionUser cu;
	private int state;
	private LinkedList sendQ = new LinkedList();
 
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	private ByteBuffer recvBuffer=null;
	private ByteBuffer sendBuffer=null;
	private StringBuffer recvString = new StringBuffer();
	private String crlf = "\r\n";
	private boolean writeReady = false;
	private String name="";
        
	/**
	 * construct a NIOConnection from a selection key
	 */
	NIOConnection(SelectionKey sk)
	{
		state = Connection.OPENED;
		this.sk = sk;		// link this to the key
        	sk.attach(this);
		
		Charset charset = Charset.forName("ISO-8859-1");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
		recvBuffer = ByteBuffer.allocate(8196);
	}
	
	/**
	 * attach a connection user to this connection
	 */
	public void attach(ConnectionUser cu)
	{
		this.cu = cu;
	}
	
        /**
         * process a read ready selection
         */
	public void doRead()
	{
		SocketChannel sc = (SocketChannel)sk.channel();
		if(sc.isOpen())
		{
			int len;
			recvBuffer.clear();
			try{len = sc.read(recvBuffer);}
			catch(IOException e){e.printStackTrace(); len=-1;} // error look like eof
			Functions.dout(1,"read len="+len);
			
			if(len > 0)
			{
				recvBuffer.flip();
				CharBuffer buf = null;
				try{buf = decoder.decode(recvBuffer);}	// convert bytes to chars
				catch(Exception ce){ce.printStackTrace(); len = -1;}
				toUser(buf);
			}
			if(len < 0)close();
		}
		else System.out.println("read closed");
	}
        /* 
         * split up received data and forward it to the user
         */
	private void toUser(CharBuffer buf)
	{
		if(buf != null)
		{
			int i = 0;
			int j = 0;
			recvString.append(buf);			// as a string buffer
			int z = recvString.length();
			while(i < z)
			{
				char c = recvString.charAt(i);
				if(c == '\r' || c == '\n')
				{
					i++;
					if(c == '\r' && i < z && '\n' == recvString.charAt(i))i++;
					cu.receive(recvString.substring(j,i));	// to user
					j = i+1;				// start of next
				}
				else i++;
			}
			if(j < z)recvString.delete(0,j);	// drop front of string buffer
			else recvString = new StringBuffer();
		}
	}
	
        /**
         * process a write ready selection
         */
	public void doWrite()
	{
		Functions.dout(12,"write ready");
		sk.interestOps(SelectionKey.OP_READ);		// deselect OP_WRITE
		writeReady = true;				// write is ready
		if(sendBuffer != null)write(sendBuffer);	// may have a partial write
		writeQueued();					// write out rest of queue
	}	
        /**
         * queue up a text string to send and try to send it out
         */
	public void send(String text)
	{
		sendQ.add(text);	// first put it on the queue
		writeQueued();		// write all we can from the queue
	}
	
        /*
         * attempt to send all queued data
         */
	private void writeQueued()
	{
		while(writeReady && sendQ.size() > 0)	// now process the queue
		{
			String msg = (String)sendQ.remove(0);
			write(msg);	// write the string
		}
	}
	
        /* 
         * convert a text string to a byte buffer and send it
         */
	private void write(String text)
	{
		try
		{
			ByteBuffer buf = encoder.encode(CharBuffer.wrap(text));
			write(buf);
		}
		catch(Exception e){e.printStackTrace();}
	}
	
        /*
         * write out a byte buffer
         */
	private void write(ByteBuffer data)
	{
		SocketChannel sc = (SocketChannel)sk.channel();
		if(sc.isOpen())
		{
			int len=0;
 
			if(data.hasRemaining())
			{
				try{len = sc.write(data);}
				catch(IOException e){e.printStackTrace(); close();}
			}
			if(data.hasRemaining())			// write would have blocked
			{
				Functions.dout(12,"write blocked");
				writeReady = false;
				sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);	// select OP_WRITE
				sendBuffer = data;		// save the partial buffer
			}
			else sendBuffer = null;
		}
		else Functions.dout(12,"write closed");
	}
 
        /*
         * close the connection and its socket
         */
	public void close()
	{	
		if(state != Connection.CLOSED)
		{
			SocketChannel sc = (SocketChannel)sk.channel();
			if(sc.isOpen())
			{
				Functions.dout(2,"closing channel");
				try
                                {
                                        sc.close();
                                        sk.selector().wakeup();
                                        sk.attach(null);
                                }
				catch(IOException ce){Functions.fail(ce,"close failed");}
			}
			else Functions.dout(12,"already closed");
			state = Connection.CLOSED;
			cu.stateChange(state);
		}
	}
	
	public String getName(){return name;}
	public void setName(String nm){name = nm;}
	public int getState(){return state;}
}