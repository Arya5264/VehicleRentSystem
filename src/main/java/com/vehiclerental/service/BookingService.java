package com.vehiclerental.service;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.XMLUtil;
import com.vehiclerental.util.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class handling booking creation, date calculations, status updates,
 * and rental lifecycle management.
 */
public class BookingService {
    private static final Logger LOGGER = Logger.getLogger(BookingService.class.getName());
    private final VehicleService vehicleService = new VehicleService();

    public List<Booking> getAllBookings() {
        return XPathUtil.findAllBookings();
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return XPathUtil.findBookingsByUserId(userId);
    }

    public Booking getBookingById(String bookingId) {
        return XPathUtil.findBookingById(bookingId);
    }

    /**
     * Calculates days between two date strings (YYYY-MM-DD).
     */
    public int calculateRentalDays(String pickupDateStr, String returnDateStr) {
        LocalDate pickup = LocalDate.parse(pickupDateStr);
        LocalDate returnDate = LocalDate.parse(returnDateStr);
        long days = ChronoUnit.DAYS.between(pickup, returnDate);
        return (int) (days <= 0 ? 1 : days);
    }

    /**
     * Validates rental dates:
     * - Pickup date cannot be in the past (before today)
     * - Return date cannot be before pickup date
     * - Rental days must be at least 1
     */
    public boolean validateDates(String pickupDateStr, String returnDateStr) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate pickup = LocalDate.parse(pickupDateStr);
            LocalDate returnDate = LocalDate.parse(returnDateStr);

            if (pickup.isBefore(today)) {
                return false;
            }
            if (returnDate.isBefore(pickup)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a new booking with rental validation, XML persistence, and vehicle status update.
     */
    public synchronized Booking createBooking(String userId, String customerName, String vehicleId,
                                              String pickupDateStr, String returnDateStr) {
        // 1. Validate vehicle
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found: " + vehicleId);
        }

        // 2. Check availability
        if (!"Available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalStateException("Vehicle is currently " + vehicle.getStatus());
        }

        // 3. Validate dates
        if (!validateDates(pickupDateStr, returnDateStr)) {
            throw new IllegalArgumentException("Invalid rental dates specified.");
        }

        // 4. Calculate rental days and total
        int rentalDays = calculateRentalDays(pickupDateStr, returnDateStr);
        double totalAmount = rentalDays * vehicle.getPricePerDay();

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.BOOKINGS_XML);
            Element root = doc.getDocumentElement();

            // Generate Booking ID (BK001, BK002...)
            NodeList list = root.getElementsByTagName("booking");
            int maxId = 1;
            for (int i = 0; i < list.getLength(); i++) {
                Element bEl = (Element) list.item(i);
                String idStr = XMLUtil.getChildText(bEl, "bookingId");
                if (idStr.startsWith("BK")) {
                    try {
                        int num = Integer.parseInt(idStr.substring(2));
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            String newBookingId = String.format("BK%03d", maxId + 1);

            Element bEl = doc.createElement("booking");
            XMLUtil.appendChildElement(doc, bEl, "bookingId", newBookingId);
            XMLUtil.appendChildElement(doc, bEl, "userId", userId);
            XMLUtil.appendChildElement(doc, bEl, "customerName", customerName);
            XMLUtil.appendChildElement(doc, bEl, "vehicleId", vehicleId);
            XMLUtil.appendChildElement(doc, bEl, "vehicleName", vehicle.getName());
            XMLUtil.appendChildElement(doc, bEl, "pickupDate", pickupDateStr);
            XMLUtil.appendChildElement(doc, bEl, "returnDate", returnDateStr);
            XMLUtil.appendChildElement(doc, bEl, "rentalDays", String.valueOf(rentalDays));
            XMLUtil.appendChildElement(doc, bEl, "pricePerDay", String.valueOf((int) vehicle.getPricePerDay()));
            XMLUtil.appendChildElement(doc, bEl, "totalAmount", String.valueOf((int) totalAmount));
            XMLUtil.appendChildElement(doc, bEl, "bookingStatus", "Confirmed");

            root.appendChild(bEl);
            XMLUtil.saveDocument(doc, XMLUtil.BOOKINGS_XML);

            // Change vehicle status to Rented
            vehicleService.updateVehicleStatus(vehicleId, "Rented");

            LOGGER.info("Created booking " + newBookingId + " for user " + userId + ", vehicle " + vehicleId);

            return new Booking(newBookingId, userId, customerName, vehicleId, vehicle.getName(),
                    pickupDateStr, returnDateStr, rentalDays, vehicle.getPricePerDay(), totalAmount, "Confirmed");
        } catch (Exception e) {
            LOGGER.severe("Error creating booking: " + e.getMessage());
            throw new RuntimeException("Could not complete booking.", e);
        }
    }

    /**
     * Cancels an existing booking and sets vehicle status back to Available.
     */
    public synchronized boolean cancelBooking(String bookingId, String requestingUserId, boolean isAdmin) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            return false;
        }

        // Authorization check: only owner or admin can cancel
        if (!isAdmin && !booking.getUserId().equals(requestingUserId)) {
            return false;
        }

        if ("Cancelled".equalsIgnoreCase(booking.getBookingStatus()) ||
                "Completed".equalsIgnoreCase(booking.getBookingStatus())) {
            return false;
        }

        boolean updated = updateBookingStatus(bookingId, "Cancelled");
        if (updated) {
            vehicleService.updateVehicleStatus(booking.getVehicleId(), "Available");
            LOGGER.info("Booking cancelled: " + bookingId + ". Vehicle released: " + booking.getVehicleId());
        }
        return updated;
    }

    /**
     * Admin approves a pending booking.
     */
    public synchronized boolean approveBooking(String bookingId) {
        return updateBookingStatus(bookingId, "Confirmed");
    }

    /**
     * Marks rental as completed and returns vehicle to Available status.
     */
    public synchronized boolean completeBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            return false;
        }

        boolean updated = updateBookingStatus(bookingId, "Completed");
        if (updated) {
            vehicleService.updateVehicleStatus(booking.getVehicleId(), "Available");
            LOGGER.info("Booking completed: " + bookingId + ". Vehicle released: " + booking.getVehicleId());
        }
        return updated;
    }

    /**
     * Updates booking status in bookings.xml using XPath.
     */
    public synchronized boolean updateBookingStatus(String bookingId, String newStatus) {
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.BOOKINGS_XML);
            XPath xpath = XPathUtil.newXPath();
            XPathExpression expr = xpath.compile("/bookings/booking[bookingId='" + bookingId + "']");
            Element bEl = (Element) expr.evaluate(doc, XPathConstants.NODE);

            if (bEl != null) {
                XMLUtil.setChildText(doc, bEl, "bookingStatus", newStatus);
                XMLUtil.saveDocument(doc, XMLUtil.BOOKINGS_XML);
                return true;
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating booking status: " + e.getMessage());
        }
        return false;
    }

    public int getTotalBookingCount() {
        return getAllBookings().size();
    }

    public int getActiveBookingCount(String userId) {
        List<Booking> list = getBookingsByUserId(userId);
        int count = 0;
        for (Booking b : list) {
            if ("Confirmed".equalsIgnoreCase(b.getBookingStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getCompletedBookingCount(String userId) {
        List<Booking> list = getBookingsByUserId(userId);
        int count = 0;
        for (Booking b : list) {
            if ("Completed".equalsIgnoreCase(b.getBookingStatus())) {
                count++;
            }
        }
        return count;
    }
}
