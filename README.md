# Restaurant Management System

Java implementation of a restaurant management system demonstrating all attribute types and class extent persistence for PJATK BYT course.

## Quick Start

```bash
# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"

# Run tests (24 tests, all passing)
mvn test
```

## Features

- **All 6 attribute types**: Basic, Complex, Multi-value, Static, Derived, Optional
- **Class extent** with persistence (serialization)
- **32 domain classes**: Customer, MenuItem, Order, Employee, Reservation, Payment, etc.
- **Comprehensive validation** and exception handling
- **100% test coverage** with JUnit 5

## Key Classes

| Class | Highlights |
|-------|-----------|
| `MenuItem` | Complex attribute `nutritionalInfo`, Static `TAX_RATE = 0.23` |
| `Ingredient` | Derived attribute `needsReorder` |
| `Employee` | Derived attributes `yearsOfService`, `isExperienced` |
| `Reservation` | Static `CANCELLATION_WINDOW_HOURS = 4`, Optional `specialRequests` |

## Requirements

- Java 21
- Maven 3.9+
