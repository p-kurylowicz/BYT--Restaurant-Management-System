# Restaurant Management System

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-356%20passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue)]()

A comprehensive Java implementation of a restaurant management system demonstrating advanced object-oriented programming concepts for PJATK BYT course. This project showcases complete implementation of all attribute types, association patterns, class extent persistence, and bidirectional relationships with automatic synchronization.

## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/p-kurylowicz/BYT--Restaurant-Management-System.git
cd BYT--Restaurant-Management-System

# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"

# Run all tests
mvn test

# Package as JAR
mvn package
java -jar target/restaurant-management-system-1.0-SNAPSHOT.jar
```

## ✨ Key Features

### Attribute Types (All 6 Required Types)
- ✅ **Basic Attributes**: Standard validated fields (name, email, price, etc.)
- ✅ **Complex Attributes**: Composite objects (`NutritionalInfo`, `ContactInfo`, `Address`)
- ✅ **Multi-value Attributes**: Collections with validation (`allergens`, `specialRequests`, `applicableItems`)
- ✅ **Static Attributes**: Class-level constants (`TAX_RATE = 0.23`, `CANCELLATION_WINDOW_HOURS = 4`)
- ✅ **Derived Attributes**: Computed values (`yearsOfService`, `needsReorder`, `isExperienced`)
- ✅ **Optional Attributes**: Nullable fields with validation (`specialRequests`, `alcoholPercentage`)

### Association Types (All 6 Required Types)
- ✅ **Basic Association (0..\*)**: `Customer` ↔ `Order`
- ✅ **Basic Association (1..\*)**: `Ingredient` ↔ `MenuItem` (constraint: MenuItem requires ≥1 ingredient)
- ✅ **Composition (1 to 1..\*)**: `Order` ↔ `Payment` (lifecycle dependency)
- ✅ **Aggregation (0..\* to 1..\*)**: `Menu` ↔ `MenuItem` (shared ownership)
- ✅ **Reflexive Association**: `Manager` ↔ `Manager` (supervision hierarchy)
- ✅ **Qualified Association**: `Customer` ↔ `Reservation` qualified by `DateTime`
- ✅ **Association Classes**: `ItemQuantity` (OrderRequest-MenuItem), `SupplyLog` (Supplier-Ingredient)

### Advanced Features
- 🔄 **Bidirectional Associations**: All associations maintain reverse connections with automatic synchronization
- 💾 **Class Extent Persistence**: XML serialization for all domain classes with proper error handling
- 🔒 **Encapsulation**: Unmodifiable collections, validated setters, protected extent management
- 🏗️ **Abstract Hierarchies**: `MenuItem`, `Order`, `Employee`, `Payment`, `Discount` with polymorphic behavior
- 📊 **State Management**: Comprehensive enums for workflow control (6 status enums)
- ✅ **Extensive Validation**: 356 passing tests covering all requirements

## 📋 Project Structure

```
BYT--Restaurant-Management-System/
├── src/                          # Source code (40 files)
│   ├── *.java                   # Domain classes (31 classes)
│   ├── *Status.java             # Enums (6 status types)
│   └── Complex types            # NutritionalInfo, ContactInfo, Address
├── test/                        # Test suite (19 test files, 356 tests)
│   ├── *AttributesTest.java     # Attribute validation tests
│   ├── *AssociationTest.java    # Association integrity tests
│   ├── ClassExtentTest.java     # Extent management tests
│   └── ExtentPersistenceTest.java # Serialization tests
├── pom.xml                      # Maven                   # AI assistant instructions
└── README.md                    # This file
```

## 🏛️ Domain Model

### Core Classes (31 Domain Classes)

| Category | Classes |
|----------|---------|
| **Menu Management** | `MenuItem` (abstract), `MainDish`, `Beverage`, `Dessert`, `Ingredient`, `Menu` |
| **Order System** | `Order` (abstract), `DineIn`, `Takeaway`, `OrderRequest`, `ItemQuantity` |
| **Customer Management** | `Customer`, `Reservation`, `Feedback` |
| **Employee Management** | `Employee` (abstract), `Waiter`, `Manager` |
| **Payment Processing** | `Payment` (abstract), `Card`, `Cash`, `Invoice` |
| **Discount System** | `Discount` (abstract), `TimeBasedDiscount`, `VolumeDiscount`, `OrderLevelDiscount`, `ItemLevelDiscount` |
| **Inventory** | `Supplier`, `SupplyLog` |
| **Restaurant** | `Table` |
| **Supporting** | `Address`, `ContactInfo`, `NutritionalInfo`, `Sensory` |

### Enumerations (6 Status Types)
- `OrderStatus`: ACTIVE, COMPLETED, CANCELLED, AWAITING_PAYMENT
- `OrderRequestStatus`: PENDING, CONFIRMED, IN_PREPARATION, READY, SERVED
- `ReservationStatus`: PENDING, CONFIRMED, SEATED, COMPLETED, NO_SHOW, CANCELLED
- `MenuItemAvailability`: AVAILABLE, UNAVAILABLE, PENDING_UPDATE
- `PaymentStatus`: PAID, IN_TRANSACTION, UNPAID
- `TableStatus`: AVAILABLE, OCCUPIED, RESERVED

## 🧪 Test Coverage (356 Tests)

### Attribute Tests (90 tests)
- **BasicAttributesTest** (10 tests): String validation, numeric ranges, null checks
- **ComplexAttributesTest** (10 tests): `NutritionalInfo`, `ContactInfo` validation
- **MultiValueAttributesTest** (12 tests): Collection management, unmodifiable views
- **StaticAttributesTest** (10 tests): `TAX_RATE`, `CANCELLATION_WINDOW_HOURS`
- **DerivedAttributesTest** (13 tests): Computed values, null handling
- **OptionalAttributesTest** (13 tests): Nullable fields with constraints
- **FeedbackAttributesTest** (8 tests): Sensory rating validation
- **InvoiceAttributesTest** (10 tests): Invoice generation logic
- **FeedbackLogicTest** (13 tests): Feedback system business rules

### Association Tests (107 tests)
- **BasicAssociationTest** (5 tests): Customer-Order bidirectional links
- **MenuItemIngredientAssociationTest** (16 tests): 1..* constraint enforcement
- **CompositionAssociationTest** (12 tests): Order-Payment lifecycle
- **AggregationAssociationTest** (14 tests): Menu-MenuItem shared ownership
- **ReflexAssociationTest** (6 tests): Manager supervision hierarchy
- **QualifiedAssociationTest** (14 tests): DateTime-qualified reservations
- **AssociationClassTest** (17 tests): `ItemQuantity`, `SupplyLog` relationship data

### System Tests
- **ClassExtentTest** (12 tests): Extent management, encapsulation
- **ExtentPersistenceTest** (11 tests): XML serialization, error recovery
- **RestaurantSystemTest**: Integration scenarios


## 🏗️ Architecture Patterns

### 1. Two-Level Order System
- **Order** (abstract) contains multiple **OrderRequest** objects
- Supports progressive ordering for dine-in (add items over time)
- Supports batch ordering for takeaway (single request)
- Independent kitchen tracking per OrderRequest

### 2. Abstract Class Hierarchies
All major entities use abstract base classes:
- `MenuItem` → MainDish, Beverage, Dessert (polymorphic menu items)
- `Order` → DineIn, Takeaway (different fulfillment workflows)
- `Employee` → Waiter, Manager (role-specific behavior)
- `Payment` → Card, Cash (payment method variations)
- `Discount` → TimeBasedDiscount, VolumeDiscount, OrderLevelDiscount, ItemLevelDiscount

### 3. Association Classes
Relationship-specific data stored in dedicated classes:
- **ItemQuantity**: OrderRequest-MenuItem quantity, special requests, timestamp
- **SupplyLog**: Supplier-Ingredient supply date, historical cost tracking

## 📖 Business Rules & Constraints

- ✅ MenuItem must have **at least 1 ingredient** (1..* constraint)
- ✅ Maximum **2 waiters per section** (business rule)
- ✅ Waiters can only manage tables in **their assigned section** (access control)
- ✅ Reservations cannot be cancelled within **4 hours** of scheduled time
- ✅ All prices include **23% Polish VAT** (`TAX_RATE`)
- ✅ Takeaway orders require **upfront Card payment**
- ✅ Card payments >500 PLN may have **IN_TRANSACTION** status
- ✅ DineIn orders can use Card or Cash

## 🛠️ Technology Stack

- **Language**: Java 21 (LTS)
- **Build Tool**: Maven 3.9+
- **Testing**: JUnit 5 (Jupiter) with parameterized tests
- **Persistence**: Java XML serialization
- **Version Control**: Git + GitHub


## 👥 Contributors

- Team collaboration via GitHub pull requests


---

