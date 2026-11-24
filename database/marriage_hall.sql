CREATE DATABASE IF NOT EXISTS marriage_hall_db;
USE marriage_hall_db;

CREATE TABLE IF NOT EXISTS MarriageHallBookings (
    BookingID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerName VARCHAR(100) NOT NULL,
    HallName VARCHAR(100) NOT NULL,
    EventDate DATE NOT NULL,
    HallCapacity INT NOT NULL,
    RentAmount DECIMAL(10,2) NOT NULL,
    BookingStatus VARCHAR(20) NOT NULL,
    INDEX idx_hall_event_date (HallName, EventDate)
);

-- Optional sample records. Use future dates when testing.
INSERT INTO MarriageHallBookings
(CustomerName, HallName, EventDate, HallCapacity, RentAmount, BookingStatus)
VALUES
('Rahul Kumar', 'Grand Palace Hall', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 500, 75000.00, 'CONFIRMED'),
('Ananya Rao', 'Royal Celebration Hall', DATE_ADD(CURDATE(), INTERVAL 45 DAY), 300, 55000.00, 'PENDING');
