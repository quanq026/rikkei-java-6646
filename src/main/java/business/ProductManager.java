package business;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManager {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "{call get_all_products()}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        }
        return list;
    }

    public boolean checkCatalogExists(String catalogName) throws SQLException {
        String sql = "{call exists_catalog(?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, catalogName);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(2) == 1;
        }
    }

    public boolean addProduct(Product product) throws SQLException {
        String sql = "{call add_product(?, ?, ?, ?, ?, ?)}";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, product.getProductName());
                stmt.setFloat(2, product.getProductPrice());
                stmt.setString(3, product.getProductTitle());
                stmt.setDate(4, product.getProductCreated());
                stmt.setString(5, product.getProductCatalog());
                stmt.setBoolean(6, product.isProductStatus());

                int affected = stmt.executeUpdate();
                conn.commit(); // Commit Transaction
                return affected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback Transaction on Failure
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "{call update_product(?, ?, ?, ?, ?, ?, ?)}";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, product.getProductId());
                stmt.setString(2, product.getProductName());
                stmt.setFloat(3, product.getProductPrice());
                stmt.setString(4, product.getProductTitle());
                stmt.setDate(5, product.getProductCreated());
                stmt.setString(6, product.getProductCatalog());
                stmt.setBoolean(7, product.isProductStatus());

                int affected = stmt.executeUpdate();
                conn.commit(); // Commit Transaction
                return affected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback Transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "{call delete_product(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Product getProductById(int id) throws SQLException {
        String sql = "{call get_product_by_id(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }
        return null;
    }

    public List<Product> searchProductByName(String name) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "{call search_product_by_name(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    public Map<String, Integer> countProductsByCatalog() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "{call count_product_by_catalog()}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("Product_catalog"), rs.getInt("count_catalog"));
            }
        }
        return stats;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("Product_Id"),
                rs.getString("Product_Name"),
                rs.getFloat("Product_Price"),
                rs.getString("Product_Title"),
                rs.getDate("Product_created"),
                rs.getString("Product_catalog"),
                rs.getBoolean("Product_Status")
        );
    }
}
