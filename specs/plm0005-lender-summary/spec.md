# Lender Summary

## ADDED Requirements

### Requirement: Lender Summary

The system must be able to query the summary of a lender with the transactions and the history.

Create the unit tests for each scenario.

#### Scenario: Summary

- GIVEN the user wants to create a summary report in JSON format
- WHEN there's a call to query history data on endpoint `/lenders/{lenderId}/summary`
- THEN the system generates the report

Format
```
{
	"total": 200,
	"lender": "John Doe",
	"date": "CURRENT_DATE('DD/MM/YYYY HH:mm:SS')",
	"transactions": [
		{
			"date": "DD/MM/YYYY",
			"value": 100,
			"type": "BORROWED"
		},
		{
			"date": "DD/MM/YYYY",
			"value": 50,
			"type": "BORROWED"
		},
		{
			"date": "DD/MM/YYYY",
			"value": 50,
			"type": "CANCELLED"
		},
		{
			"date": "DD/MM/YYYY",
			"value": 200,
			"type": "BORROWED"
		}
	],
	"history": [
		{
			"date": "DD/MM/YYYY",
			"value": 100,
			"type": "BORROWED"
		},
		{
			"date": "DD/MM/YYYY",
			"value": 200,
			"type": "BORROWED"
		},
		{
			"date": "DD/MM/YYYY",
			"value": 300,
			"type": "PAID_IN_FULL"
		}
	]
}
```

## Acceptance Criteria

1. Tests are implemented
2. api/plm.yaml is updated
3. Migrations are created if applicable