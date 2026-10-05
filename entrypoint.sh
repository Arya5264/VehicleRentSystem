#!/bin/sh
set -e

# Default port to 80 (standard for Vercel container deployment) or use $PORT if provided
TARGET_PORT="${PORT:-80}"

echo "=================================================="
echo " Starting Vehicle Rental Management System"
echo " Apache Tomcat 10.1 on Java 17"
echo " Binding HTTP Connector to port: ${TARGET_PORT}"
echo "=================================================="

# Update Tomcat's server.xml HTTP Connector port dynamically to match $PORT
sed -i "s/<Connector port=\"[0-9]*\"/<Connector port=\"${TARGET_PORT}\"/g" /usr/local/tomcat/conf/server.xml

# Configure JVM system property pointing to the persistent XML data directory
export CATALINA_OPTS="$CATALINA_OPTS -Dvehiclerental.data.dir=/usr/local/tomcat/data"

# Start Apache Tomcat in foreground as PID 1
exec catalina.sh run
