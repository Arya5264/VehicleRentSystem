package com.vehiclerental.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

/**
 * Application Lifecycle Listener that initializes the XML data directory
 * and ensures data files are available when deployed to Apache Tomcat.
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        LOGGER.info("Vehicle Rental System initializing in context: " + context.getContextPath());

        // Resolve WEB-INF/data directory
        String realPath = context.getRealPath("/WEB-INF/data");
        if (realPath != null) {
            File dataDir = new File(realPath);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            DataConfig.setDataDirectory(realPath);
            XMLUtil.setDataDirectory(realPath);

            // Ensure essential XML files are present in the runtime folder
            ensureFileExists(dataDir, XMLUtil.USERS_XML, context);
            ensureFileExists(dataDir, XMLUtil.VEHICLES_XML, context);
            ensureFileExists(dataDir, XMLUtil.BOOKINGS_XML, context);
            LOGGER.info("XML data directory configured at: " + realPath);
        } else {
            LOGGER.warning("getRealPath('/WEB-INF/data') returned null. Falling back to local data directory.");
            DataConfig.setDataDirectory("data");
            XMLUtil.setDataDirectory("data");
        }
    }

    private void ensureFileExists(File dir, String fileName, ServletContext context) {
        File file = new File(dir, fileName);
        if (!file.exists() || file.length() == 0) {
            try {
                // Try from WEB-INF/data first
                InputStream is = context.getResourceAsStream("/WEB-INF/data/" + fileName);
                if (is == null) {
                    is = getClass().getClassLoader().getResourceAsStream("data/" + fileName);
                }
                if (is == null) {
                    // Check local project data folder
                    File localSource = new File("data", fileName);
                    if (localSource.exists()) {
                        is = Files.newInputStream(localSource.toPath());
                    }
                }

                if (is != null) {
                    Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    is.close();
                    LOGGER.info("Copied default template for: " + fileName);
                } else {
                    LOGGER.warning("Could not find source template for: " + fileName);
                }
            } catch (Exception e) {
                LOGGER.severe("Failed to initialize file: " + fileName + " - " + e.getMessage());
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Vehicle Rental System context destroyed.");
    }
}
