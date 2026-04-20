# Transactions Basic

## ADDED Requirements

### Requirement: Transaction Management

The system must be able to perform CRU Operations on Transactions object.
A Transaction is a record of financial situation. It's immutable. 
It has the lender's id, the date when it happened, the value of the transaction, the type and how the transaction was paid.

```
*TRANSACTION*
id PK UK UUID
id_lender FK(LENDER.id)
transaction_date NN
transaction_value NN
transaction_type (BORROWED, PAYMENT, CANCELLED) NN
transaction_payment_type (MONEY, WIRE_TRANSACTION, PHONE, OTHERS)
```

|     **Type**     | **Actions**                                                                                                    |
|:----------------:|----------------------------------------------------------------------------------------------------------------|
|   **BORROWED**   | When a lender borrows money to the user                                                                        |
|    **PAYED**     | Pays a transaction, but does not modifies it. Creates a transaction payment record. Must inform a payment type |
|  **CANCELLED**   | Cancels a transaction, but does not modifies it. Creates a transaction cancel record                           |

For this requirement will be used **BORROWED**, **PAYMENT**, and **CANCELLED**.

#### Scenario: Create BORROWED Transaction

- GIVEN the user informs the lender's id, the value borrowed, and the payment type
- WHEN there's a call to create a transaction
- THEN the system creates a new transaction containing, the lender id, the date, the value, and the type BORROWED.

#### Scenario: Create PAYED Transaction

- GIVEN the user informs the lender's id, the value that was paid to the lender, and the payment type
- WHEN there's a call to create a transaction
- THEN the system creates a new transaction containing, the lender id, the date, the value, and the type PAYMENT.

#### Scenario: Create CANCELLED Transaction

- GIVEN the user informs the lender's id, and the transaction in the BORROWED or PAYED state
- WHEN there's a call to cancell a transaction
- THEN the system creates a new transaction containing, the lender id, the date, the value of the correlated transaction, and the type CANCELLED

#### Scenario: Query Transactions

- GIVEN the use wants to query transactions by date or date interval 
- OR by value or value interval 
- OR by type 
- OR by a combination of parameters 
- OR no parameter at all
- WHEN there's a call to query transactions  on endpoint `/lenders/{lenderId}/transactions`
- THEN the system retrieves the selected transactions or none

Format:
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
			"value": 25
			"type": "PAYMENT"
		}
	]
}
```
