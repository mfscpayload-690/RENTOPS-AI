-- Database schema update for multi-image support
-- Add columns for storing JSON arrays of image file paths

-- Add exterior images column (stores JSON array of file paths)
ALTER TABLE cars ADD COLUMN exterior_images TEXT;

-- Add interior images column (stores JSON array of file paths)  
ALTER TABLE cars ADD COLUMN interior_images TEXT;

-- Optional: Add comments for documentation
ALTER TABLE cars MODIFY COLUMN exterior_images TEXT COMMENT 'JSON array of exterior image file paths';
ALTER TABLE cars MODIFY COLUMN interior_images TEXT COMMENT 'JSON array of interior image file paths';

-- Example of data format:
-- exterior_images: '["images/cars/1/exterior/exterior_1.jpg", "images/cars/1/exterior/exterior_2.png"]'
-- interior_images: '["images/cars/1/interior/interior_1.jpg", "images/cars/1/interior/interior_2.jpeg"]'