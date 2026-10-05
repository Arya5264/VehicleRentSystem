package com.vehiclerental.service;

import com.vehiclerental.model.User;
import com.vehiclerental.util.XMLUtil;
import com.vehiclerental.util.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class handling user authentication, registration, and user retrieval.
 */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    /**
     * Authenticates a user with given username and password.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = XPathUtil.findUserByUsername(username.trim());
        if (user != null && password.trim().equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    /**
     * Finds a user by their username using XPath.
     */
    public User getUserByUsername(String username) {
        return XPathUtil.findUserByUsername(username);
    }

    /**
     * Finds a user by their ID using XPath.
     */
    public User getUserById(String id) {
        return XPathUtil.findUserById(id);
    }

    /**
     * Retrieves all registered customers using XPath.
     */
    public List<User> getAllCustomers() {
        return XPathUtil.findAllCustomers();
    }

    /**
     * Registers a new customer into users.xml.
     */
    public synchronized boolean registerCustomer(String name, String username, String password) {
        if (name == null || username == null || password == null ||
                name.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Check if username already exists
        if (XPathUtil.findUserByUsername(username.trim()) != null) {
            return false;
        }

        try {
            Document doc = XMLUtil.loadDocument(XMLUtil.USERS_XML);
            Element root = doc.getDocumentElement();

            // Generate new User ID (e.g. U003)
            NodeList users = root.getElementsByTagName("user");
            int maxIdNum = 2;
            for (int i = 0; i < users.getLength(); i++) {
                Element uEl = (Element) users.item(i);
                String idStr = XMLUtil.getChildText(uEl, "id");
                if (idStr.startsWith("U")) {
                    try {
                        int num = Integer.parseInt(idStr.substring(1));
                        if (num > maxIdNum) {
                            maxIdNum = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            String newId = String.format("U%03d", maxIdNum + 1);

            Element userEl = doc.createElement("user");
            XMLUtil.appendChildElement(doc, userEl, "id", newId);
            XMLUtil.appendChildElement(doc, userEl, "name", name.trim());
            XMLUtil.appendChildElement(doc, userEl, "username", username.trim());
            XMLUtil.appendChildElement(doc, userEl, "password", password.trim());
            XMLUtil.appendChildElement(doc, userEl, "role", "CUSTOMER");

            root.appendChild(userEl);
            XMLUtil.saveDocument(doc, XMLUtil.USERS_XML);
            LOGGER.info("Registered new customer: " + username + " with ID: " + newId);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error during customer registration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns total registered customers count.
     */
    public int getCustomerCount() {
        return getAllCustomers().size();
    }
}
