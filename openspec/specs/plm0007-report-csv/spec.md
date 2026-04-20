# Generate CSV Report

## ADDED Requirements

### Requirement: Lender's Transaction CSV Report

The system must be able to create a CSV report of the user transaction.

#### Scenario: CSV Transaction of a Lender

- GIVEN the user wants to create a report of the current transactions of a lender in CSV format
- WHEN there's a call to query history data on endpoint `/reports/csv/transactions/${lenderId}`
- THEN the system generates the CSV report ordered by transaction date from older to newest

**Format**

Header:

```
lender_name,transaction_date,transaction_type,transaction_value,transaction_payment_type
```

**Data format**

* `lender_name`: Alphanumeric
* `transaction_date`: DD/MM/YYYY
* `transaction_type`: BORROWED, PAYED, 
* `transaction_value`: 99.999,99
* `transaction_payment_type`: Alphanumeric

**File name**

`transactions_DDMMYYYY_HHmmSS_lender_name.csv`

### Requirement: Lender's History CSV Report

The system must be able to create a CSV report of the user history.

#### Scenario: CSV History of a Lender

- GIVEN the use wants to create a report of the history of a lender in CSV format
- WHEN there's a call to query history data on endpoint `/reports/csv/history/${lenderId}`
- THEN the system generates the CSV report ordered by history date from older to newest

**Format**:

Header:

```
lender_name,history_date,transaction_date,transaction_type,transaction_value,transaction_payment_type,history_type
```

Data format:

* `lender_name`: Alphanumeric
* `history_date`: DD/MM/YYYY
* `transaction_date`: DD/MM/YYYY
* `transaction_type`: BORROWED, PAYED,
* `transaction_value`: 99.999,99
* `transaction_payment_type`: Alphanumeric
* `history_type`: Alphanumeric

**File name**

`history_DDMMYYYY_HHmmSS_lender_name.csv`

### Requirement: Lender's Transactions and History CSV Report

The system must be able to create a ZIP file with the two CSV reports.

#### Scenario: ZIP file generation

- GIVEN the use wants to create the report pack of the current transaction and history of a lender in CSV format
- WHEN there's a call to query history data on endpoint `/reports/csv/all/${lenderId}`
- THEN the system generates the ZIP file including the two reports

**File name**

`plm_csv_reports_DDMMYYYY_HHmmSS_lender_name.zip`