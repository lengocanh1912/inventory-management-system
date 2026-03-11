# IMS Backend — Spring Boot REST API

## 🚀 Yêu cầu
- Java 17+
- Maven 3.8+
- MySQL 8.0+

---

## ⚙️ Cấu hình

### 1. Tạo database MySQL
```sql
CREATE DATABASE ims_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Sửa `application.properties`
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Chạy ứng dụng
```bash
mvn spring-boot:run
```

Server khởi động tại: `http://localhost:8080/api`

> Lần đầu chạy sẽ tự động seed dữ liệu mẫu (users, categories, products)

---

## 🔑 Tài khoản mặc định

| Role  | Email                | Password   |
|-------|----------------------|------------|
| ADMIN | admin@store.com      | admin123   |
| STAFF | staff@store.com      | staff123   |

---

## 📁 Cấu trúc project

```
src/main/java/com/ims/
├── ImsApplication.java
│
├── config/
│   ├── SecurityConfig.java        # Spring Security + JWT + CORS
│   └── DataSeeder.java            # Seed dữ liệu mẫu
│
├── controller/
│   ├── AuthController.java        # /auth/**
│   ├── ProductController.java     # /products/**
│   ├── InventoryController.java   # /inventory/**
│   ├── OrderController.java       # /orders/**
│   ├── UserController.java        # /users/** (ADMIN)
│   └── DashboardController.java   # /dashboard/**
│
├── service/impl/
│   ├── AuthService.java
│   ├── UserService.java           # + implements UserDetailsService
│   ├── ProductService.java
│   ├── InventoryService.java
│   ├── OrderService.java
│   └── DashboardService.java
│
├── entity/
│   ├── BaseEntity.java            # createdAt, updatedAt
│   ├── User.java                  # implements UserDetails
│   ├── RefreshToken.java
│   ├── Category.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── InventoryMovement.java
│
├── repository/
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   ├── CategoryRepository.java
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   └── InventoryMovementRepository.java
│
├── dto/
│   ├── request/
│   │   ├── AuthRequest.java       # Login, Register, RefreshToken
│   │   ├── ProductRequest.java
│   │   ├── ProductUpdateRequest.java
│   │   ├── OrderRequest.java
│   │   ├── OrderUpdateStatusRequest.java
│   │   ├── UserRequest.java
│   │   ├── UserUpdateRequest.java
│   │   ├── UserUpdateRoleRequest.java
│   │   └── InventoryAdjustRequest.java
│   └── response/
│       ├── ApiResponse.java       # Generic wrapper
│       ├── PageResponse.java      # Pagination wrapper
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── CategoryResponse.java
│       ├── ProductResponse.java
│       ├── OrderResponse.java
│       ├── OrderItemResponse.java
│       ├── MovementResponse.java
│       ├── DashboardStatsResponse.java
│       └── SalesChartResponse.java
│
├── security/
│   ├── jwt/
│   │   ├── JwtService.java        # Generate + validate JWT
│   │   └── JwtProperties.java     # Bind jwt.* from properties
│   └── filter/
│       └── JwtAuthenticationFilter.java
│
├── enums/
│   ├── Role.java                  # ADMIN, STAFF
│   ├── UserStatus.java            # ACTIVE, INACTIVE
│   ├── ProductStatus.java         # ACTIVE, INACTIVE
│   ├── OrderStatus.java           # PENDING, PROCESSING, COMPLETED, CANCELLED
│   └── MovementType.java          # IN, OUT, ADJUSTMENT
│
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    └── DuplicateResourceException.java
```

---

## 🛣️ API Endpoints

### Auth
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| POST | `/auth/login` | ❌ | Đăng nhập |
| POST | `/auth/register` | ❌ | Đăng ký |
| POST | `/auth/refresh` | ❌ | Làm mới token |
| POST | `/auth/logout` | ✅ | Đăng xuất |
| GET | `/auth/me` | ✅ | Thông tin user hiện tại |

### Products
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/products` | ✅ | Danh sách (filter, phân trang) |
| GET | `/products/:id` | ✅ | Chi tiết sản phẩm |
| GET | `/products/categories` | ✅ | Danh sách danh mục |
| GET | `/products/low-stock` | ✅ | Sản phẩm sắp hết |
| POST | `/products` | ADMIN | Tạo sản phẩm |
| PUT | `/products/:id` | ✅ | Cập nhật sản phẩm |
| DELETE | `/products/:id` | ADMIN | Xóa sản phẩm |

### Inventory
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/inventory` | ✅ | Danh sách tồn kho |
| GET | `/inventory/low-stock` | ✅ | Hàng sắp hết |
| POST | `/inventory/:id/adjust` | ✅ | Nhập/xuất kho |
| GET | `/inventory/movements` | ✅ | Lịch sử nhập xuất |

### Orders
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/orders` | ✅ | Danh sách đơn hàng |
| GET | `/orders/:id` | ✅ | Chi tiết đơn hàng |
| POST | `/orders` | ✅ | Tạo đơn hàng |
| PATCH | `/orders/:id/status` | ✅ | Cập nhật trạng thái |

### Users (ADMIN only)
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/users` | ADMIN | Danh sách nhân viên |
| GET | `/users/:id` | ADMIN | Chi tiết nhân viên |
| POST | `/users` | ADMIN | Tạo nhân viên |
| PUT | `/users/:id` | ADMIN | Cập nhật nhân viên |
| PATCH | `/users/:id/role` | ADMIN | Đổi vai trò |
| DELETE | `/users/:id` | ADMIN | Xóa nhân viên |

### Dashboard
| Method | URL | Auth | Mô tả |
|--------|-----|------|-------|
| GET | `/dashboard/stats` | ✅ | Thống kê tổng quan |
| GET | `/dashboard/sales?period=7d` | ✅ | Biểu đồ doanh thu |
| GET | `/dashboard/top-products` | ✅ | Top sản phẩm |
| GET | `/dashboard/activity` | ✅ | Hoạt động gần đây |

---

## 📦 Response Format

### Thành công
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": { ... },
  "timestamp": "2024-12-07 10:30:00"
}
```

### Có phân trang
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "last": false
  }
}
```

### Lỗi
```json
{
  "success": false,
  "message": "Sản phẩm với ID 99 không tồn tại",
  "timestamp": "2024-12-07 10:30:00"
}
```

---

## 🔐 Xác thực với JWT

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Flow
1. POST `/auth/login` → nhận `accessToken` + `refreshToken`
2. Gửi `accessToken` trong header mỗi request
3. Khi hết hạn → POST `/auth/refresh` với `refreshToken`
4. Logout → POST `/auth/logout` (xóa refreshToken)

---

## 🗄️ Database Schema (auto-created)

Tables: `users`, `refresh_tokens`, `categories`, `products`,
`orders`, `order_items`, `inventory_movements`
