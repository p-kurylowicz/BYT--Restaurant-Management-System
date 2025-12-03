# Restaurant Management System

Java implementation of a restaurant management system demonstrating all attribute types and class extent persistence for PJATK BYT course.

## Quick Start

```bash
# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"

# Run tests (266 tests, all passing)
mvn test
```

## Features

- **All 6 attribute types**: Basic, Complex, Multi-value, Static, Derived, Optional
- **All 6 association types**: Basic, Composition, Aggregation, Reflexive, Qualified, Association Classes
- **Reverse connections** on all associations with automatic bidirectional updates
- **Class extent** with persistence (serialization)
- **32 domain classes**: Customer, MenuItem, Order, Employee, Reservation, Payment, etc.
- **Comprehensive validation** and exception handling
- **266 tests passing** with JUnit 5 (53 association-specific tests)

## Attributes

- Complex attribute - NutritionalInfo, ContactInfo
- Multi-value attribute - Reservation, ApplicableItems, Allergens
- Static attribute - Menu Item, Reservation
- Derived attribute - Ingredient, Employee, Order
- Optional attribute - Beverage, ItemQuantity

## Associations (Task 6)

All 6 required association types implemented with reverse connections and comprehensive test coverage (53 tests).

| Association Type | Classes | Multiplicity | Key Features |
|-----------------|---------|--------------|--------------|
| **Basic (1..\*)** | `Customer` ↔ `Order` | 0..1 ↔ 0..* | Reverse connections, duplicate prevention |
| **Composition** | `Order` ↔ `Payment` | 1 ↔ 1 | Mandatory both sides, immutable once set |
| **Aggregation** | `Ingredient` ↔ `Supplier` | 1..* ↔ 0..* | Min multiplicity enforcement, shared parts |
| **Reflexive** | `Manager` ↔ `Manager` | 0..1 ↔ 0..* | Cycle detection, self-reference prevention |
| **Qualified** | `Menu` ↔ `MenuItem` | 0..1 ↔ 0..* | Map-based, qualifier = `name` |
| **Association Class** | `ItemQuantity` | OrderRequest ↔ MenuItem | Factory pattern, delete removes 4 refs |
| **{Bag} Association** | `SupplyLog` | Supplier ↔ Ingredient | Allows duplicates, tracks history |

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
