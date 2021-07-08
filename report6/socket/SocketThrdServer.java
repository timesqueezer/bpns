package socket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.HashMap;

import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

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
		lastAuthAttemptPerIP = new HashMap<InetAddress, LocalTime>();

	} // End Constructor

	public void listenSocket() {
		SSLServerSocketFactory ssf = null;
        try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "oopha7eeS0BieX0H".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("pkcs12");

            ks.load(new FileInputStream("serverKeyStore.p12"), passphrase);
            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            ssf = ctx.getServerSocketFactory();
            server = ssf.createServerSocket(4444);

        } catch (Exception e) {
            e.printStackTrace();
        }

		while(true) {
			ClientWorker w;
			try {
				Socket socket = server.accept();
				w = new ClientWorker(socket, textArea, this);
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

	protected void finalize() {
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
