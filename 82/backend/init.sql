-- Database initialization script for Care Scheduling System
-- This script creates the users table with the new structure

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS logindemo;
USE logindemo;

-- Drop existing users table if it exists
DROP TABLE IF EXISTS users;

-- Create users table with new structure
CREATE TABLE users (
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('poa', 'worker', 'admin') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data for testing
INSERT INTO users (first_name, last_name, email, password, role) VALUES
('Admin', 'User', 'admin@example.com', 'admin123', 'admin'),
('John', 'Doe', 'john.doe@example.com', 'password123', 'worker'),
('Jane', 'Smith', 'jane.smith@example.com', 'password123', 'poa');

-- Create indexes for better performance
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_role ON users(role);
