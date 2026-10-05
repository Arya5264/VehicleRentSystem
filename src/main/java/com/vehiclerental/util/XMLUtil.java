package com.vehiclerental.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading, writing, and manipulating XML files.
 */
public class XMLUtil {
    private static final Logger LOGGER = Logger.getLogger(XMLUtil.class.getName());

    public static final String VEHICLES_XML = "vehicles.xml";
    public static final String USERS_XML = "users.xml";
    public static final String BOOKINGS_XML = "bookings.xml";

    public static synchronized void setDataDirectory(String path) {
        DataConfig.setDataDirectory(path);
    }

    /**
     * Resolves the target XML file, creating default data if it does not already exist.
     */
    public static synchronized File getXMLFile(String fileName) {
        return DataConfig.getXMLFile(fileName);
    }

    /**
     * Loads an XML Document from the designated file.
     */
    public static synchronized Document loadDocument(String fileName) {
        try {
            File xmlFile = getXMLFile(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(false);
            dbFactory.setValidating(false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading XML document: " + fileName, e);
            throw new RuntimeException("Error loading XML document: " + fileName, e);
        }
    }

    /**
     * Saves an XML Document back to its file with proper indentation.
     */
    public static synchronized void saveDocument(Document doc, String fileName) {
        try {
            File xmlFile = getXMLFile(fileName);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);
            }
            LOGGER.info("Saved XML document: " + xmlFile.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving XML document: " + fileName, e);
            throw new RuntimeException("Error saving XML document: " + fileName, e);
        }
    }

    /**
     * Helper to read the text content of a child element.
     */
    public static String getChildText(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return "";
        }
        NodeList list = parent.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            Node node = list.item(0);
            if (node != null) {
                return node.getTextContent() != null ? node.getTextContent().trim() : "";
            }
        }
        return "";
    }

    /**
     * Helper to update the text content of a child element or create it if missing.
     */
    public static void setChildText(Document doc, Element parent, String tagName, String text) {
        if (parent == null || tagName == null) {
            return;
        }
        NodeList list = parent.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            list.item(0).setTextContent(text != null ? text : "");
        } else {
            Element child = doc.createElement(tagName);
            child.setTextContent(text != null ? text : "");
            parent.appendChild(child);
        }
    }

    /**
     * Creates and appends a child element with text content.
     */
    public static Element appendChildElement(Document doc, Element parent, String tagName, String text) {
        Element element = doc.createElement(tagName);
        element.setTextContent(text != null ? text : "");
        parent.appendChild(element);
        return element;
    }
}
