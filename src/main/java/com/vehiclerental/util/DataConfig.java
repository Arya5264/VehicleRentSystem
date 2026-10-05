package com.vehiclerental.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized configuration class managing XML database file resolution,
 * path resolution for Tomcat / Eclipse deployment environments, and template initialization.
 */
public class DataConfig {
    private static final Logger LOGGER = Logger.getLogger(DataConfig.class.getName());

    public static final String USERS_XML = "users.xml";
    public static final String VEHICLES_XML = "vehicles.xml";
    public static final String BOOKINGS_XML = "bookings.xml";

    private static String dataDirectory = null;

    /**
     * Explicitly set the active data directory (e.g. from AppContextListener or system properties).
     */
    public static synchronized void setDataDirectory(String dirPath) {
        if (dirPath != null && !dirPath.trim().isEmpty()) {
            dataDirectory = dirPath.trim();
            File dir = new File(dataDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            LOGGER.info("DataConfig: Data directory configured to: " + dataDirectory);
        }
    }

    /**
     * Retrieves the currently configured data directory.
     */
    public static synchronized String getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Resolves the target XML file across runtime, Eclipse, Tomcat, and standalone environments.
     * Ensures initial default data is seeded if the file does not yet exist.
     *
     * @param fileName the XML file name (e.g., "vehicles.xml", "users.xml", "bookings.xml")
     * @return File object pointing to the existing or newly initialized XML file
     */
    public static synchronized File getXMLFile(String fileName) {
        File targetFile = null;

        // 1. Check explicitly set directory (e.g. via AppContextListener / ServletContext)
        if (dataDirectory != null) {
            File customFile = new File(dataDirectory, fileName);
            if (customFile.exists()) {
                return customFile;
            }
        }

        // 2. Check System property "vehiclerental.data.dir"
        String sysPropDir = System.getProperty("vehiclerental.data.dir");
        if (sysPropDir != null && !sysPropDir.trim().isEmpty()) {
            File sysFile = new File(sysPropDir.trim(), fileName);
            if (sysFile.exists()) {
                return sysFile;
            }
        }

        // 3. Check Tomcat catalina.base / catalina.home locations (for Eclipse WTP and Tomcat standalone)
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            String[] tomcatPaths = {
                    catalinaBase + "/wtpwebapps/VehicleRentalSystem/WEB-INF/data",
                    catalinaBase + "/webapps/VehicleRentalSystem/WEB-INF/data",
                    catalinaBase + "/webapps/ROOT/WEB-INF/data",
                    catalinaBase + "/data"
            };
            for (String tPath : tomcatPaths) {
                File tFile = new File(tPath, fileName);
                if (tFile.exists()) {
                    return tFile;
                }
            }
        }

        // 4. Check relative candidate locations (local project, IDE, or test runner)
        String[] candidateDirs = {
                dataDirectory,
                "data",
                "src/main/webapp/WEB-INF/data",
                "src/main/resources/data",
                "../data",
                "target/VehicleRentalSystem/WEB-INF/data"
        };

        for (String dirStr : candidateDirs) {
            if (dirStr != null && !dirStr.trim().isEmpty()) {
                File candidate = new File(dirStr, fileName);
                if (candidate.exists()) {
                    return candidate;
                }
            }
        }

        // 5. If not found in any existing directory, resolve target directory to create/seed it
        File fallbackDir;
        if (dataDirectory != null) {
            fallbackDir = new File(dataDirectory);
        } else {
            fallbackDir = new File("data");
        }

        if (!fallbackDir.exists()) {
            fallbackDir.mkdirs();
        }

        targetFile = new File(fallbackDir, fileName);
        if (!targetFile.exists() || targetFile.length() == 0) {
            initializeDefaultXML(fileName, targetFile);
        }

        return targetFile;
    }

    /**
     * Seeds initial default XML data if the file is missing or empty.
     */
    public static synchronized void initializeDefaultXML(String fileName, File targetFile) {
        try {
            // A. Check classpath resource
            InputStream is = DataConfig.class.getClassLoader().getResourceAsStream("data/" + fileName);
            if (is == null) {
                is = DataConfig.class.getClassLoader().getResourceAsStream(fileName);
            }

            // B. Check source directories
            if (is == null) {
                File webInfFile = new File("src/main/webapp/WEB-INF/data", fileName);
                if (webInfFile.exists()) {
                    is = Files.newInputStream(webInfFile.toPath());
                }
            }
            if (is == null) {
                File localDataFile = new File("data", fileName);
                if (localDataFile.exists()) {
                    is = Files.newInputStream(localDataFile.toPath());
                }
            }

            if (is != null) {
                Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                is.close();
                LOGGER.info("DataConfig: Seeded XML template from resource for: " + fileName + " at " + targetFile.getAbsolutePath());
                return;
            }

            // C. Fallback: generate minimal valid XML structure
            String rootTag;
            if (VEHICLES_XML.equals(fileName)) {
                rootTag = "vehicles";
            } else if (USERS_XML.equals(fileName)) {
                rootTag = "users";
            } else {
                rootTag = "bookings";
            }

            String initialXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<" + rootTag + ">\n</" + rootTag + ">\n";
            Files.write(targetFile.toPath(), initialXml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            LOGGER.info("DataConfig: Initialized empty XML structure for: " + fileName);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DataConfig: Failed to initialize default XML for " + fileName, e);
        }
    }
}
