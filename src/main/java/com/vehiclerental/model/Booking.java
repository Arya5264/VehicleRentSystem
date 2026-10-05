package com.vehiclerental.model;

import java.io.Serializable;

/**
 * Booking model class representing vehicle rental bookings.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookingId;
    private String userId;
    private String customerName;
    private String vehicleId;
    private String vehicleName;
    private String pickupDate;
    private String returnDate;
    private int rentalDays;
    private double pricePerDay;
    private double totalAmount;
    private String bookingStatus; // Confirmed, Completed, Cancelled

    public Booking() {
    }

    public Booking(String bookingId, String userId, String customerName, String vehicleId, 
                   String vehicleName, String pickupDate, String returnDate, int rentalDays, 
                   double pricePerDay, double totalAmount, String bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.rentalDays = rentalDays;
        this.pricePerDay = pricePerDay;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", pickupDate='" + pickupDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", rentalDays=" + rentalDays +
                ", pricePerDay=" + pricePerDay +
                ", totalAmount=" + totalAmount +
                ", bookingStatus='" + bookingStatus + '\'' +
                '}';
    }
}
