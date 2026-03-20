# BankingTransactionSystem

## Overview

This project is a Java-based desktop banking application developed using Swing.
It simulates a real-world banking environment where users can create accounts, securely log in, and perform financial operations such as deposits, withdrawals, transfers, and savings management.

The system was designed with a focus on **clean architecture, modularity, and usability**, separating business logic from the graphical user interface.

---

## Key Features

### Account Management

* User registration with username and password
* Secure login system with validation
* Persistent storage using file serialization (`bank.dat`)

### Core Banking Functionality

* Deposit and withdrawal operations
* Balance tracking with real-time updates
* Transaction history logging
* Money transfer between accounts using account IDs

### Savings Account

* Automatically linked savings account per user
* Monthly interest application (2%)
* Withdrawal limit enforcement (3 per month)
* Separate transaction tracking

### User Interface (GUI)

* Built using Java Swing
* Clean, modern layout with structured sections
* Responsive design with scroll support
* Clear user feedback and error handling

### System Design Improvements

* Separation of concerns (GUI vs business logic)
* Centralised validation in domain classes
* Exception-based error handling
* Thread-safe operations (`synchronized`)
* Scheduled background tasks (interest application)

---

## Project Structure

* `Bank.java`
  Handles system-wide operations such as account creation, login, transfers, and persistence.

* `BankAccount.java`
  Represents a user account, including balance and transaction history.

* `SavingsAccount.java`
  Extends account functionality with interest and withdrawal limits.

* `Transaction.java`
  Represents individual transactions (deposit/withdrawal).

* `BankTransactionSystemGUI.java`
  Handles all user interaction and interface logic.

---

## How to Run the Application (IntelliJ)

### Requirements

* Java JDK 8 or higher
* IntelliJ IDEA (recommended)

---

### Steps

1. **Open the Project**

    * Launch IntelliJ
    * Select *Open* and choose the project folder

2. **Ensure SDK is Set**

    * Go to *File → Project Structure → Project*
    * Set Project SDK to Java 8+ if not already configured

3. **Locate Main Class**

    * Open:
      `BankTransactionSystemGUI.java`

4. **Run the Application**

    * Right-click the file
    * Click **Run 'BankTransactionSystemGUI.main()'**

---

## First-Time Use

* On first run, no accounts exist
* Enter a username and password
* Choose to **register a new account**
* The system will:

    * Create your account
    * Save it to `bank.dat`
* You can then log in using those credentials

---

## Data Persistence

* All data is stored locally in:

  ```
  bank.dat
  ```
* This file is automatically created and updated
* Deleting the file will reset all accounts

---

## Notes

* The system does **not rely on external databases**
* All functionality is self-contained within the application
* Focus has been placed on:

    * Code quality and structure
    * Maintainability
    * User experience

---

