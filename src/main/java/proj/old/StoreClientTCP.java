package proj.old;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class StoreClientTCP {
    private static final int MAX_RECONNECTIONS = 2;
    private static final int byteSize = 256;
    private static final int PORT = 1310;

    private final Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public StoreClientTCP(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void start() {
        int reconnectAttempts = 0;
        try {
            establishConnection();
        } catch (IOException e) {
            System.out.println("Please wait, reconnecting...");
            reconnect(reconnectAttempts);
        }
    }

    private void establishConnection() throws IOException {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream inputStream = clientSocket.getInputStream();
            writer = new PrintWriter(outputStream, true);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            Package packFromUser;
            packFromUser = Rand_Mes_Gen.generateFakeMessage();
            System.out.println("\n Message sent by client to server: \n" + packFromUser.toString());
            outputStream.write(packFromUser.packToBytes());
            byte[] serverResponse = new byte[byteSize];
            int serverResponseSize = inputStream.read(serverResponse);
            Package received;
            received = new Package(serverResponse);
            System.out.println("\n Message received by client to server: \n" + received.toString());
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }

    }

    private void reconnect(int currentReconnections) {
        try {
            final Socket socket = new Socket(InetAddress.getByName(null), PORT);
            socket.setSoTimeout(3_000 * MAX_RECONNECTIONS);
            start();
        } catch (IOException e) {
            if (currentReconnections == MAX_RECONNECTIONS) {
                System.out.println("Server is disabled");
            } else {
                currentReconnections += 1;
                reconnect(currentReconnections);
            }
        }
    }
}
