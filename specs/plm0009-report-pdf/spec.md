# Generate PDF Report

## ADDED Requirements

### Requirement: Personal Loan Report

The system must be able to create a PDF report of the user information.

Create the unit tests for each scenario.

vMain endpoint: `/reports`.
Specific endpoint: `/reports/pdf`

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/pdf`
- THEN the system generates the HTML report with the layout defined in `report.pdf`
- AND name it as `personal_load_manager_DDMMYYYY_HHmmSS.pdf`

## Acceptance Criteria

1. Tests are implemented
2. api/plm.yaml is updated
3. Migrations are created if applicable