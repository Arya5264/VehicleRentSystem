package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.XMLUtil;
import com.vehiclerental.util.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class handling vehicle listing, querying, addition, modification,
 * status changes, and deletion with XML persistence.
 */
public class VehicleService {
    private static final Logger LOGGER = Logger.getLogger(VehicleService.class.getName());

    public List<Vehicle> getAllVehicles() {
        return XPathUtil.findAllVehicles();
    }

    public List<Vehicle> getAvailableVehicles() {
        return XPathUtil.findAllAvailableVehicles();
    }

    public Vehicle getVehicleById(String id) {
        return XPathUtil.findVehicleById(id);
    }

    public List<Vehicle> getVehiclesByType(String type) {
        return XPathUtil.findVehiclesByType(type);
    }

    public List<Vehicle> getAvailableVehiclesByType(String type) {
        return XPathUtil.findAvailableVehiclesByType(type);
    }

    public List<Vehicle> getVehiclesBelowPrice(double maxPrice) {
        return XPathUtil.findVehiclesBelowPrice(maxPrice);
    }

    public List<Vehicle> searchVehicles(String keyword, String type, String fuel, Double maxPrice, String status) {
        return XPathUtil.searchVehicles(keyword, type, fuel, maxPrice, status);
    }

    /**
     * Adds a new vehicle to vehicles.xml.
     */
    public synchronized boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            return false;
        }

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            Element root = doc.getDocumentElement();

            // Auto-generate ID if missing or empty (e.g. V009)
            String id = vehicle.getId();
            if (id == null || id.trim().isEmpty()) {
                NodeList list = root.getElementsByTagName("vehicle");
                int maxNum = 8;
                for (int i = 0; i < list.getLength(); i++) {
                    Element vEl = (Element) list.item(i);
                    String vId = XMLUtil.getChildText(vEl, "id");
                    if (vId.startsWith("V")) {
                        try {
                            int num = Integer.parseInt(vId.substring(1));
                            if (num > maxNum) {
                                maxNum = num;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
                id = String.format("V%03d", maxNum + 1);
                vehicle.setId(id);
            }

            Element vEl = doc.createElement("vehicle");
            XMLUtil.appendChildElement(doc, vEl, "id", vehicle.getId());
            XMLUtil.appendChildElement(doc, vEl, "name", vehicle.getName());
            XMLUtil.appendChildElement(doc, vEl, "brand", vehicle.getBrand());
            XMLUtil.appendChildElement(doc, vEl, "model", vehicle.getModel());
            XMLUtil.appendChildElement(doc, vEl, "type", vehicle.getType());
            XMLUtil.appendChildElement(doc, vEl, "fuel", vehicle.getFuel());
            XMLUtil.appendChildElement(doc, vEl, "pricePerDay", String.valueOf((int) vehicle.getPricePerDay()));
            XMLUtil.appendChildElement(doc, vEl, "registrationNumber", vehicle.getRegistrationNumber());
            XMLUtil.appendChildElement(doc, vEl, "status", vehicle.getStatus() != null ? vehicle.getStatus() : "Available");

            root.appendChild(vEl);
            XMLUtil.saveDocument(doc, XMLUtil.VEHICLES_XML);
            LOGGER.info("Vehicle added successfully with ID: " + vehicle.getId());
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing vehicle's attributes.
     */
    public synchronized boolean updateVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null) {
            return false;
        }

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = XPathUtil.newXPath();
            XPathExpression expr = xpath.compile("/vehicles/vehicle[id='" + vehicle.getId() + "']");
            Element vEl = (Element) expr.evaluate(doc, XPathConstants.NODE);

            if (vEl != null) {
                XMLUtil.setChildText(doc, vEl, "name", vehicle.getName());
                XMLUtil.setChildText(doc, vEl, "brand", vehicle.getBrand());
                XMLUtil.setChildText(doc, vEl, "model", vehicle.getModel());
                XMLUtil.setChildText(doc, vEl, "type", vehicle.getType());
                XMLUtil.setChildText(doc, vEl, "fuel", vehicle.getFuel());
                XMLUtil.setChildText(doc, vEl, "pricePerDay", String.valueOf((int) vehicle.getPricePerDay()));
                XMLUtil.setChildText(doc, vEl, "registrationNumber", vehicle.getRegistrationNumber());
                if (vehicle.getStatus() != null && !vehicle.getStatus().isEmpty()) {
                    XMLUtil.setChildText(doc, vEl, "status", vehicle.getStatus());
                }

                XMLUtil.saveDocument(doc, XMLUtil.VEHICLES_XML);
                LOGGER.info("Vehicle updated: " + vehicle.getId());
                return true;
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating vehicle: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates vehicle status (e.g. Available, Rented, Maintenance).
     */
    public synchronized boolean updateVehicleStatus(String vehicleId, String status) {
        if (vehicleId == null || status == null) {
            return false;
        }

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = XPathUtil.newXPath();
            XPathExpression expr = xpath.compile("/vehicles/vehicle[id='" + vehicleId + "']");
            Element vEl = (Element) expr.evaluate(doc, XPathConstants.NODE);

            if (vEl != null) {
                XMLUtil.setChildText(doc, vEl, "status", status);
                XMLUtil.saveDocument(doc, XMLUtil.VEHICLES_XML);
                LOGGER.info("Updated status for vehicle " + vehicleId + " to " + status);
                return true;
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating vehicle status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a vehicle by its ID.
     */
    public synchronized boolean deleteVehicle(String vehicleId) {
        if (vehicleId == null) {
            return false;
        }

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.VEHICLES_XML);
            XPath xpath = XPathUtil.newXPath();
            XPathExpression expr = xpath.compile("/vehicles/vehicle[id='" + vehicleId + "']");
            Element vEl = (Element) expr.evaluate(doc, XPathConstants.NODE);

            if (vEl != null) {
                vEl.getParentNode().removeChild(vEl);
                XMLUtil.saveDocument(doc, XMLUtil.VEHICLES_XML);
                LOGGER.info("Vehicle deleted: " + vehicleId);
                return true;
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting vehicle: " + e.getMessage());
        }
        return false;
    }

    public int getTotalVehicleCount() {
        return getAllVehicles().size();
    }

    public int getAvailableVehicleCount() {
        return getAvailableVehicles().size();
    }

    public int getRentedVehicleCount() {
        List<Vehicle> list = getAllVehicles();
        int count = 0;
        for (Vehicle v : list) {
            if ("Rented".equalsIgnoreCase(v.getStatus())) {
                count++;
            }
        }
        return count;
    }
}
