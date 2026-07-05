-- PetSpaBookingSystem — FINAL v4 (Fixed & Optimized)
-- Flyway migration (adapted): creates schema objects for pet_spa_booking
-- NOTE: Remove any CREATE DATABASE / USE statements when running under Flyway

SET FOREIGN_KEY_CHECKS = 0;

-- 1. USERS
CREATE TABLE IF NOT EXISTS users (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    full_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    phone            VARCHAR(20)  UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER','TECHNICIAN','RECEPTIONIST','ADMIN') NOT NULL,
    avatar_url       VARCHAR(500),
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted_at       DATETIME     NULL,
    loyalty_points   INT          NOT NULL DEFAULT 0,
    loyalty_tier ENUM('BRONZE','SILVER','GOLD','PLATINUM') NOT NULL DEFAULT 'BRONZE',
    notif_booking    BOOLEAN      NOT NULL DEFAULT TRUE,
    notif_voucher    BOOLEAN      NOT NULL DEFAULT TRUE,
    notif_pet_status BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_role  (role),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. REFRESH TOKENS
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     BIGINT        NOT NULL,
    token       VARCHAR(500)  NOT NULL UNIQUE,
    device_info VARCHAR(255)  NULL,
    expires_at  DATETIME      NOT NULL,
    revoked     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_rt_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. PETS
CREATE TABLE IF NOT EXISTS pets (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    owner_id        BIGINT        NOT NULL,
    name            VARCHAR(100)  NOT NULL,
    species ENUM('DOG','CAT','OTHER') NOT NULL,
    breed           VARCHAR(100),
    gender ENUM('MALE','FEMALE','UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
    date_of_birth   DATE          NULL,
    weight_kg       DECIMAL(5,2),
    allergies       TEXT,
    medical_history TEXT,
    special_notes   TEXT,
    avatar_url      VARCHAR(500),
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_pets_owner (owner_id),
    INDEX idx_pets_dob   (date_of_birth)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. SERVICE CATEGORIES
CREATE TABLE IF NOT EXISTS service_categories (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL UNIQUE,
    sort_order TINYINT      NOT NULL DEFAULT 0,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. SERVICES
CREATE TABLE IF NOT EXISTS services (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    category_id      BIGINT        NULL,
    name             VARCHAR(150)  NOT NULL,
    description      TEXT,
    base_price       DECIMAL(12,2) NOT NULL,
    duration_minutes SMALLINT      NOT NULL DEFAULT 60,
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_category FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE SET NULL,
    INDEX idx_service_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. TECHNICIAN SHIFTS
CREATE TABLE IF NOT EXISTS technician_shifts (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    technician_id BIGINT       NOT NULL,
    shift_date    DATE         NOT NULL,
    start_time    TIME         NOT NULL,
    end_time      TIME         NOT NULL,
    status ENUM('AVAILABLE','BUSY','DAY_OFF','REQUESTED_OFF') NOT NULL DEFAULT 'AVAILABLE',
    note       VARCHAR(255),
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_shift_tech FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_shift_tech_date (technician_id, shift_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. VOUCHERS
CREATE TABLE IF NOT EXISTS vouchers (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    code                VARCHAR(50)   NOT NULL UNIQUE,
    campaign_name       VARCHAR(150),
    discount_type ENUM('PERCENT','FIXED_AMOUNT') NOT NULL,
    discount_value      DECIMAL(12,2) NOT NULL,
    max_discount_amount DECIMAL(12,2) NULL,
    min_order_value     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    max_uses            INT           NOT NULL DEFAULT 1,
    used_count          INT           NOT NULL DEFAULT 0,
    valid_from          DATETIME      NOT NULL,
    valid_until         DATETIME      NOT NULL,
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_by          BIGINT        NOT NULL,
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_voucher_admin FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_voucher_code  (code),
    INDEX idx_voucher_valid (is_active, valid_from, valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. BOOKINGS
CREATE TABLE IF NOT EXISTS bookings (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    booking_code    VARCHAR(20)   NOT NULL UNIQUE,
    customer_id     BIGINT        NOT NULL,
    pet_id          BIGINT        NOT NULL,
    technician_id   BIGINT        NULL,
    receptionist_id BIGINT        NULL,
    scheduled_start DATETIME      NOT NULL,
    scheduled_end   DATETIME      NOT NULL,
    booking_status ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    service_stage ENUM('CHECKED_IN','BATHING','GROOMING','DRYING','COMPLETED') NULL,
    payment_status ENUM('UNPAID','PARTIALLY_PAID','FULLY_PAID') NOT NULL DEFAULT 'UNPAID',
    total_amount     DECIMAL(12,2) NOT NULL,
    paid_amount      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    remaining_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_amount - paid_amount) STORED,
    voucher_id      BIGINT        NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    special_requests TEXT,
    cancel_reason    VARCHAR(500),
    source ENUM('APP','COUNTER') NOT NULL DEFAULT 'APP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_booking_pet FOREIGN KEY (pet_id) REFERENCES pets(id),
    CONSTRAINT fk_booking_technician FOREIGN KEY (technician_id) REFERENCES users(id),
    CONSTRAINT fk_booking_receptionist FOREIGN KEY (receptionist_id) REFERENCES users(id),
    CONSTRAINT fk_booking_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    INDEX idx_booking_customer  (customer_id),
    INDEX idx_booking_tech_time (technician_id, scheduled_start),
    INDEX idx_booking_status    (booking_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. BOOKING SERVICES
CREATE TABLE IF NOT EXISTS booking_services (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    booking_id        BIGINT        NOT NULL,
    service_id        BIGINT        NOT NULL,
    price_snapshot    DECIMAL(12,2) NOT NULL,
    duration_snapshot SMALLINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_bs_service FOREIGN KEY (service_id) REFERENCES services(id),
    UNIQUE KEY uq_booking_service (booking_id, service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. BOOKING STATUS HISTORY
CREATE TABLE IF NOT EXISTS booking_status_history (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    booking_id BIGINT       NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50)  NOT NULL,
    changed_by BIGINT       NOT NULL,
    note       VARCHAR(255),
    changed_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_bsh_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_bsh_user    FOREIGN KEY (changed_by) REFERENCES users(id),
    INDEX idx_bsh_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    booking_id       BIGINT        NOT NULL,
    amount           DECIMAL(12,2) NOT NULL,
    payment_method ENUM('CASH','MOMO','VNPAY','BANK_TRANSFER') NOT NULL,
    payment_type ENUM('DEPOSIT_50','FULL_100','REMAINING') NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_id  VARCHAR(100),
    order_id        VARCHAR(100),
    idempotency_key VARCHAR(100) UNIQUE,
    gateway_response JSON,
    paid_at    DATETIME      NULL,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_payment_booking     (booking_id),
    INDEX idx_payment_transaction (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. BOOKING PHOTOS
CREATE TABLE IF NOT EXISTS booking_photos (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    booking_id  BIGINT       NOT NULL,
    photo_url   VARCHAR(500) NOT NULL,
    phase ENUM('BEFORE','AFTER') NOT NULL,
    uploaded_by BIGINT   NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_photo_booking  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_photo_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. TECHNICIAN NOTES
CREATE TABLE IF NOT EXISTS technician_notes (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    booking_id    BIGINT   NOT NULL,
    technician_id BIGINT   NOT NULL,
    note          TEXT     NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_tn_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_tn_tech    FOREIGN KEY (technician_id) REFERENCES users(id),
    INDEX idx_tn_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. REVIEWS
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    booking_id  BIGINT   NOT NULL UNIQUE,
    customer_id BIGINT   NOT NULL,
    rating      TINYINT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_review_booking  FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    INDEX idx_review_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. CUSTOMER VOUCHERS
CREATE TABLE IF NOT EXISTS customer_vouchers (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    customer_id BIGINT   NOT NULL,
    voucher_id  BIGINT   NOT NULL,
    used_at     DATETIME NULL,
    booking_id  BIGINT   NULL,
    source ENUM('PROMO','LOYALTY','BIRTHDAY','CAMPAIGN') NOT NULL DEFAULT 'PROMO',
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_cv_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_cv_voucher  FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    CONSTRAINT fk_cv_booking  FOREIGN KEY (booking_id) REFERENCES bookings(id),
    UNIQUE KEY uq_customer_voucher (customer_id, voucher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. LOYALTY POINTS LOG
CREATE TABLE IF NOT EXISTS loyalty_points_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    customer_id BIGINT       NOT NULL,
    delta       INT          NOT NULL,
    reason ENUM('BOOKING_COMPLETE','VOUCHER_REDEEM','BIRTHDAY_BONUS','ADJUSTMENT') NOT NULL,
    booking_id  BIGINT       NULL,
    description VARCHAR(255),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_lpl_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_lpl_booking  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    INDEX idx_lpl_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    user_id BIGINT       NOT NULL,
    title   VARCHAR(200) NOT NULL,
    content TEXT         NOT NULL,
    type ENUM('BOOKING','PAYMENT','VOUCHER','PET_STATUS','SYSTEM') NOT NULL,
    is_read    BOOLEAN  NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. SYSTEM CONFIG
CREATE TABLE IF NOT EXISTS system_config (
    config_key   VARCHAR(100) NOT NULL,
    config_value TEXT         NOT NULL,
    description  VARCHAR(255),
    updated_by   BIGINT       NULL,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (config_key),
    CONSTRAINT fk_config_admin FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO system_config (config_key, config_value, description) VALUES
('BUSINESS_OPEN_TIME',  '08:00', 'Giờ mở cửa (HH:mm)'),
('BUSINESS_CLOSE_TIME', '20:00', 'Giờ đóng cửa (HH:mm)'),
('DAILY_SLOT_LIMIT',    '20',    'Số booking tối đa mỗi ngày'),
('CANCEL_HOURS_BEFORE', '24',    'Số giờ tối thiểu trước khi hủy lịch'),
('DEPOSIT_PERCENT',     '50',    'Phần trăm đặt cọc mặc định'),
('MOMO_PARTNER_CODE',   '',      'MoMo API Partner Code'),
('MOMO_SECRET_KEY',     '',      'MoMo API Secret Key'),
('VNPAY_TMN_CODE',      '',      'VNPay Terminal Code'),
('VNPAY_HASH_SECRET',   '',      'VNPay API Hash Secret');

SET FOREIGN_KEY_CHECKS = 1;

