# ==============================================================================
# Stage 1: Build the Maven WAR package using Java 17
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and dependency manifests
COPY pom.xml .

# Pre-fetch dependencies to leverage Docker layer caching
RUN mvn dependency:go-offline -B || true

# Copy project source code and initial XML data files
COPY src ./src
COPY data ./data

# Build the production WAR package skipping tests during container build
RUN mvn clean package -DskipTests

# ==============================================================================
# Stage 2: Runtime Environment with Apache Tomcat 10.1 & Java 17
# ==============================================================================
FROM tomcat:10.1-jdk17-temurin

WORKDIR /usr/local/tomcat

# Clean default sample applications for security and lean deployment
RUN rm -rf webapps/* webapps.dist

# Copy the built WAR to Tomcat webapps/ as VehicleRentalSystem.war
# Tomcat automatically extracts this to /VehicleRentalSystem/
COPY --from=builder /app/target/VehicleRentalSystem.war webapps/VehicleRentalSystem.war

# Create a clean ROOT redirect so visiting https://<app-name>.vercel.app/
# automatically routes visitors to /VehicleRentalSystem/
RUN mkdir -p webapps/ROOT && \
    echo '<!DOCTYPE html><html><head><meta http-equiv="refresh" content="0;url=/VehicleRentalSystem/"><script>window.location.replace("/VehicleRentalSystem/");</script><title>Redirecting to Vehicle Rental System...</title></head><body><p>Redirecting to <a href="/VehicleRentalSystem/">Vehicle Rental System</a>...</p></body></html>' > webapps/ROOT/index.html

# Copy container entrypoint script and fix Windows CRLF line endings
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN sed -i 's/\r$//' /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh

# Vercel containers default to port 80 unless dynamically injected via $PORT
ENV PORT=80

# Expose HTTP port
EXPOSE 80

# Execute entrypoint script to dynamically bind Tomcat to $PORT and start catalina
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
