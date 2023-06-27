package proj;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final Connection connection;
    private final String tableName;

    public ProductService(Connection connection, String name) {
        this.connection = connection;
        tableName = name;
    }

    public int insert(Product product) {
        String sql = "INSERT INTO " + tableName + " (name, characteristic, manufacturer, price, quantity, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCharacteristic());
            statement.setString(3, product.getManufacturer());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setString(6, product.getCategory());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Product created with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting product: " + e.getMessage());
        }
        return -1;
    }


    public Product read(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int productId = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    int quantity = resultSet.getInt("quantity");
                    String category = resultSet.getString("category");
                    String characteristic = resultSet.getString("characteristic");
                    String manufacturer = resultSet.getString("manufacturer");

                    return new Product(productId, name, price, quantity, category, characteristic, manufacturer);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
        }
        return null;
    }


    public void update(Product product) {
        String sql = "UPDATE " + tableName + " SET name = ?, characteristic = ?, manufacturer = ?, price = ?, quantity = ?, category = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCharacteristic());
            statement.setString(3, product.getManufacturer());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setString(6, product.getCategory());
            statement.setInt(7, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }


    public void delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts(String columnName, String filterValue) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, filterValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    int quantity = resultSet.getInt("quantity");
                    String category = resultSet.getString("category");
                    String characteristic = resultSet.getString("characteristic");
                    String manufacturer = resultSet.getString("manufacturer");

                    Product product = new Product(id, name, price, quantity, category, characteristic, manufacturer);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String category = resultSet.getString("category");
                String characteristic = resultSet.getString("characteristic");
                String manufacturer = resultSet.getString("manufacturer");

                Product product = new Product(id, name, price, quantity, category, characteristic, manufacturer);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }
        return products;
    }

    public void drop() {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Table dropped: " + tableName);
        } catch (SQLException e) {
            System.out.println("Error dropping table: " + e.getMessage());
        }
    }

}
