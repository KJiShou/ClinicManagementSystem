# Clinic Management System

A comprehensive Clinic Management System built as a Java project for Data Structures and Algorithms course. This system manages patients, consultations, pharmacy operations, and duty schedules using custom Abstract Data Type (ADT) implementations.

## Features

- **Patient Management**: Search, register, and manage patient records
- **Consultation System**: Schedule and manage doctor-patient consultations
- **Pharmacy Operations**: Manage medicines, prescriptions, and inventory
- **Duty Schedule**: Track and manage staff duty schedules
- **Reporting System**: Generate comprehensive analytics and operational reports
- **Authentication**: User authentication and role-based access

## Architecture

The system follows a layered MVC (Model-View-Controller) architecture:

### Package Structure

```
src/
├── adt/                    # Custom Abstract Data Type implementations
│   ├── ArrayList.java      # Dynamic array with iterator support
│   ├── HashedDictionary.java # Hash table implementation
│   ├── LinkedQueue.java    # Queue using linked list
│   └── *Interface.java     # ADT interfaces
├── boundary/               # UI layer classes (*UI.java files)
│   └── report/            # Report display and user interaction classes
├── control/               # Business logic controllers (*Control.java files)
│   └── report/            # Report generation and analytics controllers
├── entity/                # Domain models
│   ├── pharmacyManagement/ # Pharmacy-specific entities
│   └── report/            # Report data models and analytics structures
├── dao/                   # Data Access Objects (placeholder)
└── utility/               # Utility classes and data generators
```

### Key Components

- **Main Application Flow**: Entry point through `MainControl.main()` and main menu via `MainUI.mainMenu()`
- **Custom ADTs**: Implementation of ArrayList, HashedDictionary, and LinkedQueue instead of Java Collections
- **Reporting System**: Comprehensive analytics for pharmacy and medical operations
- **Data Storage**: Static dictionaries and lists with auto-generated sample data

## Technology Stack

- **Java**: Version 24
- **Build Tool**: Apache Ant with NetBeans project structure
- **Encoding**: UTF-8
- **Architecture**: MVC Pattern with custom ADT implementations

## Getting Started

### Prerequisites

- Java 24 or higher
- Apache Ant
- NetBeans IDE (recommended)

### Building the Project

```bash
# Build the project
ant -noinput -buildfile build.xml

# Clean build artifacts
ant clean

# Create JAR distribution
ant jar
```

### Running the Application

```bash
# Run the application
ant run
```

The main class is `clinicmanagementsystem.ClinicManagementSystem`.

Alternatively, after building the JAR:
```bash
java -jar dist/ClinicManagementSystem.jar
```

## Custom ADT Implementations

This project implements custom Abstract Data Types instead of using Java Collections Framework:

- **ArrayList<T>**: Dynamic array implementation with iterator support
- **HashedDictionary<K,V>**: Hash table implementation for key-value storage
- **LinkedQueue<T>**: Queue implementation using linked list structure

All ADTs implement their respective interfaces (ListInterface, DictionaryInterface, QueueInterface).

## System Modules

### 1. Patient Management
- Patient registration and profile management
- Medical record tracking
- Patient search functionality

### 2. Consultation System
- Doctor-patient consultation scheduling
- Medical treatment recording
- Consultation history tracking

### 3. Pharmacy Operations
- Medicine inventory management
- Prescription processing
- Sales tracking and analytics

### 4. Duty Schedule Management
- Staff duty schedule tracking
- Schedule assignment and management
- Duty history reporting

### 5. Reporting System
- **Pharmacy Reports**: Inventory Status, Sales Analytics
- **Medical Reports**: Patient Care & Treatment Summary, Operational Efficiency & Revenue
- Comprehensive operational insights and financial performance analytics

## Data Management

- Sample data is automatically generated on startup through utility classes
- UUID-based entity identification system
- In-memory data storage using custom ADT implementations
- Data persistence through DAO pattern (currently minimal implementation)

## Development Workflow

1. Use custom ADT implementations instead of Java Collections Framework
2. Follow MVC pattern: UI classes handle user interaction, Control classes contain business logic
3. Maintain separation between entity models and business logic
4. Use utility classes for data generation and common operations

## Contributing

1. Follow the existing code structure and naming conventions
2. Ensure all new features use custom ADT implementations
3. Maintain proper separation of concerns between layers
4. Add appropriate documentation for new features

## CI/CD

The project includes GitHub Actions workflow configuration:
- Automated builds on Ubuntu using Java 24 with Ant
- Triggers on pushes/PRs to main branch
- Build verification and artifact generation

## License

This project is developed as part of academic coursework for Data Structures and Algorithms.

## Contact

For questions or support regarding this project, please refer to the course materials or contact the development team.
