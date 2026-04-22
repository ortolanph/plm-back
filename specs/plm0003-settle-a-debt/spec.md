# Settle a Debt

## ADDED Requirements

### Requirement: Settle

A settlement is when the user wants to end a debt of a lender. 
The lender remains, but all his transactions till the day of the settlement are moved to the transaction table.

Create the unit tests for each scenario.

```
*TRANSACTION_HISTORY*
id PK UK UUID
history_date
lender_name 
lender_phone
lender_bank_data
transaction_date
transaction_value
transaction_type (BORROWED, PAYMENT, CANCELLED) NN
transaction_payment_type
history_type (PAID_IN_FULL, FORGIVEN)
```

|     **Type**     | **Actions**                                                                |
|:----------------:|----------------------------------------------------------------------------|
| **PAID_IN_FULL** | Paid all the transactions with the full value. Must inform a payment type. |
|   **FORGIVEN**   | The debt was forgiven                                                      |

For this requirement will be used **BORROWED**, **PAYMENT**, and **CANCELLED**.

#### Scenario: Settle the debit

- GIVEN the user wants to settle a debt with a given reason 
- WHEN the user calls the `/lenders/settle` endpoint informing the lenderId, the settlementType, and the paymentType
- THEN the system retrieves the records of all the transactions with the lender
- AND calculates the total
- AND for each transaction records, compose the history record
- AND saves into the history table
- AND delete the transaction record
- AND creates a final record with the total with the settlement type

#### Scenario: Delete a lender

- GIVEN the user wants to delete a lender
- WHEN the user calls DELETE `/lenders/{lenderId}`
- THEN the system will check if there is no open transaction to settle
- AND the system deletes the lender

#### Scenario: Delete a lender - Not Successful

- GIVEN the user wants to delete a lender
- WHEN the user calls DELETE `/lenders/{lenderId}`
- THEN the system will check if there are open transactions to settle
- AND the system does not delete the lender


## Accptance Criteria

1. Tests are implemented
2. api/plm.yaml is updated
3. Migrations are created if applicable