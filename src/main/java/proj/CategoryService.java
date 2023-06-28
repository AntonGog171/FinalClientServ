package proj;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryService {
    private final Connection connection;
    private final String categoryTableName;
    private final String productTableName;

    public CategoryService(Connection connection, String productTableName, String categoryTableName) {
        this.connection = connection;
        this.categoryTableName = categoryTableName;
        this.productTableName = productTableName;
    }

    public int addCategory(Category category) {
        String categoryName = category.getName();
        if (!categoryExists(categoryName)) {
            String sql = "INSERT INTO " + categoryTableName + " (name, description) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, categoryName);
                statement.setString(2, category.getDescription());
                statement.executeUpdate();
                System.out.println("Category added: " + categoryName);
                return 1;
            } catch (SQLException e) {
                System.out.println("Error adding category: " + e.getMessage());
                return 2;
            }
        } else {
            System.out.println("Category already exists: " + categoryName);
            return 3;
        }
    }

    public int updateCategory(Category category) {
        String categoryName = category.getName();
        if (categoryExists(categoryName)) {
            String sql = "UPDATE " + categoryTableName + " SET description = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, category.getDescription());
                statement.setString(2, categoryName);
                statement.executeUpdate();
                System.out.println("Category updated: " + categoryName);
                return 1;
            } catch (SQLException e) {
                System.out.println("Error updating category: " + e.getMessage());
                return 2;
            }
        } else {
            System.out.println("Category does not exist: " + categoryName);
            return 3;
        }
    }

    public int deleteCategory(String categoryName) {
        if (categoryExists(categoryName)) {
            String sql = "DELETE FROM " + categoryTableName + " WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, categoryName);
                statement.executeUpdate();
                deleteProductsByCategory(categoryName);
                System.out.println("Category deleted: " + categoryName);
                return 1;
            } catch (SQLException e) {
                System.out.println("Error deleting category: " + e.getMessage());
                return 2;
            }
        } else {
            System.out.println("Category does not exist: " + categoryName);
            return 3;
        }
    }

    public int addProductToCategory(String categoryName, Product product) {
        if (categoryExists(categoryName)) {
            String productName = product.getName();
            if (!productExists(productName)) {
                String sql = "INSERT INTO " + productTableName + " (name, description, manufacturer, price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, productName);
                    statement.setString(2, product.getCharacteristic());
                    statement.setString(3, product.getManufacturer());
                    statement.setDouble(4, product.getPrice());
                    statement.executeUpdate();
                    System.out.println("Product added to category: " + categoryName);
                    return 1;
                } catch (SQLException e) {
                    System.out.println("Error adding product to category: " + e.getMessage());
                    return 2;
                }
            } else {
                System.out.println("Product already exists: " + productName);
            }
        } else {
            System.out.println("Category does not exist: " + categoryName);
        }
        return 3;
    }

    public int updateProductInCategory(String categoryName, Product product) {
        if (categoryExists(categoryName)) {
            String productName = product.getName();
            if (productExists(productName)) {
                String sql = "UPDATE " + productTableName + " SET characteristic = ?, manufacturer = ?, price = ? WHERE name = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, product.getCharacteristic());
                    statement.setString(2, product.getManufacturer());
                    statement.setDouble(3, product.getPrice());
                    statement.setString(4, productName);
                    statement.executeUpdate();
                    System.out.println("Product updated in category: " + categoryName);
                    return 1;
                } catch (SQLException e) {
                    System.out.println("Error updating product in category: " + e.getMessage());
                    return 2;
                }
            } else {
                System.out.println("Product does not exist: " + productName);
            }
        } else {
            System.out.println("Category does not exist: " + categoryName);
        }
        return 3;
    }

    public int deleteProductFromCategory(String categoryName, String productName) {
        if (categoryExists(categoryName)) {
            if (productExists(productName)) {
                String sql = "DELETE FROM " + productTableName + " WHERE name = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, productName);
                    statement.executeUpdate();
                    System.out.println("Product deleted from category: " + categoryName);
                    return 1;
                } catch (SQLException e) {
                    System.out.println("Error deleting product from category: " + e.getMessage());
                    return 2;
                }
            } else {
                System.out.println("Product does not exist: " + productName);
            }
        } else {
            System.out.println("Category does not exist: " + categoryName);
        }
        return 3;
    }

    private boolean categoryExists(String categoryName) {
        String sql = "SELECT COUNT(*) FROM " + categoryTableName + " WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking category existence: " + e.getMessage());
        }
        return false;
    }

    private boolean productExists(String productName) {
        String sql = "SELECT COUNT(*) FROM " + productTableName + " WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking product existence: " + e.getMessage());
        }
        return false;
    }

    private int deleteProductsByCategory(String categoryName) {
        String sql = "DELETE FROM " + productTableName + " WHERE category = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            statement.executeUpdate();
            System.out.println("Products deleted from category: " + categoryName);
            return 1;
        } catch (SQLException e) {
            System.out.println("Error deleting products from category: " + e.getMessage());
            return 2;
        }
    }

    public Category getCategoryByName(String categoryName) {
        String sql = "SELECT * FROM " + categoryTableName + " WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    return new Category(name, description);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving category: " + e.getMessage());
        }
        return null;
    }
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM " + categoryTableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                categories.add(new Category(name, description));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
        }
        return categories;
    }
}
