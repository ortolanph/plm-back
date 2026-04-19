# Settle a Payment - Basic

## ADDED Requirements

### Requirement: Settle

A settlement is when the user wants to end a debt with a lender. The lender remains, but all his transactions till the day of the settlement are moved from the transaction table.

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
- WHEN the user calls the `/lenders/{lenderId}/settle/{settlementType}`
- THEN the system retrieves the records of all the transactions with the lender
- AND calculates the total
- AND for each transaction records, compose the history record
- AND saves into the history table
- AND delete the transaction record
- AND creates a final record with the total with the settlement type
