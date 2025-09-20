-- Add total_km_driven column to cars table
ALTER TABLE cars ADD COLUMN total_km_driven INT DEFAULT 0 NOT NULL;

-- Update existing car entries to have default values
UPDATE cars SET total_km_driven = 
  CASE 
    WHEN make = 'Toyota' AND model = 'Corolla' THEN 12500
    WHEN make = 'Honda' AND model = 'Civic' THEN 8200
    WHEN make = 'Ford' AND model = 'F-150' THEN 15700
    WHEN make = 'BMW' AND model = 'X5' THEN 10300
    WHEN make = 'Maruti Suzuki' AND model = 'WagonR TOUR' THEN 5000
    WHEN make = 'Maruti Suzuki' AND model = 'Swift' THEN 45000
    WHEN make = 'Hyundai' AND model = 'Creta' THEN 32000
    ELSE 0
  END;