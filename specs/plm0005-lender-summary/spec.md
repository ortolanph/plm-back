# Lender Summary

## ADDED Requirements

### Requirement: Lender Summary

The system must be able to query the summary of a lender with the transactions and the history.

#### Scenario: Summary

- GIVEN the user wants to query the history
- WHEN there's a call to query history data on endpoint `/lenders/{lenderId}/summary`
- THEN the system retrieves basic lender data
- AND the list of current transactions
- AND the list of related history data
- AND generates the following report

Format
```
{
	"total": 200,
	"lender": "John Doe",
	"date": "CURRENT_DATE('YYYY-MM-DD HH:mm:SS')",
	"transactions": [
		{
			"date": "YYYY-MM-DD",
			"value": 100,
			"type": "BORROWED"
		},
		{
			"date": "YYYY-MM-DD",
			"value": 50,
			"type": "BORROWED"
		},
		{
			"date": "YYYY-MM-DD",
			"value": 50,
			"type": "CANCELLED"
		},
		{
			"date": "YYYY-MM-DD",
			"value": 200,
			"type": "BORROWED"
		}
	],
	"history": [
		{
			"date": "YYYY-MM-DD",
			"value": 100,
			"type": "BORROWED"
		},
		{
			"date": "YYYY-MM-DD",
			"value": 200,
			"type": "BORROWED"
		},
		{
			"date": "YYYY-MM-DD",
			"value": 300,
			"type": "PAID_IN_FULL"
		}
	]
}
```

## Acceptance Criteria

1. Tests are implemented
2. API documentation (`api/plm.yaml`) is updated
3. Migrations are created if applicable
4. Add integration testss at the `integration-tests` folder