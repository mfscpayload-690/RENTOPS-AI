-- Add image_url column to cars table
ALTER TABLE cars ADD COLUMN image_url VARCHAR(255) DEFAULT NULL;

-- Update existing cars with sample image URLs
UPDATE cars SET image_url = 'https://example.com/images/cars/toyota_corolla_2022/' WHERE license_plate = 'ABC-123';
UPDATE cars SET image_url = 'https://example.com/images/cars/honda_civic_2023/' WHERE license_plate = 'XYZ-789';
UPDATE cars SET image_url = 'https://example.com/images/cars/tesla_model3_2023/' WHERE license_plate = 'EV-1234';
UPDATE cars SET image_url = 'https://example.com/images/cars/ford_f150_2022/' WHERE license_plate = 'TRK-456';
UPDATE cars SET image_url = 'https://example.com/images/cars/bmw_x5_2023/' WHERE license_plate = 'LUX-888';