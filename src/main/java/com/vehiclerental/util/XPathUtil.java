package com.vehiclerental.util;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class demonstrating Java XPath querying capabilities
 * to search, filter, and extract records from XML databases.
 */
public class XPathUtil {
    private static final Logger LOGGER = Logger.getLogger(XPathUtil.class.getName());
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    /**
     * Helper to create a new XPath instance.
     */
    public static XPath newXPath() {
        return XPATH_FACTORY.newXPath();
    }

    // =========================================================================
    // 1. VEHICLE XPATH QUERIES
    // =========================================================================

    /**
     * 1. Find vehicle by ID
     * XPath: /vehicles/vehicle[id='V001']
     */
    public static Vehicle findVehicleById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle[id='" + sanitize(id) + "']";
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node instanceof Element) {
                return parseVehicleElement((Element) node);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for vehicle ID: " + id, e);
        }
        return null;
    }

    /**
     * 2. Find all available vehicles
     * XPath: /vehicles/vehicle[status='Available']
     */
    public static List<Vehicle> findAllAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle[status='Available']";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseVehicleElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for available vehicles", e);
        }
        return list;
    }

    /**
     * 3. Find vehicles by type
     * XPath: /vehicles/vehicle[type='SUV']
     */
    public static List<Vehicle> findVehiclesByType(String type) {
        List<Vehicle> list = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) {
            return list;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle[type='" + sanitize(type) + "']";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseVehicleElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for vehicles by type: " + type, e);
        }
        return list;
    }

    /**
     * 4. Find vehicles below a specified price
     * XPath: /vehicles/vehicle[pricePerDay < 2000]
     */
    public static List<Vehicle> findVehiclesBelowPrice(double maxPrice) {
        List<Vehicle> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle[pricePerDay < " + (long) maxPrice + "]";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseVehicleElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for vehicles below price: " + maxPrice, e);
        }
        return list;
    }

    /**
     * 5. Find available vehicles by type
     * XPath: /vehicles/vehicle[type='SUV' and status='Available']
     */
    public static List<Vehicle> findAvailableVehiclesByType(String type) {
        List<Vehicle> list = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) {
            return list;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle[type='" + sanitize(type) + "' and status='Available']";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseVehicleElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for available vehicles by type: " + type, e);
        }
        return list;
    }

    /**
     * Find all vehicles
     * XPath: /vehicles/vehicle
     */
    public static List<Vehicle> findAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();
            String expression = "/vehicles/vehicle";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseVehicleElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for all vehicles", e);
        }
        return list;
    }

    /**
     * Advanced filter query combining type, fuel, price, and status via XPath.
     */
    public static List<Vehicle> searchVehicles(String keyword, String type, String fuel, Double maxPrice, String status) {
        List<Vehicle> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = newXPath();

            List<String> conditions = new ArrayList<>();
            if (type != null && !type.trim().isEmpty() && !"All".equalsIgnoreCase(type)) {
                conditions.add("type='" + sanitize(type) + "'");
            }
            if (fuel != null && !fuel.trim().isEmpty() && !"All".equalsIgnoreCase(fuel)) {
                conditions.add("fuel='" + sanitize(fuel) + "'");
            }
            if (status != null && !status.trim().isEmpty() && !"All".equalsIgnoreCase(status)) {
                conditions.add("status='" + sanitize(status) + "'");
            }
            if (maxPrice != null && maxPrice > 0) {
                conditions.add("number(pricePerDay) <= " + maxPrice);
            }

            StringBuilder xpathExpr = new StringBuilder("/vehicles/vehicle");
            if (!conditions.isEmpty()) {
                xpathExpr.append("[").append(String.join(" and ", conditions)).append("]");
            }

            XPathExpression expr = xpath.compile(xpathExpr.toString());
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    Vehicle v = parseVehicleElement((Element) nodes.item(i));
                    // Apply keyword search on name or brand if provided
                    if (keyword == null || keyword.trim().isEmpty() ||
                            v.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            v.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                            v.getModel().toLowerCase().contains(keyword.toLowerCase())) {
                        list.add(v);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing advanced XPath search", e);
        }
        return list;
    }

    // =========================================================================
    // 2. USER XPATH QUERIES
    // =========================================================================

    /**
     * 6. Find user by username
     * XPath: /users/user[username='customer']
     */
    public static User findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.USERS_XML);
            XPath xpath = newXPath();
            String expression = "/users/user[username='" + sanitize(username) + "']";
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node instanceof Element) {
                return parseUserElement((Element) node);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for username: " + username, e);
        }
        return null;
    }

    /**
     * Find user by ID
     * XPath: /users/user[id='U001']
     */
    public static User findUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.USERS_XML);
            XPath xpath = newXPath();
            String expression = "/users/user[id='" + sanitize(id) + "']";
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node instanceof Element) {
                return parseUserElement((Element) node);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for user ID: " + id, e);
        }
        return null;
    }

    /**
     * Find all customers
     * XPath: /users/user[role='CUSTOMER']
     */
    public static List<User> findAllCustomers() {
        List<User> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.USERS_XML);
            XPath xpath = newXPath();
            String expression = "/users/user[role='CUSTOMER']";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseUserElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for all customers", e);
        }
        return list;
    }

    // =========================================================================
    // 3. BOOKING XPATH QUERIES
    // =========================================================================

    /**
     * 7. Find booking by booking ID
     * XPath: /bookings/booking[bookingId='BK001']
     */
    public static Booking findBookingById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.BOOKINGS_XML);
            XPath xpath = newXPath();
            String expression = "/bookings/booking[bookingId='" + sanitize(bookingId) + "']";
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node instanceof Element) {
                return parseBookingElement((Element) node);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for booking ID: " + bookingId, e);
        }
        return null;
    }

    /**
     * 8. Find bookings for a particular user
     * XPath: /bookings/booking[userId='U002']
     */
    public static List<Booking> findBookingsByUserId(String userId) {
        List<Booking> list = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) {
            return list;
        }
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.BOOKINGS_XML);
            XPath xpath = newXPath();
            String expression = "/bookings/booking[userId='" + sanitize(userId) + "']";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseBookingElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for bookings by user ID: " + userId, e);
        }
        return list;
    }

    /**
     * Find all bookings
     * XPath: /bookings/booking
     */
    public static List<Booking> findAllBookings() {
        List<Booking> list = new ArrayList<>();
        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.BOOKINGS_XML);
            XPath xpath = newXPath();
            String expression = "/bookings/booking";
            XPathExpression expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    list.add(parseBookingElement((Element) nodes.item(i)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing XPath query for all bookings", e);
        }
        return list;
    }

    // =========================================================================
    // PARSING HELPERS
    // =========================================================================

    public static Vehicle parseVehicleElement(Element el) {
        String id = XMLUtil.getChildText(el, "id");
        String name = XMLUtil.getChildText(el, "name");
        String brand = XMLUtil.getChildText(el, "brand");
        String model = XMLUtil.getChildText(el, "model");
        String type = XMLUtil.getChildText(el, "type");
        String fuel = XMLUtil.getChildText(el, "fuel");
        String priceStr = XMLUtil.getChildText(el, "pricePerDay");
        String regNum = XMLUtil.getChildText(el, "registrationNumber");
        String status = XMLUtil.getChildText(el, "status");

        double price = 0.0;
        try {
            if (!priceStr.isEmpty()) {
                price = Double.parseDouble(priceStr);
            }
        } catch (NumberFormatException ignored) {
        }

        return new Vehicle(id, name, brand, model, type, fuel, price, regNum, status);
    }

    public static User parseUserElement(Element el) {
        String id = XMLUtil.getChildText(el, "id");
        String name = XMLUtil.getChildText(el, "name");
        String username = XMLUtil.getChildText(el, "username");
        String password = XMLUtil.getChildText(el, "password");
        String role = XMLUtil.getChildText(el, "role");

        return new User(id, name, username, password, role);
    }

    public static Booking parseBookingElement(Element el) {
        String bookingId = XMLUtil.getChildText(el, "bookingId");
        String userId = XMLUtil.getChildText(el, "userId");
        String customerName = XMLUtil.getChildText(el, "customerName");
        String vehicleId = XMLUtil.getChildText(el, "vehicleId");
        String vehicleName = XMLUtil.getChildText(el, "vehicleName");
        String pickupDate = XMLUtil.getChildText(el, "pickupDate");
        String returnDate = XMLUtil.getChildText(el, "returnDate");
        String rentalDaysStr = XMLUtil.getChildText(el, "rentalDays");
        String priceStr = XMLUtil.getChildText(el, "pricePerDay");
        String totalAmountStr = XMLUtil.getChildText(el, "totalAmount");
        String bookingStatus = XMLUtil.getChildText(el, "bookingStatus");

        int days = 1;
        try {
            if (!rentalDaysStr.isEmpty()) {
                days = Integer.parseInt(rentalDaysStr);
            }
        } catch (NumberFormatException ignored) {
        }

        double price = 0.0;
        try {
            if (!priceStr.isEmpty()) {
                price = Double.parseDouble(priceStr);
            }
        } catch (NumberFormatException ignored) {
        }

        double total = 0.0;
        try {
            if (!totalAmountStr.isEmpty()) {
                total = Double.parseDouble(totalAmountStr);
            }
        } catch (NumberFormatException ignored) {
        }

        return new Booking(bookingId, userId, customerName, vehicleId, vehicleName, 
                pickupDate, returnDate, days, price, total, bookingStatus);
    }

    private static String sanitize(String input) {
        if (input == null) return "";
        return input.replace("'", "&apos;").replace("\"", "&quot;");
    }
}
