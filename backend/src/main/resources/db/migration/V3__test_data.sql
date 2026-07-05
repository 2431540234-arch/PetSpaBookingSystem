-- Test data for comprehensive testing
-- Sample pets
INSERT INTO pets (id, name, species, breed, gender, age, weight, allergies, medical_history, notes, emoji, customer_id) VALUES
('pet-001-cust-tran', 'Mimi', 'cat', 'Persian', 'female', 3, 4.5, NULL, 'None', 'Very fluffy', '🐱', '8b47dc25-fd0d-413b-a373-d3a97c4c1557'),
('pet-002-cust-tran', 'Max', 'dog', 'Golden Retriever', 'male', 5, 32.0, 'Chicken', 'Vaccinated', 'Friendly and playful', '🐕', '8b47dc25-fd0d-413b-a373-d3a97c4c1557'),
('pet-003-cust-tran', 'Luna', 'dog', 'Poodle', 'female', 2, 28.5, NULL, 'None', 'Energetic', '🐩', '8b47dc25-fd0d-413b-a373-d3a97c4c1557');

-- Sample bookings (past)
INSERT INTO bookings (id, pet_id, service_id, staff_id, customer_id, date, time, notes, status, payment_status, total_amount, paid_amount, payment_method, transaction_id, created_at, service_duration, customer_requests) VALUES
('booking-001', 'pet-001-cust-tran', '5c3bfc61-5870-42d3-bdf0-768d1512a7fc', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', '8b47dc25-fd0d-413b-a373-d3a97c4c1557', '2026-06-15', '09:00', 'First grooming session', 'completed', 'paid', 150000, 150000, 'cash', 'txn-20260615-001', NOW(), 45, NULL),
('booking-002', 'pet-002-cust-tran', '61e5708c-429f-40e8-984c-d06a90a5715a', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', '8b47dc25-fd0d-413b-a373-d3a97c4c1557', '2026-06-18', '14:00', 'Full grooming with nail trim', 'completed', 'paid', 250000, 250000, 'card', 'txn-20260618-002', NOW(), 60, 'Trim ear hair');

-- Sample bookings (pending)
INSERT INTO bookings (id, pet_id, service_id, staff_id, customer_id, date, time, notes, status, payment_status, total_amount, paid_amount, created_at, service_duration) VALUES
('booking-003', 'pet-003-cust-tran', '172aad5c-36e2-477a-9db9-65d881c3b4df', NULL, '8b47dc25-fd0d-413b-a373-d3a97c4c1557', '2026-07-05', '10:00', 'Health checkup', 'pending', 'unpaid', 300000, 0, NOW(), 30);

-- Sample shifts (approved)
INSERT INTO shifts (id, staff_id, date, type, start_time, end_time, status) VALUES
('shift-001', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', '2026-07-01', 'morning', '08:00', '12:00', 'approved'),
('shift-002', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', '2026-07-01', 'afternoon', '14:00', '18:00', 'approved'),
('shift-003', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', '2026-07-02', 'morning', '08:00', '12:00', 'approved');

-- Sample staff request
INSERT INTO staff_requests (id, staff_id, type, date, reason, status, created_at) VALUES
('staffreq-001', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', 'leave', '2026-07-10', 'Personal appointment', 'pending', NOW()),
('staffreq-002', 'e04c29e0-fa5d-4843-a1aa-a23449cd814f', 'shift_change', '2026-07-15', 'Need afternoon shift instead of morning', 'approved', NOW());

-- Sample notifications
INSERT INTO notifications (id, user_id, type, title, message, time, is_read, related_id) VALUES
('notif-001', '8b47dc25-fd0d-413b-a373-d3a97c4c1557', 'booking_completed', 'Dịch vụ hoàn thành', 'Dịch vụ tắm cho Mimi đã hoàn thành', NOW(), false, 'booking-001'),
('notif-002', '8b47dc25-fd0d-413b-a373-d3a97c4c1557', 'payment_success', 'Thanh toán thành công', 'Thanh toán cho đặt lịch đã chuyển vào tài khoản', NOW(), false, 'booking-002'),
('notif-003', '8b47dc25-fd0d-413b-a373-d3a97c4c1557', 'booking_confirmed', 'Lịch được xác nhận', 'Lịch khám sức khỏe cho Luna vào 2026-07-05 10:00 đã được xác nhận', NOW(), true, 'booking-003');

-- Ensure notification settings exist for users
INSERT INTO notif_settings (user_id, booking_new, booking_confirmed, booking_rescheduled, booking_cancelled, payment_success, payment_failed, service_in_progress, service_completed, channel_push, channel_email) VALUES
('8b47dc25-fd0d-413b-a373-d3a97c4c1557', true, true, true, true, true, true, true, true, true, false) ON DUPLICATE KEY UPDATE user_id=user_id;

