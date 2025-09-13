# RENTOPS-AI  
**An AI-Integrated Java Desktop Application for Fleet and Rental Operations**  

---

## 📖 Overview  
**RENTOPS-AI** is a **Java-based desktop application** designed to optimize rental car business operations by integrating **intelligent automation** and **AI-driven insights**.  

The system supports:  
- **Fleet management**  
- **Booking and customer handling**  
- **Maintenance tracking**  
- **Reporting and analytics**  

It also leverages **machine learning models** for:  
- Demand prediction  
- Pricing optimization  
- Anomaly detection in vehicle usage  

This project was developed as part of the **B.Tech Computer Science and Engineering curriculum (KTU)** by **Group 2**, aiming to solve real-world challenges in the rental car industry.  

---

## ✨ Features  
- **Fleet Management** – Add, update, and track vehicles with availability status.  
- **Booking Management** – Reservation, cancellation, and customer tracking.  
- **AI-Driven Insights** –  
  - Predict peak rental demand.  
  - Provide dynamic pricing suggestions.  
  - Detect anomalies in vehicle usage.  
- **Customer Records** – Maintain rental history and profiles.  
- **Maintenance Alerts** – Notify for servicing schedules and issues.  
- **Reports and Analytics** – Revenue, fleet utilization, and customer trend analysis.  

---

## 🛠 Technology Stack  
- **Programming Language**: Java (Swing / JavaFX for GUI)  
- **Database**: MySQL (JDBC for connectivity)  
- **Artificial Intelligence**: Integrated ML models for predictions and optimization  
- **Development Tools**: IntelliJ IDEA / Eclipse, Maven, Git  
- **Additional Libraries**: Apache POI (report generation), JFreeChart (data visualization)  

---

## 📂 Project Structure  
```
RENTOPS-AI/
│── src/                     # Java source code
│   ├── ui/                  # GUI components
│   ├── database/            # Database connectivity and queries
│   ├── ai/                  # AI/ML modules
│   ├── models/              # Core business models (Car, Customer, Booking)
│   └── utils/               # Utility classes
│
│── resources/               # Config files, icons, images
│── docs/                    # Documentation and research reports
│── reports/                 # System-generated reports
│── README.md                # Project documentation
│── LICENSE                  # License file
```

## 👥 Team Members

- Aleena Mary Joseph (Team Lead)

- Abhijit P

- Harshitha Hari

- Sween Shaji

- Aravind Lal

## 🎯 Problem Statement

Car rental businesses face recurring challenges:

- Inefficient reservation and cancellation management.

- Lack of predictive models for demand and dynamic pricing.

- Manual and error-prone vehicle maintenance tracking.

- Difficulty in detecting irregular or fraudulent rental activities.

- RENTOPS-AI addresses these challenges by integrating robust software features with AI-powered analytics, creating a reliable and efficient rental management solution.

## ⚙️ Installation and Setup

#Prerequisites

- Java 11 or above

- MySQL 8.0 or above || MariaDB

- Maven (for dependency management)

## Steps

### Clone the repository:

`git clone https://github.com/yourusername/RENTOPS-AI.git`
`cd RENTOPS-AI`


### Configure the database:

### Import the schema from docs/schema.sql.

### Update `db.properties` with database credentials.

### Build and run the project:
```
mvn clean install
mvn exec:java
```

## 📊 Use Cases

- Rental Agencies – Streamline operations and optimize revenue.

- Corporate Fleet Managers – Ensure proper utilization and timely maintenance.

- Startups – Implement AI-driven features in rental services with minimal overhead.

## 📜 License

This project is licensed under the _**MIT License**_ – see the LICENSE file for details.
