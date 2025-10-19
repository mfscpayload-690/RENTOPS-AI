-- RENTOPS-AI Database Setup Commands
-- Run these commands in MySQL command line

-- Create database
CREATE DATABASE IF NOT EXISTS rentops_ai;

-- Create user with credentials matching your config
CREATE USER IF NOT EXISTS 'asus'@'localhost' IDENTIFIED BY '3005';

-- Grant privileges
GRANT ALL PRIVILEGES ON rentops_ai.* TO 'asus'@'localhost';

-- Refresh privileges
FLUSH PRIVILEGES;

-- Show databases to verify
SHOW DATABASES;

-- Use the database
USE rentops_ai;

-- Show that we're using the right database
SELECT DATABASE();