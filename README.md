# Restaurant Management System

Java implementation of a restaurant management system demonstrating all attribute types and class extent persistence for PJATK BYT course.

## Quick Start

```bash
# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"

# Run tests (293 tests, all passing)
mvn test
```

## Features

- **All 6 attribute types**: Basic, Complex, Multi-value, Static, Derived, Optional
- **All 6 association types**: Basic, Composition, Aggregation, Reflexive, Qualified, Association Classes
- **Reverse connections** on all associations with automatic bidirectional updates
- **Class extent** with persistence (serialization)
- **32 domain classes**: Customer, MenuItem, Order, Employee, Reservation, Payment, etc.
- **Comprehensive validation** and exception handling
- **293 tests passing** with JUnit 5 (77 association-specific tests)

## Attributes

- Complex attribute - NutritionalInfo, ContactInfo
- Multi-value attribute - Reservation, ApplicableItems, Allergens
- Static attribute - Menu Item, Reservation
- Derived attribute - Ingredient, Employee, Order
- Optional attribute - Beverage, ItemQuantity

## Associations (Task 6)

All 6 required association types implemented with reverse connections and comprehensive test coverage (75 tests).

| Association Type | Classes |
|-----------------|---------|
| **Basic (0..\*)** | `Customer` ↔ `Order` |
| **Basic (1..\*)** | `Ingredient` ↔ `MenuItem` |
| **Composition (1 to 1..\*)** | `Order` ↔ `Payment` |
| **Aggregation (0..\* to 1..\*)** | `Menu` ↔ `MenuItem` |
| **Reflexive** | `Manager` ↔ `Manager` |
| **Qualified** | `Customer` (0..1) ↔ (1) `Reservation` (qualifier: DateTime) |
| **Association Class** | `ItemQuantity` (OrderRequest ↔ MenuItem) |
| **{Bag} Association** | `SupplyLog` (Supplier ↔ Ingredient) |

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
