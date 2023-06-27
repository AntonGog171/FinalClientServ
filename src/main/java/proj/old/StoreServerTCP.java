package proj.old;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class StoreServerTCP implements Runnable {
    // const
    private static final int num_clients = 10;
    private static final int byteSize = 256;
    private static final int PORT = 1310;
    private ServerSocket serverSocket;


    public Socket getSocket() {
        return socket;
    }

    private Socket socket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        for (int i = 0; i < num_clients; i++){
            try {
                socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                byte[] buf = new byte[byteSize];
                Package userPack;
                userPack = new Package(buf);
                out.write(Mes_Responser.response(userPack));
            } catch (Exception e) {
                throw new RuntimeException("Unable to set pack for response", e);
            }
        }
    }

    @Override
    public void run() {
        StoreServerTCP server = new StoreServerTCP();
        try {
            server.start(PORT);
        } catch (IOException e){
            throw new RuntimeException("", e);
        }
    }
}
