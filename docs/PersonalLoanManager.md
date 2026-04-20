# Personal Loan Manager API   

## Intent   
To create a list of creditors, the debts I have with them, transactions, histories, and reports.   

## API   
- Lenders   
- Transactions   
- Debts   
- History Data   
- Reports   
   
## Data Structure   

```
*LENDER*:
id PK UK UUID
lender_name NN IDX
lender_phone
lender_bank_data

*TRANSCTION*
id PK UK UUID
id_lender FK(LENDER.id)
transaction_date NN
transaction_value NN
transaction_type (BORROWED, PAYMENT, CANCELLED) NN
transaction_payment_type (MONEY, WIRE_TRANSACTION, PHONE, OTHERS)

*HISTORY*
id PK UK UUID
history_date
lender_name 
lender_phone
lender_bank_data
transaction_date
transaction_value
transaction_payment_type
history_type (BORROWED, PAYMENT, CANCELLED, PAID_IN_FULL, FORGIVEN)
```

## Lenders   
Those who borrow money. The money owners.   
   
Root Endpoint: `/lenders` .   
   
Lenders could be:   
1. Created   
2. Updated   
3. Queried   
    1. By Id   
    2. By name or part of name   
    3. By phone   
    4. Combining Parameters   
    5. No parameters   
4. Erased   
    1. Only when all the transactions are paid in full or forgiven   
5. Debt Settlement `(POST /lenders/{lenderId}/settle/{settlementType}`)   
6. List all the transactions `(GET /lenders/{lenderId}/transactions`)   
7. List the lender history`(GET /lenders/{lenderId}/history`)   
8. Summary (total debt + transactions + history) `(GET /lenders/{lenderId}/summary`)   
   
   
When erased:   
1. System verifies if there are open transactions for that lender `(/lenders/{lenderId}/transactions`)   
2. UI asks for:   
    1. If the user wants to settle the debt Paying in Full `(/lenders/{lenderId}/settle`)   
        ```
        Payload example
        {
        	"transaction_type": "PAID_IN_FULL",
        	"transaction_payment_type": "MONEY"
        }
        ```
    2. If the user wants to settle the debt Forgiving the Debt (`/lenders/{lenderId}/settle`)   
        ```
        Payload example
        {
        	"transaction_type": "FORGIVEN",
        	"transaction_payment_type": ""
        }
        ```
    3. If the users wants only to cancel   
        1. Do not delete the lender record   
   
### Debt Settlement

Root Endpoint: `POST /lenders/{lenderId}/settle`

Payload: 

1. User informs the type of settlement and how it was settled   
2. Payload example
{
	"transaction\_type": "PAID\_IN\_FULL",
	"transaction\_payment\_type": "MONEY"
}   
3. System calculates user's total debt for that lender   
4. System fetches user's information   
5. System moves transaction records to the history table   
6. System creates a settlement record with the kind and payment mode   
7. Lender record is kept   
   
### List all transactions   
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

### History of a Lender   
```
{
	"lender": "John Doe",
	"date": "CURRENT_DATE('DD/MM/YYYY HH:mm:SS')",
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
### Summary   
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
## Transactions   
The immutable record of money transactions.    
   
Root Endpoint:` /transactions` 

A transaction can be:   
1. Created   
2. Queried   
    1. By Date or Interval   
    2. By Value or Interval   
    3. Combination of parameters   
    4. No parameters   
   
   
### Transaction Types:   
   
|     **Type**     | **Actions**                                                                                                    |
|:----------------:|----------------------------------------------------------------------------------------------------------------|
|   **BORROWED**   | When a lender borrows money for the user                                                                       |
|    **PAYMENT**   | Pays a transaction, but does not modifies it. Creates a transaction payment record. Must inform a payment type |
|   **CANCELLED**  | Cancels a transaction, but does not modifies it. Creates a transaction cancel record                           |
| **PAID_IN_FULL** | Paid all the transactions with the full value. Must inform a payment type.                                     |
|   **FORGIVEN**   | The debt was forgiven                                                                                          |

**NO TRANSACTION IS DELETED OR MODIFIED**   
While the user does not pay in full a lender, no transaction is moved to the history.   
## Debts   
Calculate all the lenders debts and shows up their summary records.   
   
Root Endpoint:` /debts`   
   
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
## History   
Shows the historic data of transactions. History is kept for one year.   
   
Root Endpoint:` /history`   
   
History data is:   
1. Queried:   
    1. By date or date interval   
    2. Lender (id, name or part of name)   
    3. Operation type   
   
   
### Key Points   
- History is generated when there's a settlement   
- History is stored for one year by default   
   
   
## Reports   
Generate CSV, HTML, Excel, and PDF reports.   
   
Root Endpoint:` /reports`    
   
### CSV   
Generates CSV file for transactions and for history data.   
   
Key Endpoint:` /reports/csv`    
   
**Parameters**:   
- `OPT` Date period (endDate and startDate)   
- `OPT` Lender (id, name, name part)   
   
   
Omitting the parameters means everything.   
   
Alternate endpoints:
- `/reports/csv/transactions`
    - All or selected transactions
    - File name:` transactions_DDMMYYYY_HHmmSS_lender_name.csv`
    - Header and Layout: `lender_name,transaction_date,transaction_name,transaction_type,transaction_value,transaction_payment_type`    
- `/reports/csv/history`
    - All or selected transactions
    - File name:` history_DDMMYYYY_HHmmSS.csv`
    - Header and Layout: `lender_name,history_date,transaction_date,transaction_name,transaction_type,transaction_value,transaction_payment_type`

### HTML
Generates an HTML report for one lender.

Root Endpoint: `/reports/html/{lenderId}`


### EXCEL   
Generate an Excel Workbook with transactions sheets for every or selected lenders and history data.   
   
Root Endpoint: `/reports/excel`    
   
**Parameters**:   
- `OPT` Date period (endDate and startDate)   
- `OPT` Lender (id, name, name part)   
   
   
Omitting the parameters means everything.   
   
Workbook Structure:   
- Lender Sheet   
    - Tab Title: `${lender_name}`    
    - follow the link to the layout   
- History Sheet   
    - Tab Title: `Transaction History`    
    - follow the link to the layout   
   

### PDF   
Generates a summary PDF file of all data.   
   
Root Endpoint: `/reports/pdf`    
   
Parameters:   
- No parameters   
      
PDF File Layout:   
- Title Page with the application name   
- Table of Contents Page   
- A page for each lender and active transactions   
- A page with all active history data   
  
## Tech Stack   
1. Spring Boot   
2. Spring Boot JPA   
3. PostGRESQL   
