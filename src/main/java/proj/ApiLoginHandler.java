package proj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static proj.Server.SECRET_KEY;
import static proj.Server.adminPASSWORD;


public class ApiLoginHandler implements HttpHandler {

    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000; // 24 hours
    //private static final long EXPIRATION_TIME_MS = 15000;
    public static String PATH="/login";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Read the login and password from the request body
            InputStream in = exchange.getRequestBody();
            String json = new String(in.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestData = objectMapper.readValue(json, new TypeReference<>() {});

            String login = requestData.get("login");
            String password = requestData.get("password");
            System.out.println("Tried to login: "+login+" "+password);
            // Validate the login and password
            if (isValidCredentials(login, password)) {
                // Generate a JWT token
                System.out.println("Valid");
                String token = generateToken(login);

                // Return the token in the response
                String responseJson = "OK " + token;
                //System.out.println(responseJson);
                exchange.sendResponseHeaders(200, responseJson.length());
                OutputStream os = exchange.getResponseBody();
                os.write(responseJson.getBytes());
                os.close();
            } else {
                sendErrorResponse(exchange, 401, "Unauthorized");
            }
        }
    }

    private boolean isValidCredentials(String login, String password) {
        String hashedPassword = getMD5Hash(password);
        String etalonPassword= getMD5Hash(adminPASSWORD);
        return "admin".equals(login) && hashedPassword.equals(etalonPassword);
    }
    private String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String generateToken(String login) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("login", login);
        Key signingKey = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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