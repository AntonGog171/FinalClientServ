package proj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static proj.Server.SECRET_KEY;
import static proj.Server.adminLOGIN;

public class ApiGoodsHandler implements HttpHandler {
    public static String PATH="/api/good";
    private static Cipher ecipher;
    private static Cipher dcipher;
    public static final byte[] KEY = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'};

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String dbFileName = Server.product_db;
        Connect conn = new Connect(dbFileName, Server.category_db);
        ProductService ps = new ProductService(conn.getCon(), dbFileName);
        if(!auth(exchange)){return;}

        try {
            ecipher = Cipher.getInstance("AES");
            SecretKeySpec eSpec = new SecretKeySpec(KEY, "AES");
            ecipher.init(Cipher.ENCRYPT_MODE, eSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            dcipher = Cipher.getInstance("AES");
            SecretKeySpec dSpec = new SecretKeySpec(KEY, "AES");
            dcipher.init(Cipher.DECRYPT_MODE, dSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder builder = new StringBuilder();
        String method= exchange.getRequestMethod();
        String DATA=exchange.getRequestURI().toString().substring(PATH.length());
        //System.out.println("uri after good: "+DATA);


        if(method.equals("PUT") && DATA.equals("")){
            InputStream in =exchange.getRequestBody();
            String json=new String(in.readAllBytes());
            //System.out.println(json);
            //System.out.println("-------");
            try {
                json= new String(dcipher.doFinal(Base64.decodeBase64(json.getBytes())));
            }catch (Exception exception){exception.printStackTrace();}

            ObjectMapper objectMapper = new ObjectMapper();
            Product p = null;
            try {
                p = objectMapper.readValue(json, Product.class);
            }catch (Exception ex){ex.printStackTrace();
                sendErrorResponse(exchange, 409,"Conflict, wrong request body");}
            System.out.println(p);
            if(!ps.getAllProducts("name", p.getName()).isEmpty()){
                System.out.println(ps.getAllProducts("name", p.getName()));
                int new_amount=ps.getAllProducts("name", p.getName()).get(0).getQuantity()+p.getQuantity();
                p.setQuantity(new_amount);
                p.setId(ps.getAllProducts("name", p.getName()).get(0).getId());
                if (p.getPrice()>0 && p.getQuantity()>0){
                    ps.update(p);

                    builder.append("Updated with id ");
                    builder.append(p.getId());
                    byte[] bytes = builder.toString().getBytes();
                    exchange.sendResponseHeaders(201, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
                else{
                    sendErrorResponse(exchange, 409,"Conflict, could not insert data or data is wrong");
                }
            }else{
                if (p.getPrice()>0 && p.getQuantity()>0){
                    int newid=ps.insert(p);
                    p.setId(newid);
                    builder.append("Created with id ");
                    builder.append(newid);
                    byte[] bytes = builder.toString().getBytes();
                    exchange.sendResponseHeaders(201, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }else{
                    sendErrorResponse(exchange, 409,"Conflict, could not insert data or data is wrong");
                }
            }
        }else if(method.equals("GET") && DATA.equals("")){
            List<Product> p = ps.getAllProducts();
            if(p != null){
                builder.append("[");
                for (var x : p){
                    builder.append(x.toString().substring(7)+", ");
                }
                builder.deleteCharAt(builder.length()-2);
                builder.deleteCharAt(builder.length()-1);
                builder.append("]");
                try {
                    byte[] bytes = ecipher.doFinal(builder.toString().getBytes());
                    //String  kek = new String(dcipher.doFinal(bytes));
                    //System.out.println(kek);
                    OutputStream os = exchange.getResponseBody();

                    try{
                        byte[] enc = Base64.encodeBase64String(bytes).getBytes();
                        exchange.sendResponseHeaders(200, enc.length);
                        for( var b: enc){
                            os.write(b);
                        }
                    }catch (Exception ex){ex.printStackTrace();}
                    os.close();
                }catch (Exception exception){exception.printStackTrace();}
            }else {
                sendErrorResponse(exchange, 404, "");
            }
        }
        else if(method.equals("GET") && Pattern.matches("/\\d+",DATA)){
            int id=Integer.parseInt(DATA.substring(1));
            Product p = ps.read(id);
            System.out.println(p);
            if(p != null){
                builder.append(p.toString().substring(7));
                try {
                    byte[] bytes = ecipher.doFinal(builder.toString().getBytes());
                    //String  kek = new String(dcipher.doFinal(bytes));
                    //System.out.println(kek);
                    OutputStream os = exchange.getResponseBody();

                    try{
                        byte[] enc = Base64.encodeBase64String(bytes).getBytes();
                        exchange.sendResponseHeaders(200, enc.length);
                        for( var b: enc){
                            os.write(b);
                        }
                    }catch (Exception ex){ex.printStackTrace();}
                    os.close();
                }catch (Exception exception){exception.printStackTrace();}
            }else {
                sendErrorResponse(exchange, 404, "");
            }
        }
        else if(method.equals("POST")&&Pattern.matches("/\\d+",DATA)){
            int id=Integer.parseInt(DATA.substring(1));
            InputStream in =exchange.getRequestBody();
            String json=new String(in.readAllBytes());
            try {
                json= new String(dcipher.doFinal(Base64.decodeBase64(json.getBytes())));
            }catch (Exception exception){exception.printStackTrace();}
            ObjectMapper objectMapper = new ObjectMapper();
            Product p = objectMapper.readValue(json, Product.class);
            if(ps.read(id)!=null){
            p.setId(id);
                if (p.getPrice()>0 && p.getQuantity()>0){
                    ps.update(p);
                    exchange.sendResponseHeaders(204,  -1);
                }else {
                    sendErrorResponse(exchange,409, "Conflict: wrong informantion");
                }
            }else{
                sendErrorResponse(exchange,404,"Not found id");
            }
        }
        else if(method.equals("DELETE")&&Pattern.matches("/\\d+",DATA)){
            int id=Integer.parseInt(DATA.substring(1));
            if(ps.read(id)!=null){
                ps.delete(id);
                exchange.sendResponseHeaders(204,  -1);
            }else{
                sendErrorResponse(exchange,404,"Not found id");
            }
        }else if(method.equals("DELETE")&&Pattern.matches("/\\d+_\\d+",DATA)){
            int indx = 0;
            for(int i=0;i<DATA.length();i++){
                if(DATA.charAt(i)=='_'){
                    indx=i;
                    break;
                }
            }
            int id=Integer.parseInt(DATA.substring(1, indx));
            int am=Integer.parseInt(DATA.substring(indx+1));
            //System.out.println("Removing"+am+" from "+id);
            if(ps.read(id)!=null){
                Product p=ps.read(id);
                if(p.getQuantity()-am>=0){
                    p.setQuantity(p.getQuantity()-am);
                    ps.update(p);
                    exchange.sendResponseHeaders(204,  -1);
                }else {
                    sendErrorResponse(exchange,409,"Cant take so much");
                }

            }else{
                sendErrorResponse(exchange,404,"Not found id");
            }
        }
    }

    private boolean auth(HttpExchange exchange) throws IOException {

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("NO HEADER");
            sendErrorResponse(exchange, 401, "Unauthorized");
            return false;
        }
        String token = authHeader.substring(7);
        try{
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String tokenLogin = claims.get("login", String.class);
        if(!Objects.equals(tokenLogin, adminLOGIN)){
            System.out.println("NOT ADMIN");
            sendErrorResponse(exchange, 401, "Unauthorized");
            return false;
        }
        }catch (Exception ex){
            ex.printStackTrace();
            sendErrorResponse(exchange, 401, "Unauthorized");
            return false;
        }


    return true;
    }


    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
