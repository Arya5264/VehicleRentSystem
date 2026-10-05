package com.vehiclerental.model;

import java.io.Serializable;

/**
 * Vehicle model class representing vehicle entities in the rental system.
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String brand;
    private String model;
    private String type;
    private String fuel;
    private double pricePerDay;
    private String registrationNumber;
    private String status;

    public Vehicle() {
    }

    public Vehicle(String id, String name, String brand, String model, String type, 
                   String fuel, double pricePerDay, String registrationNumber, String status) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.fuel = fuel;
        this.pricePerDay = pricePerDay;
        this.registrationNumber = registrationNumber;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", fuel='" + fuel + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
