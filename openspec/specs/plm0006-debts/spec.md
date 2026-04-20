# Debts

## ADDED Requirements

### Requirement: Debts

The system must be able to make a report of all debts.

#### Scenario: Debts

- GIVEN the use wants to create a report of all debts in JSON format
- WHEN there's a call to query history data on endpoint `/debts`
- THEN the system generates the report

Format:
```
{
 "total_debt": 10000,
 "date": "CURRENT_DATE('DD/MM/YYYY HH:mm:SS')"
 "details": [
  {
   "lender": "John Doe",
   "total": 3000
  },
  {
   "lender": "Jane Doe",
   "total": 2000
  },
  {
   "lender": "Alan Smithee",
   "total": 4000
  },
  {
   "lender": "John Smith",
   "total": 1000
  }
 ]
}
```