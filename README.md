# Hospital Appointment Booking System

Advanced Internet Programming mini project: a full-stack Java web app using **JDBC**, **Hibernate** and **MySQL (XAMPP)**, deployed on **Tomcat 11**.

## Tools
| Tool | Version |
|---|---|
| JDK | 17 or newer (Tomcat 11 needs 17+) |
| Tomcat | 11 (Jakarta Servlet 6.1, `jakarta.*` packages) |
| NetBeans | Any recent version (open as a Maven project) |
| XAMPP | MySQL/MariaDB module |
| Hibernate | 6.6 (via Maven) |

## Run it
1. Open XAMPP Control Panel and **start MySQL** (Apache is not needed).
2. In NetBeans: **File > Open Project** and choose the `HospitalAppointment` folder. Maven downloads the libraries (needs internet the first time).
3. Register Tomcat 11: **Services > Servers > Add Server > Apache Tomcat**, pick your Tomcat 11 folder.
4. Right-click the project > **Run**. Open `http://localhost:8080/HospitalAppointment/`.

The database `hospital_db` and all tables are created automatically on first start, and demo data is added.

If your NetBeans version cannot register Tomcat 11: right-click the project > **Clean and Build**, then copy `target/HospitalAppointment.war` into `<tomcat>/webapps/` and start Tomcat.

## Demo logins
| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Doctor | `drasha`, `drrohan`, `drpriya` | `doctor123` |
| Patient | register on the sign-in page | your choice |

## Where JDBC and Hibernate are used
| Part | Technology | Files |
|---|---|---|
| Login accounts (`users` table), authentication | **JDBC** (`PreparedStatement`) | `UserDAO`, `DBUtil` |
| Admin dashboard counts and group-by reports | **JDBC** (aggregate SQL) | `ReportDAO` |
| Doctors, patients, appointments (CRUD, relations) | **Hibernate** (annotations, HQL, `@ManyToOne`, `join fetch`) | `model/*`, `DoctorDAO`, `PatientDAO`, `AppointmentDAO`, `HibernateUtil` |

## Features
- Role-based access (admin, doctor, patient) enforced by `AuthFilter`.
- Patients register, book a doctor by date and time slot, and cancel open appointments.
- Double booking of the same doctor, date and slot is blocked.
- Doctors confirm, complete or cancel their own appointments.
- Admin adds or removes doctors and sees totals, status counts and appointments per doctor.
- Passwords are stored as salted PBKDF2 hashes; all SQL uses parameters; output is escaped with `<c:out>`.

## Project structure
```
src/main/java/com/hospital
  model/      User (POJO), Doctor, Patient, Appointment (Hibernate entities)
  dao/        UserDAO, ReportDAO (JDBC) | DoctorDAO, PatientDAO, AppointmentDAO (Hibernate)
  service/    AccountService (creates login + profile together)
  servlet/    AuthServlet, AdminServlet, DoctorServlet, PatientServlet, AuthFilter, AppInit
  util/       DBUtil, HibernateUtil, PasswordUtil, Flash
src/main/resources  hibernate.cfg.xml, db.properties
src/main/webapp     css/style.css, WEB-INF/views/*.jsp, WEB-INF/web.xml
sql/schema.sql      reference copy of the tables
```

## Troubleshooting
- **"Could not reach the database"**: MySQL is not running in XAMPP, or the credentials differ. Change them in both `hibernate.cfg.xml` and `db.properties`.
- **Port 8080 busy**: XAMPP Apache uses 80/443, but another app may use 8080. Change Tomcat's port in its `server.xml`.
- **Using Oracle MySQL instead of MariaDB**: set `hibernate.dialect` to `org.hibernate.dialect.MySQLDialect`.
- **`javax.servlet` errors**: Tomcat 11 only supports `jakarta.*`. Do not add old `javax` libraries.
