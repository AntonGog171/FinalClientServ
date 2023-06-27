package proj;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Server {
    public static String adminLOGIN="admin";
    public static String adminPASSWORD="password";
    public static final String SECRET_KEY = "TESTKEYLONGENOUGHTESTKEYLONGENOUGH";
    public static final String product_db="product";
    private static HttpServer server;
    public static void run() throws Exception {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(1310), 0);
        HttpContext goodsapi = server.createContext(ApiGoodsHandler.PATH, new ApiGoodsHandler());
        HttpContext loginapi = server.createContext(ApiLoginHandler.PATH, new ApiLoginHandler());
        HttpContext index = server.createContext("/", new IndexHandler());
        HttpContext goods=server.createContext("/manage_products", new GoodsHandler());
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(40));
        server.start();
    }
    public static void kill(){
        server.stop(1);
    }
    public static void main(String[] args) throws Exception{
        run();
    }
    static class GoodsHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "D:\\clinet server\\ClientServerLab05\\Final_Project\\src\\main\\java\\proj\\HTML\\manage_products.html";
            File file = new File(filePath);
            if (file.exists()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                sendErrorResponse(exchange, 404, "File Not Found");
            }
        }
        private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] response = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "D:\\clinet server\\ClientServerLab05\\Final_Project\\src\\main\\java\\proj\\HTML\\index.html";
            File file = new File(filePath);
            if (file.exists()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                sendErrorResponse(exchange, 404, "File Not Found");
            }
        }
        private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] response = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

}
