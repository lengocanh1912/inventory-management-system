package com.ims.config;

import com.ims.entity.Category;
import com.ims.entity.Product;
import com.ims.entity.User;
import com.ims.enums.ProductStatus;
import com.ims.enums.Role;
import com.ims.enums.UserStatus;
import com.ims.repository.CategoryRepository;
import com.ims.repository.ProductRepository;
import com.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Seeding database...");
        seedUsers();
        seedCategories();
        seedProducts();
        log.info("Database seeded successfully!");
    }

    private void seedUsers() {
        List<User> users = List.of(
            User.builder()
                .name("Admin User")
                .email("admin@store.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build(),
            User.builder()
                .name("Staff Member")
                .email("staff@store.com")
                .password(passwordEncoder.encode("staff123"))
                .role(Role.STAFF)
                .status(UserStatus.ACTIVE)
                .build()
        );
        userRepository.saveAll(users);
        log.info("Seeded {} users", users.size());
    }

    private void seedCategories() {
        List<Category> categories = List.of(
            Category.builder().name("Electronics").description("Thiết bị điện tử").build(),
            Category.builder().name("Accessories").description("Phụ kiện").build(),
            Category.builder().name("Audio").description("Âm thanh").build(),
            Category.builder().name("Storage").description("Lưu trữ").build(),
            Category.builder().name("Components").description("Linh kiện máy tính").build()
        );
        categoryRepository.saveAll(categories);
        log.info("Seeded {} categories", categories.size());
    }

    private void seedProducts() {
        Category electronics = categoryRepository.findByName("Electronics").orElseThrow();
        Category accessories = categoryRepository.findByName("Accessories").orElseThrow();
        Category audio = categoryRepository.findByName("Audio").orElseThrow();
        Category storage = categoryRepository.findByName("Storage").orElseThrow();
        Category components = categoryRepository.findByName("Components").orElseThrow();

        List<Product> products = List.of(
            Product.builder().sku("PRD-001").name("Laptop Dell XPS 13")
                .category(electronics).price(new BigDecimal("25000000"))
                .cost(new BigDecimal("18000000")).stock(24).minStock(5)
                .status(ProductStatus.ACTIVE).supplier("Dell Vietnam").build(),

            Product.builder().sku("PRD-002").name("iPhone 15 Pro Max")
                .category(electronics).price(new BigDecimal("34000000"))
                .cost(new BigDecimal("28000000")).stock(3).minStock(5)
                .status(ProductStatus.ACTIVE).supplier("Apple Vietnam").build(),

            Product.builder().sku("PRD-003").name("Bàn Phím Cơ Keychron K2")
                .category(accessories).price(new BigDecimal("2500000"))
                .cost(new BigDecimal("1500000")).stock(58).minStock(10)
                .status(ProductStatus.ACTIVE).supplier("Keychron").build(),

            Product.builder().sku("PRD-004").name("Màn Hình LG 27UL850")
                .category(electronics).price(new BigDecimal("12000000"))
                .cost(new BigDecimal("9000000")).stock(12).minStock(5)
                .status(ProductStatus.ACTIVE).supplier("LG Vietnam").build(),

            Product.builder().sku("PRD-005").name("Tai Nghe Sony WH-1000XM5")
                .category(audio).price(new BigDecimal("8500000"))
                .cost(new BigDecimal("6000000")).stock(0).minStock(5)
                .status(ProductStatus.INACTIVE).supplier("Sony Vietnam").build(),

            Product.builder().sku("PRD-006").name("Chuột Logitech MX Master 3")
                .category(accessories).price(new BigDecimal("2200000"))
                .cost(new BigDecimal("1400000")).stock(35).minStock(10)
                .status(ProductStatus.ACTIVE).supplier("Logitech").build(),

            Product.builder().sku("PRD-007").name("SSD Samsung 970 EVO 1TB")
                .category(storage).price(new BigDecimal("3200000"))
                .cost(new BigDecimal("2400000")).stock(4).minStock(10)
                .status(ProductStatus.ACTIVE).supplier("Samsung").build(),

            Product.builder().sku("PRD-008").name("RAM Corsair 32GB DDR5")
                .category(components).price(new BigDecimal("4500000"))
                .cost(new BigDecimal("3200000")).stock(18).minStock(5)
                .status(ProductStatus.ACTIVE).supplier("Corsair").build()
        );
        productRepository.saveAll(products);
        log.info("Seeded {} products", products.size());
    }
}

