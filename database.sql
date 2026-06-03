CREATE DATABASE IF NOT EXISTS ProductManagement;
USE ProductManagement;

CREATE TABLE IF NOT EXISTS Product (
    Product_Id INT PRIMARY KEY AUTO_INCREMENT,
    Product_Name VARCHAR(100) NOT NULL UNIQUE,
    Product_Price FLOAT NOT NULL CHECK (Product_Price > 0),
    Product_Title VARCHAR(200) NOT NULL,
    Product_created DATE NOT NULL,
    Product_catalog VARCHAR(100) NOT NULL,
    Product_Status BIT DEFAULT 1
);

DELIMITER //

DROP PROCEDURE IF EXISTS get_all_products //
CREATE PROCEDURE get_all_products()
BEGIN
    SELECT Product_Id, Product_Name, Product_Price, Product_Title, Product_created, Product_catalog, Product_Status FROM Product;
END //

DROP PROCEDURE IF EXISTS exists_catalog //
CREATE PROCEDURE exists_catalog(IN cat_name VARCHAR(100), OUT is_exist INT)
BEGIN
    DECLARE count_cat INT DEFAULT 0;
    SELECT COUNT(*) INTO count_cat FROM Product WHERE Product_catalog = cat_name;
    IF count_cat > 0 THEN
        SET is_exist = 1;
    ELSE
        SET is_exist = 0;
    END IF;
END //

DROP PROCEDURE IF EXISTS add_product //
CREATE PROCEDURE add_product(
    IN p_name VARCHAR(100),
    IN p_price FLOAT,
    IN p_title VARCHAR(200),
    IN p_created DATE,
    IN p_catalog VARCHAR(100),
    IN p_status BIT
)
BEGIN
    INSERT INTO Product(Product_Name, Product_Price, Product_Title, Product_created, Product_catalog, Product_Status)
    VALUES (p_name, p_price, p_title, p_created, p_catalog, p_status);
END //

DROP PROCEDURE IF EXISTS update_product //
CREATE PROCEDURE update_product(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_price FLOAT,
    IN p_title VARCHAR(200),
    IN p_created DATE,
    IN p_catalog VARCHAR(100),
    IN p_status BIT
)
BEGIN
    UPDATE Product
    SET Product_Name = p_name,
        Product_Price = p_price,
        Product_Title = p_title,
        Product_created = p_created,
        Product_catalog = p_catalog,
        Product_Status = p_status
    WHERE Product_Id = p_id;
END //

DROP PROCEDURE IF EXISTS delete_product //
CREATE PROCEDURE delete_product(IN p_id INT)
BEGIN
    DELETE FROM Product WHERE Product_Id = p_id;
END //

DROP PROCEDURE IF EXISTS get_product_by_id //
CREATE PROCEDURE get_product_by_id(IN p_id INT)
BEGIN
    SELECT Product_Id, Product_Name, Product_Price, Product_Title, Product_created, Product_catalog, Product_Status FROM Product WHERE Product_Id = p_id;
END //

DROP PROCEDURE IF EXISTS search_product_by_name //
CREATE PROCEDURE search_product_by_name(IN p_name VARCHAR(100))
BEGIN
    SELECT Product_Id, Product_Name, Product_Price, Product_Title, Product_created, Product_catalog, Product_Status 
    FROM Product 
    WHERE Product_Name LIKE CONCAT('%', p_name, '%');
END //

DROP PROCEDURE IF EXISTS count_product_by_catalog //
CREATE PROCEDURE count_product_by_catalog()
BEGIN
    SELECT Product_catalog, COUNT(*) AS count_catalog 
    FROM Product 
    GROUP BY Product_catalog;
END //

DELIMITER ;
