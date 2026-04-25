# Generate PDF Report

This is a Spring Boot project in the Java programming language. This uses gradle to manage depenencies and build the project.

## ADDED Requirements

### Requirement: Personal Loan Report

The system must be able to create a PDF report of the user information.

Create the unit tests for each scenario.

vMain endpoint: `/reports`.
Specific endpoint: `/reports/pdf`

#### Scenario: Report Generation

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/pdf`
- THEN the system generates the PDF report with the layout defined in `report.pdf`
- AND name it as `personal_load_manager_YYYYMMDD_HHmmSS.pdf`

#### Scenario: Report Generation in Microsoft Word format

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/pdf?word=true`
- THEN the system generates the Microsoft Word DOCX report with the layout defined in `report.docx`
- AND name it as `personal_load_manager_YYYYMMDD_HHmmSS.docx`

#### Scenario: Report Generation in Open Document format

- GIVEN the user wants to create the personal load manager report
- WHEN there's a call to query history data on endpoint `/reports/pdf?opendoc=true`
- THEN the system generates the Open Document report with the layout defined in `report.odt`
- AND name it as `personal_load_manager_YYYYMMDD_HHmmSS.oft`

## Acceptance Criteria

1. Tests are implemented
2. API documentation (api/plm.yaml) is updated
3. Migrations are created if applicable
4. Add integration tests at the `integration-tests` folder
5. For generating PDF consider using itext library
6. For generating DOC and ODT consider using Apache POI

## Tips

1. I've added Spring Boot Starter Freemarker dependency
2. There's a directory for template on the path `src/main/resources/templates`
3. Feel free to use it to store templates
