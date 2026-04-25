# Generate Excel Report

This is a Spring Boot project in the Java programming language. This project uses Gradle to manage dependencies and build the project.

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
- AND name it as `personal_load_manager_YYYYMMDD_HHmmSS.xlsx`

### Requirement: Personal Loan Report in Open Document format

The system must be able to create an ODS report of the user information

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/excel?opendoc=true`
- THEN the system generates the XLSX report with the layout defined in `report.ods`
- AND name it as `personal_load_manager_YYYYMMDD_HHmmSS.ods`

## Acceptance Criteria

1. Tests are implemented
2. API documentation (api/plm.yaml) is updated
3. Migrations are created if applicable
4. Add integration tests at the `integration-tests` folder
5. Consider using Apache POI lib for generating these two formats.

## Tips

1. I've added Spring Boot Starter Freemarker dependency
2. There's a directory for template on the path `src/main/resources/templates`
3. Feel free to use it to store templates
