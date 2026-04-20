# Generate HTML Report

## ADDED Requirements

### Requirement: Personal Loan Report

The system must be able to create a HTML report of the user information

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/html`
- THEN the system generates the HTML report with the layout defined in report.html
- AND name it as `personal_load_manager_DDMMYYYY_HHmmSS.html`
