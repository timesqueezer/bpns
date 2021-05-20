package socket5_5;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;


class ClientWorker implements Runnable {
	private Socket client;
	private JTextArea textArea;
	private SocketThrdServer server;

	private PrintWriter out = null;

	private boolean authenticated = false;

	ClientWorker(Socket client, JTextArea textArea, SocketThrdServer server) {
		this.client = client;
		this.textArea = textArea;
		this.server = server;
	}

	public void send(String line) {
		out.println(line);
	}

	public void run() {
		String line;
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				line = in.readLine();

				if (authenticated) {
					server.broadcast(line);
					textArea.append(line);

				} else {
					if (server.checkLastAuthAttempt(client) == false) {
						out.println("Too many authentication attempts");
						break;
					}

					if (line.indexOf(":") != -1 &&
						server.authenticate(
						line.split(":")[0],
						line.split(":")[1].toCharArray()
					)) {
						authenticated = true;
						out.println("Authenticated");

					} else {
						out.println("Unauthenicated");
					}
				}

			} catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		}
	}
}
