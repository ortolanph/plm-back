# History deletion

## ADDED Requirements

### Requirement: History deletion

The system must delete history records after one year.

Create the unit tests for each scenario.

### Requirement: Purge Expired History records

- GIVEN the system has a scheduled task that runs every day
- WHEN the scheduled task executes
- THEN the system deletes all history data that are one year old.
