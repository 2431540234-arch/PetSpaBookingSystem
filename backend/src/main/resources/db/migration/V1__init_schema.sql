CREATE TABLE users (
    id            VARCHAR(36) PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    gender        VARCHAR(20),
    dob           VARCHAR(20),
    address       VARCHAR(255),
    role          VARCHAR(20) NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff_profiles (
    user_id    VARCHAR(36) PRIMARY KEY,
    specialty  VARCHAR(255),
    position   VARCHAR(100),
    avatar     VARCHAR(500),
    expertise  VARCHAR(500),
    join_date  VARCHAR(20),
    status     VARCHAR(20) DEFAULT 'active',
    CONSTRAINT fk_staff_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE pets (
    id               VARCHAR(36) PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    species          VARCHAR(100),
    breed            VARCHAR(100),
    gender           VARCHAR(20),
    age              INT DEFAULT 0,
    weight           DOUBLE DEFAULT 0,
    allergies        VARCHAR(500),
    medical_history  VARCHAR(1000),
    notes            VARCHAR(500),
    emoji            VARCHAR(10) DEFAULT '🐾',
    customer_id      VARCHAR(36) NOT NULL,
    CONSTRAINT fk_pets_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE services (
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    category    VARCHAR(100),
    price       BIGINT NOT NULL,
    duration    INT NOT NULL,
    emoji       VARCHAR(10) DEFAULT '✨',
    description VARCHAR(1000),
    status      VARCHAR(20) DEFAULT 'active',
    image_url   VARCHAR(500)
);

CREATE TABLE bookings (
    id                 VARCHAR(36) PRIMARY KEY,
    pet_id             VARCHAR(36) NOT NULL,
    service_id         VARCHAR(36) NOT NULL,
    staff_id           VARCHAR(36),
    customer_id        VARCHAR(36) NOT NULL,
    date               VARCHAR(20) NOT NULL,
    time               VARCHAR(20) NOT NULL,
    notes              VARCHAR(500),
    status             VARCHAR(20) DEFAULT 'pending',
    payment_status     VARCHAR(20) DEFAULT 'unpaid',
    total_amount       BIGINT DEFAULT 0,
    paid_amount        BIGINT DEFAULT 0,
    payment_method     VARCHAR(50),
    transaction_id     VARCHAR(100),
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    service_duration   INT DEFAULT 60,
    customer_requests  VARCHAR(500),
    CONSTRAINT fk_bookings_pet      FOREIGN KEY (pet_id) REFERENCES pets(id),
    CONSTRAINT fk_bookings_service  FOREIGN KEY (service_id) REFERENCES services(id),
    CONSTRAINT fk_bookings_staff    FOREIGN KEY (staff_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE shifts (
    id         VARCHAR(36) PRIMARY KEY,
    staff_id   VARCHAR(36) NOT NULL,
    date       VARCHAR(20) NOT NULL,
    type       VARCHAR(50),
    start_time VARCHAR(20),
    end_time   VARCHAR(20),
    status     VARCHAR(20) DEFAULT 'pending',
    CONSTRAINT fk_shifts_staff FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE staff_requests (
    id         VARCHAR(36) PRIMARY KEY,
    staff_id   VARCHAR(36) NOT NULL,
    type       VARCHAR(50),
    date       VARCHAR(20),
    reason     VARCHAR(500),
    status     VARCHAR(20) DEFAULT 'pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_requests_staff FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    id         VARCHAR(36) PRIMARY KEY,
    user_id    VARCHAR(36) NOT NULL,
    type       VARCHAR(50),
    title      VARCHAR(255),
    message    VARCHAR(1000),
    time       VARCHAR(50),
    is_read    BOOLEAN DEFAULT FALSE,
    related_id VARCHAR(36),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE notif_settings (
    user_id              VARCHAR(36) PRIMARY KEY,
    booking_new          BOOLEAN DEFAULT TRUE,
    booking_confirmed    BOOLEAN DEFAULT TRUE,
    booking_rescheduled  BOOLEAN DEFAULT TRUE,
    booking_cancelled    BOOLEAN DEFAULT TRUE,
    payment_success      BOOLEAN DEFAULT TRUE,
    payment_failed       BOOLEAN DEFAULT TRUE,
    service_in_progress  BOOLEAN DEFAULT TRUE,
    service_completed    BOOLEAN DEFAULT TRUE,
    channel_push         BOOLEAN DEFAULT TRUE,
    channel_email        BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_notif_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
