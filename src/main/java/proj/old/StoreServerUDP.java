package proj.old;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class StoreServerUDP extends Thread {
    private static final int PORT = 1310;
    private static final int byteSize = 256;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[byteSize];

    public StoreServerUDP()  {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e){
            throw new RuntimeException("", e);
        }

    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException("Can't receive the packet", e);
            }
            Package receivedPack;
            try {
                receivedPack = new Package(buf);
                buf = Mes_Responser.response(receivedPack);
            } catch (Exception e) {
                throw new RuntimeException("Unable to set pack for response", e);
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            byte[] receivedMessage = receivedPack.getbMsg().getMessageBMsq();
            String receivedStr = new String(receivedMessage, StandardCharsets.UTF_8);
            if (receivedStr.equals("end")) {
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException("Can't send the packet", e);
            }
        }
        socket.close();
    }
}
