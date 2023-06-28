package proj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static proj.Server.SECRET_KEY;
import static proj.Server.adminLOGIN;

public class ApiCategoryService implements HttpHandler {
    public static final String PATH = "/api/category";
    private CategoryService categoryService;


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!auth(exchange)) {
            return;
        }
        String dbFileName = Server.product_db;
        Connect conn = new Connect(dbFileName, Server.category_db);
        categoryService = new CategoryService(conn.getCon(), dbFileName, Server.category_db);
        String method = exchange.getRequestMethod();
        String data = exchange.getRequestURI().toString().substring(PATH.length());

        if (method.equals("GET")) {
            handleGetRequest(exchange, data);
        } else if (method.equals("POST")) {
            handlePostRequest(exchange, data);
        } else if (method.equals("PUT")) {
            handlePutRequest(exchange, data);
        } else if (method.equals("DELETE")) {
            handleDeleteRequest(exchange, data);
        } else {
            sendErrorResponse(exchange, 404, "Invalid request");
        }
    }

    private void handleGetRequest(HttpExchange exchange, String data) throws IOException {
        if (data.equals("")) {
            List<Category> categories = categoryService.getAllCategories();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(categories);
            sendSuccessResponse(exchange, 200, json);
        } else {
            Category category = categoryService.getCategoryByName(data.substring(1));
            if (category != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(category);
                sendSuccessResponse(exchange, 200, json);
            } else {
                sendErrorResponse(exchange, 404, "Category not found");
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange, String data) throws IOException {
        if (data.equals("")) {
            System.out.println("POST1");
            InputStream in = exchange.getRequestBody();
            String json = new String(in.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(json);
            Category category = objectMapper.readValue(json, Category.class);
            System.out.println("POST2");
            if (categoryService.getCategoryByName(category.getName()) != null) {
                sendErrorResponse(exchange, 409, "Category already exists");
            } else {
                categoryService.addCategory(category);
                sendSuccessResponse(exchange, 201, "Category created");
            }
        } else {
            sendErrorResponse(exchange, 404, "Invalid request");
        }
    }

    private void handlePutRequest(HttpExchange exchange, String data) throws IOException {
        if (Pattern.matches("/\\w+", data)) {
            String categoryName = data.substring(1);
            InputStream in = exchange.getRequestBody();
            String json = new String(in.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            Category category = objectMapper.readValue(json, Category.class);
            if (categoryService.getCategoryByName(categoryName) != null) {
                categoryService.updateCategory(category);
                sendSuccessResponse(exchange, 200, "Category updated");
            } else {
                sendErrorResponse(exchange, 404, "Category not found");
            }
        } else {
            sendErrorResponse(exchange, 404, "Invalid request");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String data) throws IOException {
        if (Pattern.matches("/\\w+", data)) {
            String categoryName = data.substring(1);
            Category category = categoryService.getCategoryByName(categoryName);
            if (category != null) {
                categoryService.deleteCategory(categoryName);
                sendSuccessResponse(exchange, 200, "Category deleted");
            } else {
                sendErrorResponse(exchange, 404, "Category not found");
            }
        } else {
            sendErrorResponse(exchange, 404, "Invalid request");
        }
    }

    private boolean auth(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(exchange, 401, "Unauthorized");
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenLogin = claims.get("login", String.class);
            if (!Objects.equals(tokenLogin, adminLOGIN)) {
                sendErrorResponse(exchange, 401, "Unauthorized");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendErrorResponse(exchange, 401, "Unauthorized");
            return false;
        }
        return true;
    }

    private void sendSuccessResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
