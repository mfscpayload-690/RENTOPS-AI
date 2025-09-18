package models;

import java.math.BigDecimal;

public class Car {
    private int id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String status;
    private String specs;
    private BigDecimal pricePerDay;

    public Car(int id, String make, String model, int year, String licensePlate, String status, String specs, BigDecimal pricePerDay) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.status = status;
        this.specs = specs;
        this.pricePerDay = pricePerDay;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
}
