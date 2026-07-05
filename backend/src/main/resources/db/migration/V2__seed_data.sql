-- Mat khau cho ca 3 tai khoan mau deu la: 123456
-- Hash duoc sinh thuc te bang BCrypt (rounds=10), da kiem tra khop voi mat khau goc.

INSERT INTO users (id, name, email, phone, password_hash, gender, dob, address, role, created_at) VALUES
('c1f8d965-29fc-457c-99e6-46e2971740f1', 'Chu tiem PetSpa', '[email protected]', '0900000001', '$2b$10$rVsFFjXh8BYNMPUHa309yeKjSpW8gCI3n9YbKYvblx7mjcB3zLezC', 'female', '1990-01-01', 'TP.HCM', 'OWNER', NOW()),
('e04c29e0-fa5d-4843-a1aa-a23449cd814f', 'Nguyen Van Nhan Vien', '[email protected]', '0900000002', '$2b$10$rVsFFjXh8BYNMPUHa309yeKjSpW8gCI3n9YbKYvblx7mjcB3zLezC', 'male', '1995-05-05', 'TP.HCM', 'STAFF', NOW()),
('8b47dc25-fd0d-413b-a373-d3a97c4c1557', 'Tran Thi Khach Hang', '[email protected]', '0900000003', '$2b$10$rVsFFjXh8BYNMPUHa309yeKjSpW8gCI3n9YbKYvblx7mjcB3zLezC', 'female', '1998-08-08', 'TP.HCM', 'CUSTOMER', NOW());

INSERT INTO staff_profiles (user_id, specialty, position, avatar, expertise, join_date, status) VALUES
('e04c29e0-fa5d-4843-a1aa-a23449cd814f', 'Tam & cat tia long', 'Ky thuat vien', '', 'Grooming,Spa', '2023-01-01', 'active');

INSERT INTO services (id, name, category, price, duration, emoji, description, status, image_url) VALUES
('5c3bfc61-5870-42d3-bdf0-768d1512a7fc', 'Tam co ban', 'Grooming', 150000, 45, '🛁', 'Tam va say kho cho thu cung', 'active', ''),
('61e5708c-429f-40e8-984c-d06a90a5715a', 'Cat tia long', 'Grooming', 250000, 60, '✂️', 'Cat tia long theo yeu cau', 'active', ''),
('172aad5c-36e2-477a-9db9-65d881c3b4df', 'Kham tong quat', 'Health', 300000, 30, '🩺', 'Kham suc khoe tong quat', 'active', '');
