-- Create database
CREATE DATABASE IF NOT EXISTS rentops_ai;
USE rentops_ai;

-- Create tables
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    organization VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cars (
    id INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    specs TEXT,
    price_per_day DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    car_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (car_id) REFERENCES cars(id)
);

-- Insert admin user (username: admin, password: admin123)
-- Note: In a real application, you would use a proper hashing method
INSERT IGNORE INTO users (username, password_hash, role)
VALUES ('admin', 'hashed_password:salt', 'admin');

-- Insert sample cars
INSERT IGNORE INTO cars (make, model, year, license_plate, status, specs, price_per_day)
VALUES 
('Toyota', 'Corolla', 2022, 'ABC-123', 'available', 'Automatic, 4-door sedan, Fuel efficient', 50.00),
('Honda', 'Civic', 2023, 'XYZ-789', 'available', 'Manual, 4-door sedan, Sport mode', 55.00),
('Tesla', 'Model 3', 2023, 'EV-1234', 'available', 'Electric, Autopilot, Long range battery', 90.00),
('Ford', 'F-150', 2022, 'TRK-456', 'available', 'Pickup truck, 4WD, Crew cab', 80.00),
('BMW', 'X5', 2023, 'LUX-888', 'available', 'SUV, Leather interior, Premium sound', 120.00);