#!/bin/sh
set -e

# Default port to 80 (standard for Vercel container deployment) or use $PORT if provided
TARGET_PORT="${PORT:-80}"

echo "=================================================="
echo " Starting Vehicle Rental Management System"
echo " Apache Tomcat 10.1 on Java 17"
echo " Binding HTTP Connector to port: ${TARGET_PORT}"
echo "=================================================="

# Update Tomcat's server.xml HTTP Connector port dynamically to $PORT
sed -i "s/<Connector port=\"[0-9]*\"/<Connector port=\"${TARGET_PORT}\"/g" /usr/local/tomcat/conf/server.xml

# Ensure webapps and data directory have write permissions
mkdir -p /usr/local/tomcat/webapps/VehicleRentalSystem/WEB-INF/data

# Start Apache Tomcat in foreground
exec catalina.sh run
