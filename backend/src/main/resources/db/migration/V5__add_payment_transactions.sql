CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    payment_gateway VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(100),
    request_id VARCHAR(100) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'VND',
    payment_status VARCHAR(20) NOT NULL,
    signature TEXT,
    pay_url TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    paid_at DATETIME,
    UNIQUE KEY uk_request_id (request_id),
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_pt_request_id ON payment_transactions(request_id);
CREATE INDEX idx_pt_booking_id ON payment_transactions(booking_id);
