package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Car {

    private int id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String status;
    private String specs;
    private BigDecimal pricePerDay;
    private int totalKmDriven;
    private List<String> exteriorImages;
    private List<String> interiorImages;

    public Car(int id, String make, String model, int year, String licensePlate, String status, String specs, BigDecimal pricePerDay) {
        this(id, make, model, year, licensePlate, status, specs, pricePerDay, 0);
    }

    public Car(int id, String make, String model, int year, String licensePlate, String status, String specs, BigDecimal pricePerDay, int totalKmDriven) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.status = status;
        this.specs = specs;
        this.pricePerDay = pricePerDay;
        this.totalKmDriven = totalKmDriven;
        this.exteriorImages = new ArrayList<>();
        this.interiorImages = new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getTotalKmDriven() {
        return totalKmDriven;
    }

    public void setTotalKmDriven(int totalKmDriven) {
        this.totalKmDriven = totalKmDriven;
    }

    public List<String> getExteriorImages() {
        return exteriorImages != null ? exteriorImages : new ArrayList<>();
    }

    public void setExteriorImages(List<String> exteriorImages) {
        this.exteriorImages = exteriorImages != null ? exteriorImages : new ArrayList<>();
    }

    public List<String> getInteriorImages() {
        return interiorImages != null ? interiorImages : new ArrayList<>();
    }

    public void setInteriorImages(List<String> interiorImages) {
        this.interiorImages = interiorImages != null ? interiorImages : new ArrayList<>();
    }

    public void addExteriorImage(String imagePath) {
        if (this.exteriorImages == null) {
            this.exteriorImages = new ArrayList<>();
        }
        this.exteriorImages.add(imagePath);
    }

    public void addInteriorImage(String imagePath) {
        if (this.interiorImages == null) {
            this.interiorImages = new ArrayList<>();
        }
        this.interiorImages.add(imagePath);
    }

    public void removeExteriorImage(String imagePath) {
        if (this.exteriorImages != null) {
            this.exteriorImages.remove(imagePath);
        }
    }

    public void removeInteriorImage(String imagePath) {
        if (this.interiorImages != null) {
            this.interiorImages.remove(imagePath);
        }
    }
}
