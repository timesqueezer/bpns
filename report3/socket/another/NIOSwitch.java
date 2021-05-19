package javaLanguage.basic.socket.another;


import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
 
/**
 * a simple message switch using NIO based socket i/o
 * part of the pkwnet package
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
 * @version 1.0 June 14,2004
 */
public class NIOSwitch
{
	static int debugLevel=2;
	private UserTable perUser = new UserTable();
	private Timer idleTimer;
	private ServerSocket ss;			// the listening socket
	private ServerSocketChannel sschan; 		// the listening channel
	private Selector selector;			// the only selector
	private int bufsz = 8192;
	private int idleTime;
	
	private void listen(int port, int idleTime)
	{
		this.idleTime = idleTime;	
		idleTimer = new Timer();
		idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){oneSec();}},0,1000);
 
		int n=0;
		Iterator it;
		SelectionKey key;
		Object att;
		int io;
 
		Functions.dout(12,"listening on port="+port);
		try
		{
			sschan = ServerSocketChannel.open();
			sschan.configureBlocking(false);
			ss = sschan.socket();
			ss.bind(new InetSocketAddress(port));
			selector = Selector.open();
			sschan.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
			idleTimer.cancel();
			System.exit(0);
		}
 
		while(true)
		{
			// now we select any pending io
			try{n = selector.select();}	// select
			catch(Exception e){Functions.fail(e,"select failed");}
			Functions.dout(0,"select n="+n);
 
			// process any selected keys
			Set selectedKeys = selector.selectedKeys();
			it = selectedKeys.iterator();
			while(it.hasNext())
			{
				key = (SelectionKey)it.next();
				int kro = key.readyOps();
				Functions.dout(0,"kro="+kro);
				if((kro & SelectionKey.OP_READ) == SelectionKey.OP_READ)doRead(key);
				if((kro & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE)doWrite(key);
				if((kro & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)doAccept(key);
				it.remove();			// remove the key
			}
		}
	}
 
	private void doAccept(SelectionKey sk)
	{
		ServerSocketChannel sc = (ServerSocketChannel)sk.channel();
		Functions.dout(2,"accept");
		SocketChannel usc = null;
		ByteBuffer data;
		try
		{
			usc = sc.accept();
			usc.configureBlocking(false);
			Socket sock = usc.socket();
			String nm = sock.getInetAddress()+":"+sock.getPort();
			System.out.println("connection from "+nm);
			sock.setKeepAlive(true);
			data = ByteBuffer.allocate(bufsz);
			data.position(data.limit()); // looks like write complete
 			SelectionKey dsk = usc.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,null);
			Connection conn = new NIOConnection(dsk);	// contains socket i/o code
			conn.setName(nm);
			new User(conn, perUser, idleTime);		// contains the user application code
			dsk.attach(conn);				// link it to the key so we can find it
 		}
		catch(IOException re){Functions.fail(re,"registration error");}
	}
 
	private void doRead(SelectionKey sk)
	{
		NIOConnection conn = (NIOConnection)sk.attachment();	// get our connection
		conn.doRead();
	}
 
	private void doWrite(SelectionKey sk)
	{
		NIOConnection conn = (NIOConnection)sk.attachment();	// get our connection
		conn.doWrite();
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
		new NIOSwitch().listen(port,idleTime);
	}
 
        private void oneSec()
        {
                Object[] uv = perUser.allUsers();
                for(int i=0; i<uv.length; i++)((User)uv[i]).oneSec();
        }
}
 
