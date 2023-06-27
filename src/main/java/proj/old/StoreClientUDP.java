package proj.old;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoreClientUDP {
    private static final int PORT = 1310;
    private static final int byteSize = 256;
    private DatagramSocket socket;
    private InetAddress ip;

    private byte[] buf;

    public StoreClientUDP() {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(null);
        } catch (SocketException | UnknownHostException e){
            throw new RuntimeException("", e);
        }

    }



    private void tryToResend(Map<Long, byte[]> allReceived, Map<Long, byte[]> allSent) throws IOException {
        if(!allSent.isEmpty()){
            Set<Long> pktIds = allReceived.keySet();
            for(Long id : pktIds){
                allSent.remove(id);
            }
            allReceived.clear();
            Set<Long> idsSent = allSent.keySet();
            for (Long id : idsSent) {
                byte[] bytes = allSent.get(id);

                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), PORT);
                socket.send(packet);

                allSent.put(id, bytes);

                final byte[] inputMessage = new byte[byteSize];
                final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);

                try {
                    socket.receive(response);

                    final int realMessageSize = response.getLength();
                    byte[] responseBytes = new byte[realMessageSize];
                    System.arraycopy(response.getData(), 0, responseBytes, 0, responseBytes.length);
                    Package responsePacket;
                    responsePacket = new Package(responseBytes);
                    allReceived.put(responsePacket.getbPktId(), responseBytes);
                    System.out.println("Response for " + responsePacket.getbSrc() + " : " + new String(responsePacket.getbMsg().getMessageBMsq()));

                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout");
                }
            }
        }
    }

    public void sendEcho() throws IOException{

        Map<Long, byte[]> sent = new HashMap<>();
        Map<Long, byte[]> recieved = new HashMap<>();
        socket.setSoTimeout(2_000);
        Package send_Pack = Rand_Mes_Gen.generateFakeMessage();
        buf = send_Pack.packToBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, PORT);
        socket.send(packet);
        System.out.println("\n Message sent by client to server: \n" + send_Pack.toString());
        sent.put(send_Pack.getbPktId(), buf);

        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e){
            socket.close();
        }
        Package rec_Pack = new Package(packet.getData());
        System.out.println("\n Message received by client from server: \n" + rec_Pack.toString());
        recieved.put(rec_Pack.getbPktId(), buf);
        tryToResend(recieved, sent);
    }

}
