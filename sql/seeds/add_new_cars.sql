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
'2.0L | 4 Cylinder | Petrol | Manual | SUV', 1799.00, 17800),

-- Car 14
('Maruti Suzuki', 'Swift', 2018, 'KL27A1423', 'available', 
'1.2L | 4 Cylinder | Petrol | 5-speed Manual | Hatchback', 600.00, 62000),

-- Car 15
('Hyundai', 'Grand i10 Nios', 2019, 'KL28B4832', 'available', 
'1.2L | 4 Cylinder | Petrol | 5-speed Manual | Hatchback', 580.00, 54300),

-- Car 16
('Tata', 'Tiago', 2020, 'KL03C1201', 'available', 
'1.2L | 3 Cylinder | Petrol | 5-speed Manual | Hatchback', 550.00, 39800),

-- Car 17
('Honda', 'Amaze', 2018, 'KL27D2290', 'maintenance', 
'1.5L | 4 Cylinder | Diesel | 5-speed Manual | Sedan', 750.00, 67200),

-- Car 18
('Hyundai', 'Aura', 2020, 'KL28E3294', 'unavailable', 
'1.2L | 3 Cylinder | Diesel | 5-speed Manual | Sedan', 690.00, 41500),

-- Car 19
('Maruti Suzuki', 'Ciaz', 2021, 'KL03U8081', 'available', 
'1.5L | 4 Cylinder | Petrol | 5-speed Manual | Sedan', 900.00, 18400),

-- Car 20
('Tata', 'Nexon', 2022, 'KL27V4532', 'available', 
'1.5L | 4 Cylinder | Diesel | 6-speed Manual | SUV', 1250.00, 15600),

-- Car 21
('Hyundai', 'Creta', 2023, 'KL28W6220', 'available', 
'1.5L | 4 Cylinder | Petrol | 6-speed Manual | SUV', 1400.00, 6800),

-- Car 22
('Kia', 'Seltos', 2024, 'KL03X9003', 'available', 
'1.5L | 4 Cylinder | Diesel | 6-speed Manual | SUV', 1500.00, 2300),

-- Car 23
('Maruti Suzuki', 'Alto K10', 2022, 'KL27Z3345', 'maintenance', 
'1.0L | 3 Cylinder | Petrol | 5-speed Manual | Hatchback', 500.00, 17900),

-- Car 24
('Volkswagen', 'Polo', 2019, 'KL28K8502', 'available', 
'1.0L | 3 Cylinder | Turbo Petrol | 6-speed Manual | Hatchback', 650.00, 41600),

-- Car 25
('Toyota', 'Urban Cruiser Hyryder', 2023, 'KL27L1024', 'available', 
'1.5L | 4 Cylinder | Petrol Hybrid | e-CVT | SUV', 1700.00, 7200),

-- Car 26
('Renault', 'Duster', 2020, 'KL03M2391', 'maintenance', 
'1.5L | 4 Cylinder | Diesel | 6-speed Manual | SUV', 1100.00, 29900),

-- Car 27
('Mahindra', 'KUV100 NXT', 2018, 'KL28N5107', 'available', 
'1.2L | 3 Cylinder | Petrol | 5-speed Manual | Hatchback', 550.00, 58800),

-- Car 28
('Honda', 'City (5th Gen)', 2021, 'KL27O4320', 'unavailable', 
'1.5L | 4 Cylinder | Petrol | 6-speed Manual | Sedan', 1150.00, 18300),

-- Car 29
('Skoda', 'Kushaq', 2022, 'KL03P3096', 'available', 
'1.0L | 3 Cylinder | Turbo Petrol | 6-speed Manual | SUV', 1200.00, 13500),

-- Car 30
('Maruti Suzuki', 'Ignis', 2018, 'KL28Q2321', 'available', 
'1.2L | 4 Cylinder | Petrol | 5-speed Manual | Hatchback', 500.00, 54300),

-- Car 31
('Hyundai', 'Venue', 2021, 'KL27R6574', 'maintenance', 
'1.5L | 4 Cylinder | Diesel | 6-speed Manual | SUV', 1350.00, 22700),

-- Car 32
('Tata', 'Punch', 2022, 'KL03S1489', 'available', 
'1.2L | 3 Cylinder | Petrol | 5-speed Manual | SUV', 1000.00, 10900),

-- Car 33
('Maruti Suzuki', 'Dzire', 2023, 'KL28T7781', 'available', 
'1.2L | 4 Cylinder | Petrol | 5-speed Manual | Sedan', 800.00, 4900),

-- Car 34
('Ford', 'EcoSport', 2019, 'KL03F1902', 'available', 
'1.5L | 4 Cylinder | Diesel | 5-speed Manual | SUV', 900.00, 53000),

-- Car 35
('Skoda', 'Rapid', 2018, 'KL27G4987', 'maintenance', 
'1.6L | 4 Cylinder | Diesel | 5-speed Manual | Sedan', 800.00, 78200),

-- Car 36
('Mahindra', 'XUV300', 2021, 'KL28P6145', 'available', 
'1.5L | 4 Cylinder | Diesel | 6-speed Manual | SUV', 1300.00, 24400),

-- Car 37
('Renault', 'Kwid', 2017, 'KL03H1225', 'available', 
'1.0L | 3 Cylinder | Petrol | 5-speed Manual | Hatchback', 500.00, 80300),

-- Car 38
('Toyota', 'Yaris', 2020, 'KL27J7771', 'unavailable', 
'1.5L | 4 Cylinder | Petrol | 6-speed Manual | Sedan', 820.00, 30100),

-- Car 39
('Nissan', 'Magnite', 2022, 'KL28Q3442', 'available', 
'1.0L | 3 Cylinder | Turbo Petrol | 5-speed Manual | SUV', 1000.00, 13600),

-- Car 40
('Tata', 'Altroz', 2021, 'KL03R9132', 'maintenance', 
'1.5L | 4 Cylinder | Diesel | 5-speed Manual | Hatchback', 750.00, 21800),

-- Car 41
('Honda', 'Jazz', 2018, 'KL27I4572', 'available', 
'1.2L | 4 Cylinder | Petrol | 5-speed Manual | Hatchback', 600.00, 61900),

-- Car 42
('Maruti Suzuki', 'S-Presso', 2023, 'KL28S1876', 'available', 
'1.0L | 3 Cylinder | Petrol | 5-speed Manual | Hatchback', 550.00, 8700),

-- Car 43
('Hyundai', 'Verna', 2024, 'KL03T5221', 'unavailable', 
'1.5L | 4 Cylinder | Petrol | 6-speed Manual | Sedan', 1100.00, 2400);