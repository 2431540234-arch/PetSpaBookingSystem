# PetSpa Backend

Backend Spring Boot cho ứng dụng Android PetSpa — thay thế `MockApiService`.

## Yêu cầu

- Java 17+
- Maven 3.8+
- MySQL 8.x đang chạy ở `localhost:3306`

## Chạy thử

1. Tạo database:

```sql
CREATE DATABASE petspa CHARACTER SET utf8mb4;
```

2. Sửa `src/main/resources/application.yml`: đổi `username`/`password` MySQL cho đúng máy bạn.

3. Chạy:

```bash
mvn spring-boot:run
```

Flyway sẽ tự tạo 9 bảng và chèn dữ liệu mẫu (3 tài khoản: owner / staff / customer, mật khẩu đều là `123456`).

4. Test đăng nhập:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"[email protected]\",\"password\":\"123456\"}"
```

Sẽ nhận về `{ "token": "...", "user": {...} }` — đúng shape mà `AuthResponse` và `TokenDataStore` phía Android đang mong đợi.

## Trạng thái hiện tại

### ✅ Đã implement (Vertical Slice 1 + 2):
- **Auth**: Login, JWT token, Spring Security
- **Database**: 9 bảng đầy đủ với schema hoàn chỉnh
- **Pets Management**: CRUD pets, filter by customer
- **Services**: List services, filter by category (public endpoint)
- **Bookings**: CRUD, status tracking, payment management
- **Staff Management**: Update profile, view availability
- **Shifts**: Create/update shifts, manage status
- **Staff Requests**: Leave/change shift requests with approval workflow
- **Notifications**: Real-time notifications, read/unread tracking

### Entities:
- ✅ User (base entity for all 3 roles)
- ✅ Pet
- ✅ ServiceCatalog
- ✅ Booking
- ✅ StaffProfile
- ✅ Shift
- ✅ StaffRequest
- ✅ Notification
- ✅ NotificationSetting

### API Endpoints:
- ✅ POST `/auth/login` - Login
- ✅ GET `/services` - List services
- ✅ GET `/services/{id}` - Service detail
- ✅ GET `/staff/{id}` - Staff profile
- ✅ GET `/staff/available?date=...` - Available staff on date
- ✅ POST/GET/PUT/DELETE `/pets` - Pet management
- ✅ POST/GET/PUT/DELETE `/bookings` - Booking management
- ✅ PUT `/bookings/{id}/status` - Update booking status
- ✅ PUT `/bookings/{id}/payment` - Payment tracking
- ✅ POST/GET/PUT/DELETE `/shifts` - Shift management
- ✅ POST/GET/PUT `/staff-requests` - Staff requests
- ✅ GET/PUT/DELETE `/notifications` - Notifications

## API Documentation

Xem tệp `API_DOCUMENTATION.md` để biết chi tiết tất cả các endpoints, request/response format, và ví dụ sử dụng.

## Architecture

```
controller/      - REST endpoints
  ├── AuthController
  ├── PetController
  ├── ServiceController
  ├── BookingController
  ├── StaffController
  ├── ShiftController
  ├── StaffRequestController
  └── NotificationController

service/         - Business logic
  ├── AuthService
  ├── PetService
  ├── ServiceCatalogService
  ├── BookingService
  ├── StaffService
  ├── ShiftService
  ├── StaffRequestService
  └── NotificationService

repository/      - Database queries
  ├── UserRepository
  ├── PetRepository
  ├── ServiceCatalogRepository
  ├── BookingRepository
  ├── StaffProfileRepository
  ├── ShiftRepository
  ├── StaffRequestRepository
  ├── NotificationRepository
  └── NotificationSettingRepository

entity/          - JPA entities
  ├── User
  ├── Pet
  ├── ServiceCatalog
  ├── Booking
  ├── StaffProfile
  ├── Shift
  ├── StaffRequest
  ├── Notification
  └── NotificationSetting

dto/             - Data transfer objects
  ├── request/
  └── response/

exception/       - Exception handling
  ├── GlobalExceptionHandler
  ├── ResourceNotFoundException
  ├── InvalidBookingException
  ├── StaffNotAvailableException
  └── UnauthorizedException

security/        - JWT & authentication
  ├── SecurityConfig
  ├── JwtTokenProvider
  ├── JwtAuthFilter
  └── CustomUserDetailsService
```

## Database Schema

9 bảng chính:
1. **users** - Tất cả người dùng (CUSTOMER, STAFF, OWNER)
2. **pets** - Thú cưng của khách hàng
3. **services** - Danh mục dịch vụ
4. **bookings** - Đặt lịch dịch vụ
5. **staff_profiles** - Hồ sơ nhân viên
6. **shifts** - Ca trực của nhân viên
7. **staff_requests** - Yêu cầu nghỉ/đổi ca
8. **notifications** - Thông báo cho người dùng
9. **notif_settings** - Cấu hình thông báo

Migrations được quản lý bởi Flyway:
- `V1__init_schema.sql` - Schema khởi tạo
- `V2__seed_data.sql` - Dữ liệu mẫu

## Security

- JWT token-based authentication (24 hours expiration)
- BCrypt password hashing
- Role-based access control (OWNER, STAFF, CUSTOMER)
- Stateless session (phù hợp với mobile app)
- Public endpoints cho services và staff profiles

## Tương tác với Android App

### TokenDataStore (App)
Backend trả về token trong response login, app lưu vào `TokenDataStore`.

### AuthResponse shape
```kotlin
{
  "token": "eyJhbGc...",
  "user": {
    "id": "user-id",
    "name": "User Name",
    "email": "user@email.com",
    "phone": "0901234567",
    "gender": "male",
    "dob": "1990-01-01",
    "address": "TP.HCM",
    "role": "CUSTOMER",
    "createdAt": "2026-06-23T10:30:00"
  }
}
```

### Các API được sử dụng từ App

**Pets Screen:**
- GET `/api/pets` - Get my pets
- POST `/api/pets` - Create pet
- PUT `/api/pets/{id}` - Update pet

**Booking Flow:**
- GET `/api/services` - List services
- GET `/api/staff/available?date=...` - Find available staff
- POST `/api/bookings` - Create booking
- GET `/api/bookings` - Get my bookings
- PUT `/api/bookings/{id}/status` - Update booking status

**Notifications:**
- GET `/api/notifications` - Get my notifications
- PUT `/api/notifications/{id}/read` - Mark as read

**Staff Features:**
- GET `/api/staff/me` - Get my profile
- POST `/api/shifts` - Create shift
- GET `/api/shifts` - My shifts
- POST `/api/staff-requests` - Request leave/change

## Performance Tips

- Services endpoint là public → không cần cache bây giờ nhưng có thể thêm khi cần
- Bookings filter được index bằng customer_id, staff_id, date
- Notification có separate unread query

## Future Enhancements

- [ ] Thêm pagination cho list endpoints
- [ ] Advanced search/filter (price range, duration, etc.)
- [ ] Staff availability algorithm (tính toán time slots trống)
- [ ] Payment gateway integration (Stripe, VnPay, etc.)
- [ ] Email notifications
- [ ] Analytics/Reports
- [ ] WebSocket untuk real-time notifications
- [ ] File upload (avatar, service images)
