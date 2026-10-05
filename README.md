# Vehicle Rental Management System

A complete, enterprise-grade Web Technology Lab mini-project developed with **Java 17**, **Jakarta Servlet API 6.0**, **Apache Tomcat 10.1**, **XML & XPath** for persistent data handling, **Vanilla HTML5/CSS3/JavaScript (DOM)** for the frontend, and automated end-to-end testing with **Selenium WebDriver**.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Technology Stack](#technology-stack)
4. [Demonstration of Web Technology Lab Concepts](#demonstration-of-web-technology-lab-concepts)
5. [System Requirements](#system-requirements)
6. [Project Structure](#project-structure)
7. [Environment Configuration](#environment-configuration)
8. [Building the Project](#building-the-project)
9. [Deploying to Apache Tomcat 10.1](#deploying-to-apache-tomcat-101)
10. [Running the Application](#running-the-application)
11. [Demo Credentials](#demo-credentials)
12. [Automated Testing with Selenium WebDriver](#automated-testing-with-selenium-webdriver)
13. [Architecture and Data Design](#architecture-and-data-design)

---

## 1. Project Overview

The **Vehicle Rental Management System** is a self-contained web application designed for a college Web Technology laboratory project. It manages the complete lifecycle of vehicle rentals without relying on heavyweight frameworks (such as Spring Boot, React, or Angular) or external database servers (such as MySQL or PostgreSQL).

Instead, it rigorously demonstrates core standard web technologies:
- Pure **Jakarta Servlets** for HTTP request/response processing and routing.
- Pure **XML files** (`vehicles.xml`, `users.xml`, `bookings.xml`) as data storage.
- Real **W3C XPath queries** (`javax.xml.xpath`) executed directly from Java.
- Rich, interactive **DOM manipulation** in pure Vanilla JavaScript for dynamic vehicle filtering, real-time rental estimation, and modal interaction.
- Pure responsive **Vanilla CSS3** without external CDN frameworks.
- Automated browser testing using **Selenium WebDriver (Java) + JUnit 5**.

---

## 2. Key Features

### Customer Features
- **User Authentication**: Secure registration and session-based login.
- **Customer Dashboard**: Displays available vehicle count, active bookings, completed rentals, and quick navigation.
- **Vehicle Catalog & Search**: Browse all vehicles with live multi-criteria search (keyword, vehicle type, fuel type, price threshold, availability).
- **Vehicle Details View**: Inspect full specifications including brand, model year, fuel type, registration number, and per-day pricing.
- **Dynamic Booking Calculator**: Real-time DOM calculation of rental days and total cost based on pickup and return dates before booking submission.
- **Booking Workflow**: Validation of dates (pickup cannot be in past, return cannot precede pickup), vehicle availability verification, and automatic booking reference generation.
- **Booking Management**: View current bookings, cancel active bookings (releasing vehicle back to `Available`), and review booking history.

### Admin Features
- **Admin Dashboard**: Real-time statistics on total vehicles, available vehicles, rented vehicles, total bookings, and registered customers.
- **Vehicle Management (CRUD)**:
  - Add new vehicles with comprehensive details (ID, Brand, Model, Type, Fuel, Price, Registration Number, Initial Status).
  - Edit existing vehicle details and pricing.
  - Delete vehicles with confirmation modals.
  - Toggle vehicle status (`Available`, `Rented`, `Maintenance`).
- **Booking Management**:
  - View all customer bookings.
  - Approve pending bookings.
  - Mark active rentals as `Completed` (automatically resets vehicle status to `Available`).
  - Cancel bookings (automatically reverts vehicle status to `Available`).
- **Customer Directory**: View all registered customers and their account details.

---

## 3. Technology Stack

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 (LTS) | Core backend programming |
| **Servlet API** | Jakarta Servlet | 6.0.0 | Server-side request handling & session auth |
| **Server** | Apache Tomcat | 10.1.x | Servlet container and HTTP web server |
| **Build Tool** | Apache Maven | 3.8+ / 3.9+ | Dependency management and `.war` packaging |
| **Data Storage**| XML (Extensible Markup Language)| 1.0 | Flat-file structured persistent storage |
| **XML Querying**| W3C XPath (`javax.xml.xpath`) | Standard | Querying, filtering, and locating XML records |
| **Frontend UI** | HTML5 & CSS3 | Standard | Semantic UI, responsive design system |
| **Frontend Logic**| Vanilla JavaScript | ES6+ | Client-side DOM manipulation, validation, live calc |
| **Testing** | Selenium WebDriver + JUnit 5 | 4.20.0 / 5.10.2| Automated end-to-end browser testing |

---

## 4. Demonstration of Web Technology Lab Concepts

This project specifically showcases key curriculum requirements for Web Technology:

### 1. Java (Backend)
- Standard Object-Oriented design with encapsulation, service layers, and models (`Vehicle`, `User`, `Booking`).
- Modern Date-Time API (`java.time.LocalDate`, `java.time.temporal.ChronoUnit`) for safe day difference and date validations.
- Exception handling, synchronized file persistence, and thread-safe XML manipulation.

### 2. Jakarta Servlets (Request/Response & Session Management)
- Annotations: `@WebServlet` and `@WebListener`.
- HTTP Methods: Structured `doGet` and `doPost` implementations.
- Session Management: `request.getSession()` for role-based authentication and URL security filters.
- Content negotiation: Supports both HTML redirects and JSON payload output (`/vehicles?format=json`).

### 3. XML (Data Storage)
- Clean, structured XML representations:
  - `vehicles.xml`: Vehicle repository with schema attributes (`id`, `name`, `brand`, `model`, `type`, `fuel`, `pricePerDay`, `registrationNumber`, `status`).
  - `users.xml`: User accounts with role differentiation (`ADMIN`, `CUSTOMER`).
  - `bookings.xml`: Transactional booking records with timestamps and statuses.
- `XMLUtil.java` uses `javax.xml.parsers.DocumentBuilderFactory` and `javax.xml.transform.Transformer` to parse, modify, and serialize XML safely.

### 4. XPath (XML Querying)
- Real W3C XPath execution implemented in `XPathUtil.java` using `javax.xml.xpath.XPath`:
  - Find vehicle by ID: `/vehicles/vehicle[id='V001']`
  - Find all available vehicles: `/vehicles/vehicle[status='Available']`
  - Find vehicles by category: `/vehicles/vehicle[type='SUV']`
  - Find vehicles under budget: `/vehicles/vehicle[number(pricePerDay) <= 2000]`
  - Complex predicate queries: `/vehicles/vehicle[type='SUV' and status='Available']`
  - Find user by username: `/users/user[username='customer']`
  - Find user bookings: `/bookings/booking[userId='U002']`

### 5. JavaScript DOM (Dynamic UI)
- Located in `src/main/webapp/js/script.js`.
- Meaningful DOM manipulation:
  - `document.getElementById()`, `querySelector()`, `querySelectorAll()`.
  - Dynamic vehicle card creation using `document.createElement()` and `appendChild()`.
  - Real-time rental calculation: Listens to `input` and `change` events on pickup/return dates, calculates difference in days, computes total price, and updates `.textContent` without reloading the page.
  - Live client-side search and filtering across 5 distinct dimensions.
  - Interactive modal dialogs with focus trapping and class toggling.

### 6. Apache Maven (Build Tool)
- Standard Maven directory layout (`src/main/java`, `src/main/webapp`, `src/test/java`).
- `pom.xml` configured with `war` packaging, compilation plugin targeting Java 17, and dependencies (`jakarta.servlet-api` scope provided, Selenium, JUnit 5).

### 7. Apache Tomcat 10.1 (Web Server & Deployment)
- Deploys as `VehicleRentalSystem.war` into Tomcat's `webapps/` folder.
- Listens on `http://localhost:8080/VehicleRentalSystem/`.
- Demonstrates `AppContextListener` (`ServletContextListener`) to guarantee runtime XML storage resolution.

### 8. Selenium WebDriver (Automated Testing)
- End-to-end browser automation in `SeleniumTest.java`.
- 10 automated test cases exercising the entire workflow:
  1. Verify Home Page loads and contains primary brand titles.
  2. Perform Customer Login.
  3. Search for vehicle (e.g., "Creta").
  4. Navigate to Vehicle Details page.
  5. Enter rental pickup and return dates.
  6. Verify dynamic rental amount calculation in DOM.
  7. Confirm and submit booking.
  8. Verify booking confirmation and ID generation.
  9. Perform Admin Login.
  10. Verify Admin Dashboard statistics and access controls.

---

## 5. System Requirements

- **Operating System**: Windows 10/11, Linux, or macOS
- **Java Development Kit (JDK)**: JDK 17 (or higher)
- **Apache Maven**: Version 3.8.0 or higher
- **Servlet Container**: Apache Tomcat 10.1.x
- **Web Browser**: Google Chrome (for Selenium tests)

---

## 6. Project Structure

```
VehicleRentalSystem/
├── pom.xml                                 # Maven build and dependency descriptor
├── README.md                               # Comprehensive project documentation
│
├── data/                                   # XML data files for repository reference
│   ├── users.xml                           # User accounts (Admin & Customer)
│   ├── vehicles.xml                        # Vehicle catalog (8 sample records)
│   └── bookings.xml                        # Booking transactions
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── vehiclerental/
│   │   │           ├── model/              # Domain Model classes
│   │   │           │   ├── User.java
│   │   │           │   ├── Vehicle.java
│   │   │           │   └── Booking.java
│   │   │           ├── service/            # Business Logic Layer
│   │   │           │   ├── UserService.java
│   │   │           │   ├── VehicleService.java
│   │   │           │   └── BookingService.java
│   │   │           ├── servlet/            # Jakarta Servlets (16 endpoints)
│   │   │           │   ├── LoginServlet.java
│   │   │           │   ├── LogoutServlet.java
│   │   │           │   ├── RegisterServlet.java
│   │   │           │   ├── VehicleServlet.java
│   │   │           │   ├── VehicleDetailsServlet.java
│   │   │           │   ├── SearchVehicleServlet.java
│   │   │           │   ├── BookingServlet.java
│   │   │           │   ├── CancelBookingServlet.java
│   │   │           │   ├── MyBookingsServlet.java
│   │   │           │   ├── AdminDashboardServlet.java
│   │   │           │   ├── AddVehicleServlet.java
│   │   │           │   ├── EditVehicleServlet.java
│   │   │           │   ├── DeleteVehicleServlet.java
│   │   │           │   ├── ManageBookingsServlet.java
│   │   │           │   ├── ManageCustomersServlet.java
│   │   │           │   └── CurrentUserServlet.java
│   │   │           └── util/               # XML, XPath & Context Utilities
│   │   │               ├── XMLUtil.java
│   │   │               ├── XPathUtil.java
│   │   │               ├── JsonUtil.java
│   │   │               └── AppContextListener.java
│   │   │
│   │   └── webapp/                         # Web Application Root
│   │       ├── index.html                  # Landing page
│   │       ├── login.html                  # Login page
│   │       ├── register.html               # Registration page
│   │       ├── dashboard.html              # Customer dashboard
│   │       ├── vehicles.html               # Vehicle catalog with filters
│   │       ├── vehicle-details.html        # Vehicle detail page
│   │       ├── booking.html                # Booking & dynamic pricing page
│   │       ├── my-bookings.html            # Customer bookings & cancellation
│   │       ├── admin-dashboard.html        # Admin statistics dashboard
│   │       ├── manage-vehicles.html        # Admin vehicle inventory table
│   │       ├── add-vehicle.html            # Admin add vehicle form
│   │       ├── edit-vehicle.html           # Admin edit vehicle form
│   │       ├── manage-bookings.html        # Admin booking approval/completion
│   │       ├── customers.html              # Admin customer directory
│   │       ├── access-denied.html          # Role unauthorized page
│   │       ├── error.html                  # Generic error handler page
│   │       │
│   │       ├── css/
│   │       │   └── style.css               # Complete responsive stylesheet
│   │       │
│   │       ├── js/
│   │       │   └── script.js               # DOM manipulation & client-side logic
│   │       │
│   │       └── WEB-INF/
│   │           ├── web.xml                 # Web application descriptor
│   │           └── data/                   # Bundled XML data files
│   │               ├── users.xml
│   │               ├── vehicles.xml
│   │               └── bookings.xml
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── vehiclerental/
│                   └── SeleniumTest.java   # 10 automated Selenium test cases
│
└── target/
    └── VehicleRentalSystem.war             # Packaged deployable WAR
```

---

## 7. Environment Configuration

### Verify Java 17
```powershell
java -version
```
Ensure output indicates `openjdk version "17.0.x"` or higher.

### Verify Maven
```powershell
mvn -version
```
Ensure Maven 3.8+ is installed and on the system PATH.

---

## 8. Building the Project

Navigate to the project root directory where `pom.xml` is located:

Execute Maven to compile, package, and generate the WAR artifact:

```powershell
mvn clean package -DskipTests
```

**Expected Build Output:**
```
[INFO] BUILD SUCCESS
[INFO] Building war: .../target/VehicleRentalSystem.war
```

---

## 9. Running and Deploying

### Option A: Running Inside Eclipse IDE (Enterprise Java and Web Developers) - Recommended
You can run the entire application completely within Eclipse without touching Command Prompt:

1. **Import the Project into Eclipse**:
   - Open Eclipse IDE for Enterprise Java and Web Developers.
   - Go to **File** &rarr; **Import...**
   - Select **Maven** &rarr; **Existing Maven Projects** and click **Next**.
   - Browse to the project root directory (containing `pom.xml`).
   - Check the `pom.xml` checkbox and click **Finish**.
   - Eclipse will automatically resolve dependencies and build the workspace.

2. **Configure Apache Tomcat 10.1 in Eclipse**:
   - Open the **Servers** tab (or **Window** &rarr; **Show View** &rarr; **Servers**).
   - Right-click inside the Servers view and select **New** &rarr; **Server**.
   - Expand **Apache** and select **Tomcat v10.1 Server**. Click **Next**.
   - Point the **Tomcat installation directory** to your local Apache Tomcat 10.1 installation.
   - Ensure the JRE is set to **Java 17** (or Java SE 17). Click **Finish**.

3. **Run on Server**:
   - Right-click on the `VehicleRentalSystem` project in the Project Explorer.
   - Select **Run As** &rarr; **Run on Server**.
   - Choose your configured **Tomcat v10.1 Server**, click **Next**, ensure `VehicleRentalSystem` is in the **Configured** list, and click **Finish**.
   - Tomcat 10.1 will launch, the XML databases will initialize, and the web application will automatically open at:
     ```
     http://localhost:8080/VehicleRentalSystem/
     ```

### Option B: Deploying to Standalone Apache Tomcat 10.1

1. **Build the WAR File**:
   ```powershell
   mvn clean package -DskipTests
   ```
2. **Copy the WAR**:
   Copy `target/VehicleRentalSystem.war` into Tomcat's `webapps/` directory:
   - Windows: `Copy-Item target\VehicleRentalSystem.war <TOMCAT_DIR>\webapps\ -Force`
   - Linux/macOS: `cp target/VehicleRentalSystem.war <TOMCAT_DIR>/webapps/`
3. **Start Tomcat**:
   - Windows: `<TOMCAT_DIR>\bin\startup.bat`
   - Linux/macOS: `<TOMCAT_DIR>/bin/startup.sh`
   Tomcat will automatically extract `VehicleRentalSystem.war` and deploy the application.

---

## 10. Running the Application

Open your browser and navigate to:
```
http://localhost:8080/VehicleRentalSystem/
```

### Application URLs:
- **Home Page**: `http://localhost:8080/VehicleRentalSystem/index.html`
- **Login**: `http://localhost:8080/VehicleRentalSystem/login.html`
- **Register**: `http://localhost:8080/VehicleRentalSystem/register.html`
- **Browse Vehicles**: `http://localhost:8080/VehicleRentalSystem/vehicles.html`
- **Customer Dashboard**: `http://localhost:8080/VehicleRentalSystem/dashboard.html`
- **My Bookings**: `http://localhost:8080/VehicleRentalSystem/my-bookings.html`
- **Admin Dashboard**: `http://localhost:8080/VehicleRentalSystem/admin-dashboard.html`
- **Manage Vehicles**: `http://localhost:8080/VehicleRentalSystem/manage-vehicles.html`
- **Manage Bookings**: `http://localhost:8080/VehicleRentalSystem/manage-bookings.html`
- **Customers**: `http://localhost:8080/VehicleRentalSystem/customers.html`

---

## 11. Demo Credentials

The system includes pre-configured accounts in `data/users.xml`:

| Role | Username | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Full administrative control: manage vehicles, approve bookings, view customers |
| **CUSTOMER** | `customer` | `customer123` | Browse catalog, calculate rental estimates, book vehicles, manage reservations |

*New customer accounts can also be created dynamically via the **Register** page.*

---

## 12. Automated Testing with Selenium WebDriver

The project includes an end-to-end automated test suite located at `src/test/java/com/vehiclerental/SeleniumTest.java`.

### Prerequisites for Running Tests:
1. Google Chrome browser installed on the machine.
2. Apache Tomcat running with `VehicleRentalSystem.war` deployed at `http://localhost:8080/VehicleRentalSystem/`.

### Run All Selenium Tests:
```powershell
mvn test
```

### The 10 Automated Test Scenarios:
1. `test01_HomePageLoads`: Verifies the landing page title, hero banner, and primary CTA buttons.
2. `test02_CustomerLogin`: Submits customer credentials (`customer`/`customer123`) and verifies redirection to customer dashboard.
3. `test03_SearchVehicle`: Searches for "Creta" in the live catalog and verifies filter result card display.
4. `test04_OpenVehicleDetails`: Clicks "View Details" on the vehicle card and checks detail specifications.
5. `test05_EnterRentalDates`: Navigates to booking form and populates pickup and return dates.
6. `test06_VerifyRentalAmount`: Verifies real-time JavaScript DOM calculation of rental days and total cost.
7. `test07_ConfirmBooking`: Submits the rental booking form and verifies server response.
8. `test08_VerifyBookingConfirmation`: Verifies that the booking appears in "My Bookings" with valid Booking ID.
9. `test09_AdminLogin`: Authenticates as administrator (`admin`/`admin123`).
10. `test10_AdminDashboard`: Validates admin statistics (vehicles, bookings, customers) and management navigation links.

---

## 13. Architecture and Data Design

### Layered Architecture
```
[Browser / Client]
       │
   HTML5 / CSS3 / JavaScript (DOM Manipulation)
       │ HTTP GET / POST (AJAX / Form Submissions)
       ▼
[Jakarta Servlets 6.0]
 (LoginServlet, BookingServlet, VehicleServlet, AdminDashboardServlet...)
       │
       ▼
[Service Layer]
 (UserService, VehicleService, BookingService)
       │
       ▼
[Utility & Persistence Layer]
 (XPathUtil ───► javax.xml.xpath.XPath)
 (XMLUtil   ───► javax.xml.parsers.DocumentBuilderFactory)
       │
       ▼
[XML Storage Files]
 (vehicles.xml, users.xml, bookings.xml)
```

### XML Schemas
- **`vehicles.xml`**:
  ```xml
  <vehicles>
      <vehicle>
          <id>V001</id>
          <name>Swift</name>
          <brand>Maruti</brand>
          <model>2025</model>
          <type>Hatchback</type>
          <fuel>Petrol</fuel>
          <pricePerDay>1500</pricePerDay>
          <registrationNumber>TN38AB1001</registrationNumber>
          <status>Available</status>
      </vehicle>
  </vehicles>
  ```
- **`users.xml`**:
  ```xml
  <users>
      <user>
          <id>U001</id>
          <name>Admin</name>
          <username>admin</username>
          <password>admin123</password>
          <role>ADMIN</role>
      </user>
  </users>
  ```
- **`bookings.xml`**:
  ```xml
  <bookings>
      <booking>
          <bookingId>BK001</bookingId>
          <userId>U002</userId>
          <customerName>Customer</customerName>
          <vehicleId>V001</vehicleId>
          <vehicleName>Swift</vehicleName>
          <pickupDate>2026-09-20</pickupDate>
          <returnDate>2026-09-22</returnDate>
          <rentalDays>2</rentalDays>
          <pricePerDay>1500</pricePerDay>
          <totalAmount>3000</totalAmount>
          <bookingStatus>Confirmed</bookingStatus>
      </booking>
  </bookings>
  ```

---
*Developed for College Web Technology Lab Demonstration.*
