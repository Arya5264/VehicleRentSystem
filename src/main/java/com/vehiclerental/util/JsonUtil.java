package com.vehiclerental.util;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;

import java.util.List;

/**
 * Lightweight JSON utility for serializing models without external third-party dependencies.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String toJson(Vehicle v) {
        if (v == null) return "null";
        return "{" +
                "\"id\":\"" + escape(v.getId()) + "\"," +
                "\"name\":\"" + escape(v.getName()) + "\"," +
                "\"brand\":\"" + escape(v.getBrand()) + "\"," +
                "\"model\":\"" + escape(v.getModel()) + "\"," +
                "\"type\":\"" + escape(v.getType()) + "\"," +
                "\"fuel\":\"" + escape(v.getFuel()) + "\"," +
                "\"pricePerDay\":" + v.getPricePerDay() + "," +
                "\"registrationNumber\":\"" + escape(v.getRegistrationNumber()) + "\"," +
                "\"status\":\"" + escape(v.getStatus()) + "\"" +
                "}";
    }

    public static String vehicleListToJson(List<Vehicle> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toJson(User u) {
        if (u == null) return "null";
        return "{" +
                "\"id\":\"" + escape(u.getId()) + "\"," +
                "\"name\":\"" + escape(u.getName()) + "\"," +
                "\"username\":\"" + escape(u.getUsername()) + "\"," +
                "\"role\":\"" + escape(u.getRole()) + "\"" +
                "}";
    }

    public static String userListToJson(List<User> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toJson(Booking b) {
        if (b == null) return "null";
        return "{" +
                "\"bookingId\":\"" + escape(b.getBookingId()) + "\"," +
                "\"userId\":\"" + escape(b.getUserId()) + "\"," +
                "\"customerName\":\"" + escape(b.getCustomerName()) + "\"," +
                "\"vehicleId\":\"" + escape(b.getVehicleId()) + "\"," +
                "\"vehicleName\":\"" + escape(b.getVehicleName()) + "\"," +
                "\"pickupDate\":\"" + escape(b.getPickupDate()) + "\"," +
                "\"returnDate\":\"" + escape(b.getReturnDate()) + "\"," +
                "\"rentalDays\":" + b.getRentalDays() + "," +
                "\"pricePerDay\":" + b.getPricePerDay() + "," +
                "\"totalAmount\":" + b.getTotalAmount() + "," +
                "\"bookingStatus\":\"" + escape(b.getBookingStatus()) + "\"" +
                "}";
    }

    public static String bookingListToJson(List<Booking> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
