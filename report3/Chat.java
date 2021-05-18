import java.io.IOException;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;


public class Chat {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public Chat() {
        try {
            socket = new DatagramSocket(9999);

        } catch (SocketException e) {
            e.printStackTrace();

        }
    }

    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                System.out.println(received);
                // socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public static void main(String[] args) {
        Chat chat = new Chat();
        chat.run();
    }

}
