package javaLanguage.basic.socket.another;


/**
 * a Swing GUI that uses the Client and ConnectionUser interfaces
 * part of the pkwnet package
 *
 * @author PKWooster
 * @version 1.0 June 18,2004
 */
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
// client class that talks to the "Switch" server
public class ClientGUI extends JFrame implements ConnectionUser
{
	// swing GUI components
	private JTextField userText = new JTextField(40);		// input text
	private JTextArea log = new JTextArea(24,40);			// logging window
	private JTextField statusText = new JTextField(40);		// status
	private JPanel outPanel = new JPanel();
	private JScrollPane logScroll = new JScrollPane(log);
 
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem startItem = new JMenuItem("Start");
	private JMenuItem hostItem = new JMenuItem("Host");
	private JMenuItem aboutItem = new JMenuItem("About");
	private JMenuItem abortItem = new JMenuItem("Abort");
	private JMenuItem exitItem = new JMenuItem("Exit");
	private JMenu fileMenu = new JMenu("File");
	private JMenu helpMenu = new JMenu("Help");
 
	private Container cp;
 
	// teminal stuff
	private Client client;        					// setwork support
	private String address="127.0.0.1";				// default host is self
	private int port=5050;
	private int state = Connection.CLOSED;
	
        /**
         * construct connected to a Client
         */
	ClientGUI(Client client)
	{
                this.client = client;
		buildMenu();
		cp = getContentPane();
		log.setEditable(false);
		outPanel.add(new JLabel("Send: "));
		outPanel.add(userText);
 
		// enter on userText causes transmit
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){userTyped(evt);}
		});
		cp.setLayout(new BorderLayout());
		cp.add(outPanel,BorderLayout.NORTH);
		cp.add(logScroll,BorderLayout.CENTER);
		cp.add(statusText,BorderLayout.SOUTH);
		setStatus("Closed");
		addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent evt){mnuExit();}});
		pack();
	}
 
	// user pressed enter in the user text field, we try to send the text
	private void userTyped(ActionEvent evt)
	{
		String txt = evt.getActionCommand()+"\n";
		userText.setText("");	// don't send it twice
		toLog(txt);
		if(state == Connection.OPENED)client.send(txt); // send the text
	}
 
	private void toLog(String txt)
	{
		log.append(txt);
		log.setCaretPosition(log.getDocument().getLength() ); // force last line visible
	}
 
	// build the standard menu bar
	private void buildMenu()
	{
		JMenuItem item;
 
		// file menu
		startItem.addActionListener(new ActionListener()
		{public void actionPerformed(ActionEvent e){mnuStart();}});
		fileMenu.add(startItem);
 
		hostItem.addActionListener(new ActionListener()
		{public void actionPerformed(ActionEvent e){mnuHost();}});
		fileMenu.add(hostItem);
 
		exitItem.addActionListener(new ActionListener()
		{public void actionPerformed(ActionEvent e){mnuExit();}});
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
 
		helpMenu.add(aboutItem);
		aboutItem.addActionListener(new ActionListener()
		{public void actionPerformed(ActionEvent e){mnuAbout();}});
 
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	}
 
	// start and stop menu
	private void mnuStart()
	{
                if(state ==  Connection.CLOSED)
                {
                        client.setAddress(address);
                        client.setPort(port);
                        client.connect();
                }
		else client.disconnect();
	}
 
	// prompt user for host in form address:port
	// default is 127.0.0.1:5050
	private void mnuHost()
	{
		String txt = JOptionPane.showInputDialog("Enter host address and port",address+":"+port);
		if (txt == null)return;
 
		int n = txt.indexOf(':');
		if(n == 0)
		{
			address = "127.0.0.1";
			port = Functions.toInt(txt.substring(1),5050);
		}
		else if(n < 0)
		{
			address = txt;
			port = 5050;
		}
		else
		{
			address = txt.substring(0,n);
			port = Functions.toInt(txt.substring(n+1),5050);
		}
	}
 
	private void mnuAbout()
	{
		new AboutDialog(this).show();
		System.out.println("about called");
	}
 
	// exit menu
	private void mnuExit()
	{
		client.disconnect();	 	// close socket
		System.exit(0);
	}
 
	private void setSockState (int s)
	{
		if(state != s)
		{
			state = s;
			switch(state)
			{
				case Connection.OPENED:
					startItem.setText("Stop");
					setStatus("Connected to "+address);
					break;
				case Connection.CLOSED:
					startItem.setText("Start");
					setStatus("Disconnected");
					break;
				case Connection.OPENING:
					setStatus("Connecting to "+address);
					startItem.setText("Abort");
					break;
 
				case Connection.CLOSING:
					setStatus("Disconnecting from "+address);
					startItem.setText("Abort");
					break;
			}
		}
	}
 
	private void setStatus(String st){statusText.setText(st);}
 
        // methods required by ConnectionUser interface
        // these need to be queued to run in the AWT Event Dispatch Tread
        public void stateChange(final int st)
        {
                SwingUtilities.invokeLater(new Runnable(){public void run(){setSockState(st);}});                
        }
        
        public void receive(final String text)
        {
                SwingUtilities.invokeLater(new Runnable(){public void run(){toLog(text);}});                
        }
 
	//============================================================================================
	// inner classes
 
	//----------------------------------------------------------------------------
	// about dialog
	private class AboutDialog extends JDialog
	{
		Container contentPane;
		JTextField text = new JTextField("Simple character client");
 
		AboutDialog(Frame f)
		{
			super(f,"About Client",true);
			contentPane = getContentPane();
			contentPane.add(text);
			pack();
		}
	}
}
 
