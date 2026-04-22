# Generate Excel Report

## ADDED Requirements

### Requirement: Personal Loan Report

The system must be able to create a XLSX report of the user information.

Create the unit tests for each scenario.

Main endpoint: `/reports`.
Specific endpoint: `/reports/excel`.

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/excel`
- THEN the system generates the XLSX report with the layout defined in `report.xlsx`
- AND name it as `personal_load_manager_DDMMYYYY_HHmmSS.xlsx`

### Requirement: Personal Loan Report in Open Document format

The system must be able to create an ODS report of the user information

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/excel?opendoc=true`
- THEN the system generates the XLSX report with the layout defined in `report.ods`
- AND name it as `personal_load_manager_DDMMYYYY_HHmmSS.ods`

## Acceptance Criteria

1. Tests are implemented
2. api/plm.yaml is updated
3. Migrations are created if applicable