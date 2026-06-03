package presentation;

import business.ProductManager;
import model.Product;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final ProductManager manager = new ProductManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static {
        dateFormat.setLenient(false);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        while (true) {
            System.out.println("\n********************PRODUCT MANAGEMENT****************");
            System.out.println("1. Danh sách sản phẩm");
            System.out.println("2. Thêm mới sản phẩm");
            System.out.println("3. Cập nhật sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo tên sản phẩm");
            System.out.println("6. Sắp xếp sản phẩm theo giá tăng dần");
            System.out.println("7. Thống kê số lượng sản phẩm theo danh mục");
            System.out.println("8. Thoát");
            System.out.print("Chọn chức năng: ");

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        displayAllProducts();
                        break;
                    case 2:
                        addNewProduct();
                        break;
                    case 3:
                        updateProduct();
                        break;
                    case 4:
                        deleteProduct();
                        break;
                    case 5:
                        searchProduct();
                        break;
                    case 6:
                        sortProductsByPrice();
                        break;
                    case 7:
                        statisticByCatalog();
                        break;
                    case 8:
                        System.out.println("Thoát chương trình...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Lỗi thao tác CSDL: " + e.getMessage());
            }
        }
    }

    private static void displayAllProducts() throws SQLException {
        List<Product> products = manager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Chưa có sản phẩm nào.");
            return;
        }
        System.out.println("\n=== DANH SÁCH SẢN PHẨM ===");
        for (Product p : products) {
            printProductDetail(p);
        }
    }

    private static void addNewProduct() throws SQLException {
        System.out.println("\n=== THÊM MỚI SẢN PHẨM ===");
        Product product = new Product();
        inputProductData(product, false);

        if (manager.addProduct(product)) {
            System.out.println("Thêm sản phẩm mới thành công!");
        } else {
            System.out.println("Thêm sản phẩm thất bại.");
        }
    }

    private static void updateProduct() throws SQLException {
        System.out.println("\n=== CẬP NHẬT SẢN PHẨM ===");
        System.out.print("Nhập mã sản phẩm cần sửa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Mã sản phẩm không hợp lệ.");
            return;
        }

        Product existing = manager.getProductById(id);
        if (existing == null) {
            System.out.println("Không tìm thấy sản phẩm có mã này.");
            return;
        }

        System.out.println("Thông tin hiện tại:");
        printProductDetail(existing);

        Product updated = new Product();
        updated.setProductId(id);
        inputProductData(updated, true);

        if (manager.updateProduct(updated)) {
            System.out.println("Cập nhật sản phẩm thành công!");
        } else {
            System.out.println("Cập nhật sản phẩm thất bại.");
        }
    }

    private static void deleteProduct() throws SQLException {
        System.out.println("\n=== XÓA SẢN PHẨM ===");
        System.out.print("Nhập mã sản phẩm cần xóa: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Mã sản phẩm không hợp lệ.");
            return;
        }

        Product existing = manager.getProductById(id);
        if (existing == null) {
            System.out.println("Không tìm thấy sản phẩm có mã này.");
            return;
        }

        if (manager.deleteProduct(id)) {
            System.out.println("Xóa sản phẩm thành công!");
        } else {
            System.out.println("Xóa sản phẩm thất bại.");
        }
    }

    private static void searchProduct() throws SQLException {
        System.out.println("\n=== TÌM KIẾM SẢN PHẨM ===");
        System.out.print("Nhập tên sản phẩm cần tìm: ");
        String name = scanner.nextLine().trim();
        List<Product> products = manager.searchProductByName(name);
        if (products.isEmpty()) {
            System.out.println("Không tìm thấy sản phẩm nào phù hợp.");
            return;
        }
        System.out.println("\nKết quả tìm kiếm:");
        for (Product p : products) {
            printProductDetail(p);
        }
    }

    private static void sortProductsByPrice() throws SQLException {
        List<Product> products = manager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Chưa có sản phẩm nào để sắp xếp.");
            return;
        }
        products.sort(Comparator.comparingDouble(Product::getProductPrice));
        System.out.println("\n=== DANH SÁCH SẢN PHẨM SẮP XẾP THEO GIÁ TĂNG DẦN ===");
        for (Product p : products) {
            printProductDetail(p);
        }
    }

    private static void statisticByCatalog() throws SQLException {
        System.out.println("\n=== THỐNG KÊ SỐ LƯỢNG SẢN PHẨM THEO DANH MỤC ===");
        Map<String, Integer> stats = manager.countProductsByCatalog();
        if (stats.isEmpty()) {
            System.out.println("Chưa có dữ liệu thống kê.");
            return;
        }
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            System.out.printf("Danh mục: %s | Số lượng: %d%n", entry.getKey(), entry.getValue());
        }
    }

    private static void inputProductData(Product product, boolean isUpdate) {
        // Product Name validation
        while (true) {
            System.out.print("Nhập tên sản phẩm (không rỗng, tối đa 100 ký tự): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty() || name.length() > 100) {
                System.out.println("Tên sản phẩm không hợp lệ, vui lòng nhập lại.");
                continue;
            }
            product.setProductName(name);
            break;
        }

        // Product Price validation
        while (true) {
            System.out.print("Nhập giá sản phẩm (> 0): ");
            try {
                float price = Float.parseFloat(scanner.nextLine());
                if (price <= 0) {
                    System.out.println("Giá sản phẩm phải lớn hơn 0.");
                    continue;
                }
                product.setProductPrice(price);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập giá hợp lệ.");
            }
        }

        // Product Title validation
        while (true) {
            System.out.print("Nhập tiêu đề sản phẩm (không rỗng): ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) {
                System.out.println("Tiêu đề không được để trống.");
                continue;
            }
            product.setProductTitle(title);
            break;
        }

        // Product Created Date validation
        while (true) {
            System.out.print("Nhập ngày tạo sản phẩm (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine().trim();
            try {
                java.util.Date parsed = dateFormat.parse(dateStr);
                product.setProductCreated(new Date(parsed.getTime()));
                break;
            } catch (ParseException e) {
                System.out.println("Ngày tạo không hợp lệ (định dạng đúng: yyyy-MM-dd). Vui lòng nhập lại.");
            }
        }

        // Product Catalog validation
        while (true) {
            System.out.print("Nhập danh mục sản phẩm (không rỗng): ");
            String catalog = scanner.nextLine().trim();
            if (catalog.isEmpty()) {
                System.out.println("Danh mục không được để trống.");
                continue;
            }
            product.setProductCatalog(catalog);
            break;
        }

        // Product Status validation
        while (true) {
            System.out.print("Nhập trạng thái sản phẩm (true-Hoạt động, false-Khóa): ");
            String statusStr = scanner.nextLine().trim().toLowerCase();
            if (statusStr.equals("true") || statusStr.equals("1")) {
                product.setProductStatus(true);
                break;
            } else if (statusStr.equals("false") || statusStr.equals("0")) {
                product.setProductStatus(false);
                break;
            } else {
                System.out.println("Trạng thái không hợp lệ, nhập lại.");
            }
        }
    }

    private static void printProductDetail(Product p) {
        System.out.printf("ID: %d | Tên: %s | Giá: %.2f | Tiêu đề: %s | Ngày tạo: %s | Danh mục: %s | Trạng thái: %s%n",
                p.getProductId(),
                p.getProductName(),
                p.getProductPrice(),
                p.getProductTitle(),
                p.getProductCreated().toString(),
                p.getProductCatalog(),
                p.isProductStatus() ? "Hoạt động" : "Khóa"
        );
    }
}
