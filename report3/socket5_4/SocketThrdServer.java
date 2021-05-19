package socket5_4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class SocketThrdServer extends JFrame{

	JLabel label = new JLabel("Text received over socket:");
	JPanel panel;
	JTextArea textArea = new JTextArea();
	ServerSocket server = null;

	ArrayList<ClientWorker> clientWorkers;

	Useradmin useradmin;

	SocketThrdServer() { // Begin Constructor
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", label);
		panel.add("Center", textArea);

		clientWorkers = new ArrayList<ClientWorker>();

		useradmin = new Useradmin();
	} // End Constructor

	public void listenSocket() {
		try {
			server = new ServerSocket(4444);

		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}

		while(true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept(), textArea, this);
				Thread t = new Thread(w);
				t.start();

				clientWorkers.add(w);

			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

	public void broadcast(String line) {
		for (ClientWorker w : clientWorkers) {
			w.send(line);
		}
	}

	public boolean authenticate(String username, char[] password) {
		return useradmin.checkUser(username, password);
	}

	protected void finalize(){
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		SocketThrdServer frame = new SocketThrdServer();
		frame.setTitle("Server Program");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
		frame.pack();
		frame.setVisible(true);
		frame.listenSocket();
	}
}
