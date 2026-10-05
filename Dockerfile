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

# Build the production WAR package (skipping tests during container build)
RUN mvn clean package -DskipTests

# ==============================================================================
# Stage 2: Runtime Environment with Apache Tomcat 10.1 & Java 17
# ==============================================================================
FROM tomcat:10.1-jdk17-temurin

WORKDIR /usr/local/tomcat

# Clean default sample applications for security and lean deployment
RUN rm -rf webapps/* webapps.dist

# Copy the built WAR from the builder stage
COPY --from=builder /app/target/VehicleRentalSystem.war /tmp/VehicleRentalSystem.war

# Pre-unpack the application into BOTH webapps/VehicleRentalSystem AND webapps/ROOT:
# 1. Unpack into webapps/VehicleRentalSystem so /VehicleRentalSystem/ is fully available
RUN mkdir -p webapps/VehicleRentalSystem && \
    cd webapps/VehicleRentalSystem && \
    jar -xf /tmp/VehicleRentalSystem.war

# 2. Also unpack into webapps/ROOT so the root URL (https://<app>.vercel.app/) works directly
RUN mkdir -p webapps/ROOT && \
    cd webapps/ROOT && \
    jar -xf /tmp/VehicleRentalSystem.war

# 3. Create a common data directory initialized with the baseline XML files
RUN mkdir -p /usr/local/tomcat/data && \
    cp -r webapps/VehicleRentalSystem/WEB-INF/data/. /usr/local/tomcat/data/ && \
    rm -f /tmp/VehicleRentalSystem.war

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
