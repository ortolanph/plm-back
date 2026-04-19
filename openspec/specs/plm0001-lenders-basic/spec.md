# Random Code Generator

## ADDED Requirements

### Requirement: Lender Management

The system must be able to perform CRU Operations on Lenders object.
A Lender is a person on which lends money to an user. As it is a single user system, there is no field that requires on which user the lender is owned. 
It has name, a bank account number, a phone number, an address (simple line address).

```
*LENDER*
id PK UK UUID
lender_name NN IDX
lender_phone
lender_bank_data
lender_address
```

#### Scenario: Create a Lender

- GIVEN the user informs the name
- AND optionally the phone, the bank data, and the address lne
- WHEN there's a call to create a new lender
- THEN the system creates a new lender

#### Scenario: Update a Lender

- GIVEN the user informs the lender's id
- AND all the data to update the lender's information.
- WHEN there's a call to update a lender
- THEN the system updates the lender

#### Scenario: Query a Lender

- GIVEN the user wants to query a lender by id or by name or part of the name or by phone or a combination or no parameter at all
- WHEN there's a call to query lenders
- THEN the system retrieves the selected lenders or none 

## MODIFIED Requirements

### Requirement: Lender Management

#### Scenario: Query a Lender

- GIVEN the user wants to query a lender by id or by name or part of the name or by phone or part of the phone or a combination or no parameter at all
- WHEN there's a call to query lenders
- THEN the system retrieves the selected lenders or none
