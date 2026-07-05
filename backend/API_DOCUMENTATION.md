# PetSpa Backend - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Hầu hết các endpoint yêu cầu Bearer token JWT. Thêm token vào header:
```
Authorization: Bearer <your_jwt_token>
```

## Public Endpoints

### Authentication
- **POST** `/auth/login` - Đăng nhập
  ```json
  {
    "email": "user@example.com",
    "password": "password"
  }
  ```
  Response:
  ```json
  {
    "token": "eyJhbGc...",
    "user": {
      "id": "user-id",
      "name": "User Name",
      "email": "user@example.com",
      "role": "CUSTOMER"
    }
  }
  ```

### Services (Public)
- **GET** `/services` - Lấy danh sách tất cả dịch vụ (lọc theo category)
  ```
  GET /services?category=Grooming
  ```

- **GET** `/services/{serviceId}` - Chi tiết dịch vụ

### Staff (Public)
- **GET** `/staff/{staffId}` - Xem hồ sơ nhân viên

- **GET** `/staff/available?date=2026-06-23` - Lấy danh sách nhân viên có sẵn vào ngày nhất định

---

## Protected Endpoints

### Pets (Authenticated)
- **POST** `/pets` - Tạo thú cưng mới
  ```json
  {
    "name": "Mimi",
    "species": "cat",
    "breed": "Persian",
    "gender": "female",
    "age": 3,
    "weight": 4.5,
    "allergies": "Chicken",
    "medicalHistory": "None",
    "notes": "Very friendly",
    "emoji": "🐱"
  }
  ```

- **GET** `/pets` - Lấy danh sách thú cưng của tôi

- **GET** `/pets/{petId}` - Chi tiết thú cưng

- **PUT** `/pets/{petId}` - Cập nhật thú cưng

- **DELETE** `/pets/{petId}` - Xóa thú cưng

### Bookings (Authenticated)
- **POST** `/bookings` - Tạo đặt lịch mới
  ```json
  {
    "petId": "pet-id",
    "serviceId": "service-id",
    "staffId": "staff-id",
    "date": "2026-07-01",
    "time": "10:00",
    "notes": "Additional notes",
    "customerRequests": "Special care needed",
    "serviceDuration": 60
  }
  ```

- **GET** `/bookings` - Lấy danh sách đặt lịch của tôi
  ```
  GET /bookings
  GET /bookings?staffId=staff-id
  GET /bookings?date=2026-07-01
  ```

- **GET** `/bookings/{bookingId}` - Chi tiết đặt lịch

- **PUT** `/bookings/{bookingId}` - Cập nhật đặt lịch

- **PUT** `/bookings/{bookingId}/status` - Cập nhật trạng thái đặt lịch
  ```json
  {
    "status": "confirmed"
  }
  ```
  Status có thể là: pending, confirmed, in_progress, completed, cancelled

- **PUT** `/bookings/{bookingId}/payment` - Cập nhật thanh toán
  ```json
  {
    "paymentStatus": "paid",
    "paidAmount": 150000,
    "transactionId": "txn-123"
  }
  ```

- **DELETE** `/bookings/{bookingId}` - Hủy đặt lịch

### Staff Profile (Authenticated)
- **GET** `/staff/me` - Xem hồ sơ của tôi (chỉ dành cho staff)

- **PUT** `/staff/me` - Cập nhật hồ sơ của tôi
  ```json
  {
    "specialty": "Grooming",
    "position": "Senior Groomer",
    "avatar": "url-to-avatar",
    "expertise": "Grooming,Spa",
    "joinDate": "2023-01-01",
    "status": "active"
  }
  ```

### Shifts (Authenticated - Staff only)
- **POST** `/shifts` - Tạo shift mới
  ```json
  {
    "date": "2026-07-01",
    "type": "morning",
    "startTime": "08:00",
    "endTime": "12:00"
  }
  ```

- **GET** `/shifts` - Lấy danh sách shift của tôi
  ```
  GET /shifts
  GET /shifts?date=2026-07-01
  ```

- **GET** `/shifts/{shiftId}` - Chi tiết shift

- **PUT** `/shifts/{shiftId}/status` - Cập nhật trạng thái shift
  ```json
  {
    "status": "approved"
  }
  ```

- **DELETE** `/shifts/{shiftId}` - Xóa shift

### Staff Requests (Authenticated - Staff only)
- **POST** `/staff-requests` - Tạo yêu cầu (nghỉ phép, đổi ca, v.v.)
  ```json
  {
    "type": "leave",
    "date": "2026-07-05",
    "reason": "Personal reason"
  }
  ```

- **GET** `/staff-requests/my-requests` - Lấy danh sách yêu cầu của tôi

- **GET** `/staff-requests/pending` - Lấy danh sách yêu cầu chưa xử lý (chỉ Owner/Admin)

- **GET** `/staff-requests/{requestId}` - Chi tiết yêu cầu

- **PUT** `/staff-requests/{requestId}/approve` - Phê duyệt yêu cầu

- **PUT** `/staff-requests/{requestId}/reject` - Từ chối yêu cầu

- **DELETE** `/staff-requests/{requestId}` - Xóa yêu cầu

### Notifications (Authenticated)
- **GET** `/notifications` - Lấy danh sách thông báo
  ```
  GET /notifications
  GET /notifications?unreadOnly=true
  ```

- **PUT** `/notifications/{notificationId}/read` - Đánh dấu thông báo đã đọc

- **DELETE** `/notifications/{notificationId}` - Xóa thông báo

---

## Error Responses

Tất cả lỗi được trả về dưới định dạng:
```json
{
  "timestamp": "2026-06-23T10:30:00",
  "status": 400,
  "message": "Error message"
}
```

### HTTP Status Codes
- **200** - Success
- **400** - Bad Request (validation error, invalid booking, etc.)
- **401** - Unauthorized (invalid token)
- **403** - Forbidden (insufficient permissions)
- **404** - Not Found
- **409** - Conflict (staff not available, etc.)
- **500** - Internal Server Error

---

## Example Usage

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@petspa.com","password":"123456"}'
```

### Get Services
```bash
curl -X GET http://localhost:8080/api/services
```

### Create Pet (with token)
```bash
curl -X POST http://localhost:8080/api/pets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"Mimi","species":"cat"}'
```

### Create Booking (with token)
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "petId":"pet-123",
    "serviceId":"service-456",
    "date":"2026-07-01",
    "time":"10:00"
  }'
```

---

## Test Accounts

| Email | Password | Role |
|-------|----------|------|
| owner@petspa.com | 123456 | OWNER |
| staff@petspa.com | 123456 | STAFF |
| customer@petspa.com | 123456 | CUSTOMER |

---

## Notes
- Tất cả dates/times sử dụng format: `YYYY-MM-DD` và `HH:mm`
- Prices tính bằng VND (đơn vị nhỏ nhất, ví dụ: 150000 = 150.000 VND)
- JWT token hết hạn sau 24 giờ

