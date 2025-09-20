-- SQL Script to add new car entries to the rentops_ai database

INSERT INTO cars (make, model, year, license_plate, status, specs, price_per_day, total_km_driven)
VALUES 
-- Car 1
('Maruti Suzuki', 'Dzire', 2018, 'KL28F3345', 'available', 
'1.2L | 4 Cylinder | Petrol | Manual | Sedan', 889.00, 62000),

-- Car 2
('Renault', 'Kwid', 2017, 'KL03B9821', 'available', 
'1.0L | 3 Cylinder | Petrol | Manual | Hatchback', 599.00, 70800),

-- Car 3
('Toyota', 'Fortuner', 2022, 'KL27X4762', 'available', 
'2.7L | 4 Cylinder | Petrol | Manual | SUV', 2499.00, 49400),

-- Car 4
('Hyundai', 'Verna', 2024, 'KL03Z7841', 'available', 
'1.5L | 4 Cylinder | Petrol | Manual | Sedan', 1599.00, 1900),

-- Car 5
('Tata', 'Tiago', 2021, 'KL28V2209', 'available', 
'1.2L | 3 Cylinder | Petrol | Manual | Hatchback', 799.00, 31700),

-- Car 6
('Toyota', 'Etios', 2019, 'KL28C3421', 'available', 
'1.5L | 4 Cylinder | Petrol | Manual | Hatchback', 659.00, 49600),

-- Car 7
('Honda', 'City', 2020, 'KL27D2234', 'available', 
'1.5L | 4 Cylinder | Petrol | Manual | Sedan', 899.00, 38200),

-- Car 8
('Maruti Suzuki', 'Baleno', 2019, 'KL03E1987', 'available', 
'1.2L | 4 Cylinder | Petrol | Manual | Hatchback', 600.00, 54800),

-- Car 9
('Kia', 'Seltos', 2021, 'KL28W6611', 'available', 
'1.5L | 4 Cylinder | Petrol | Manual | SUV', 1199.00, 24400),

-- Car 10
('Tata', 'Nexon', 2022, 'KL27X7743', 'available', 
'1.2L | 3 Cylinder | Petrol | Manual | SUV', 1059.00, 15900),

-- Car 11
('Hyundai', 'i20 Sport', 2023, 'KL03Y5589', 'available', 
'1.2L | 4 Cylinder | Petrol | Manual | Hatchback', 899.00, 7800),

-- Car 12
('Skoda', 'Slavia', 2024, 'KL28Z9274', 'available', 
'1.0L | 3 Cylinder | Petrol | Manual | Sedan', 999.00, 2200),

-- Car 13
('Mahindra', 'XUV700', 2022, 'KL27V2025', 'available', 
'2.0L | 4 Cylinder | Petrol | Manual | SUV', 1799.00, 17800);