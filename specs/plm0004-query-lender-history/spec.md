# Query Lender History

## ADDED Requirements

### Requirement: Query Lender History

The system must be able to query the history of a lender.

#### Scenario: Query History

- GIVEN the user wants to query a lender history by date or date interval
- OR by value or value interval
- OR by type 
- OR by a combination of parameters
- OR no parameter at all
- WHEN there's a call to query history data on endpoint `/lenders/{lenderId}/history`
- THEN the system retrieves the selected history or none

Format
```
{
	"lender": "John Doe",
	"date": "CURRENT_DATE('YYYY-MM-DD HH:mm:SS')",
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
2. API documentation (api/plm.yaml) is updated
3. Migrations are created if applicable