# YourMed

YourMed is a web application designed to support patients and doctors in managing medication therapy. The system helps users plan medication intake, track daily doses, receive e-mail reminders, and manage doctor-patient medication assignments.

The application provides separate functionality for patients and doctors, including role-based permissions, medication ownership rules, and doctor-managed prescriptions that cannot be modified by patients.

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [User Roles](#user-roles)
* [Screenshots](#screenshots)
* [Tech Stack](#tech-stack)
* [Database](#database)
* [Requirements](#requirements)
* [Configuration](#configuration)
* [Running the Application](#running-the-application)
* [Project Structure](#project-structure)
* [Repository](#repository)

## Overview

YourMed helps patients organize their daily medication schedule and allows doctors to manage medications for assigned patients. The system supports:

* planning medication intake by dose and time,
* marking doses as taken,
* sending e-mail reminders for scheduled doses,
* managing doctor-patient relationships,
* assigning medications to patients by doctors,
* distinguishing medications added by patients from medications added by doctors,
* blocking patients from editing or deleting doctor-assigned medications.

The application includes the following main sections:

* Home
* Profile
* Status & Overview
* Medication Management
* Doctor Panel
* Settings / Logout

## Features

### Authentication

* User registration and login
* Account type selection: Patient or Doctor
* Password confirmation during registration
* Unique e-mail validation
* Doctor registration with PWZ number verification
* Logout from the settings menu
* Optional e-mail remembering after logout

### Home Page

* Welcome screen
* Medication search feature
* Ability to check whether a medication exists in the dataset/database

### User Profile

* View user profile information
* Edit profile data after clicking the edit button
* Supported profile fields:

  * date of birth,
  * NFZ branch,
  * description.

### Status & Overview

* Daily medication summary
* Number of scheduled doses
* Number of taken doses
* Number of remaining doses
* Daily progress bar
* List of today’s medications with:

  * medication name,
  * dose,
  * scheduled time,
  * status,
  * “Taken” action button.

### Medication Management

Patients can manage medications that they added themselves.

Supported actions:

* view medication details,
* add a new medication,
* edit patient-added medications,
* delete patient-added medications,
* disable or remove reminders for patient-added medications.

Medication details include:

* name,
* dose,
* intake time,
* therapy start date,
* therapy end date,
* status,
* source: Patient or Doctor.

Doctor-added medications are visible to patients but cannot be edited or removed by them.

### Doctor Panel

Doctors have access to a dedicated panel that allows them to:

* view their patient list,
* add patients by e-mail address,
* open a patient’s medication list,
* add medications for selected patients,
* assign medications as doctor-managed.

Doctor-assigned medications are marked with the source `Doctor` and are locked from patient-side editing or deletion.

### E-mail Reminders

The application sends e-mail reminders related to scheduled medication doses.

Reminder behavior:

* reminders are connected with medication schedules,
* reminder e-mails are sent to patients,
* patient-added medication reminders can be disabled or removed by the patient,
* doctor-added medication reminders are protected from patient-side changes.

## User Roles

## Patient

A patient can:

* register and log in,
* edit their profile,
* view today’s medication schedule,
* track daily medication progress,
* mark doses as taken,
* add and manage their own medications,
* delete or disable reminders for their own medications,
* view medications assigned by a doctor.

A patient cannot:

* edit medications added by a doctor,
* delete medications added by a doctor,
* remove reminders for doctor-assigned medications.

## Doctor

A doctor can:

* register and log in,
* register only after providing a valid PWZ number,
* access the doctor panel,
* view a list of assigned patients,
* add a patient by e-mail,
* view a patient’s medication list,
* add medications to a patient’s schedule.

Doctor-added medications are protected from patient-side modification.

## Screenshots

> Add application screenshots here.

### Login Page

![Login Page](docs/screenshots/login.png)

### Registration Page

![Registration Page](docs/screenshots/register.png)

### Home Page

![Home Page](docs/screenshots/home.png)

### User Profile

![User Profile](docs/screenshots/profile.png)

### Status & Overview

![Status and Overview](docs/screenshots/status-overview.png)

### Medication Management

![Medication Management](docs/screenshots/medication-management.png)

### Add Medication Modal

![Add Medication](docs/screenshots/add-medication.png)

### Doctor Panel

![Doctor Panel](docs/screenshots/doctor-panel.png)

### Patient Medication View for Doctor

![Patient Medication View](docs/screenshots/doctor-patient-medications.png)

## Tech Stack

The project uses the following technologies:

* Java 21
* Spring Boot 3.5.7
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* PostgreSQL JDBC Driver
* Spring Security
* Spring Security Crypto
* Spring Mail
* Thymeleaf
* Thymeleaf Extras Spring Security 6
* Spring AI – PostgresML Embeddings
* Apache Commons CSV
* Lombok
* Spring Boot DevTools
* Maven

## Database

The application uses PostgreSQL as a relational database.

Main database tables include:

* `users`
* `medications`
* `reminders`
* `doctor_patients`

The database stores users, medications, medication statuses, reminders, and doctor-patient relationships.

## Requirements

Before running the application, make sure you have installed:

* Java 21
* Apache Maven
* PostgreSQL
* SMTP access, for example Gmail SMTP, for e-mail notifications

PostgreSQL can be installed locally or run inside a Docker container.

## Configuration

Application configuration should be provided in `application.properties`.

### PostgreSQL Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourmed
spring.datasource.username=yourmed_user
spring.datasource.password=yourmed_pass
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Mail Configuration

The application uses SMTP to send medication reminders.

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=[MAIL]
spring.mail.password=[PASSWORD]
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### Application Base URL

```properties
app.base-url=http://localhost:8080
```

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Miyukivv/MedicalApp.git
cd MedicalApp
```

Make sure PostgreSQL is running and the database configuration in `application.properties` is correct.

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

After successful startup, open the application in your browser:

```text
http://localhost:8080
```

## Project Structure

Simplified project structure:

```text
src
├── main
│   ├── java
│   │   └── ...
│   │       ├── controller
│   │       ├── dto
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       └── MedicalApplication.java
│   └── resources
│       ├── data
│       │   ├── medicine_dataset.csv
│       │   └── pwz.csv
│       ├── static
│       │   ├── css
│       │   └── img
│       ├── templates
│       └── application.properties
└── pom.xml
```

## Main Use Cases

The system supports the following main use cases:

* patient registration,
* doctor registration with PWZ verification,
* user login,
* user logout,
* adding a patient to a doctor’s list,
* adding a medication by a patient,
* adding a medication to a patient by a doctor,
* marking a medication dose as taken,
* automatic e-mail reminder sending,
* disabling reminders for patient-added medications,
* blocking patient edits for doctor-added medications,
* checking whether a medication exists in the dataset.

## Medication Ownership Rules

YourMed uses medication source information to control permissions.

| Medication Source | Can Patient Edit? | Can Patient Delete? | Can Doctor Assign? |
| ----------------- | ----------------: | ------------------: | -----------------: |
| Patient           |               Yes |                 Yes |                 No |
| Doctor            |                No |                  No |                Yes |

This ensures that doctor-prescribed medications remain protected from unauthorized patient-side modifications.