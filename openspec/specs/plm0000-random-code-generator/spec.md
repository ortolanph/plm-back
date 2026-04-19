# Random Code Generator

## ADDED Requirements

### Requirement: Generate Code

The system MUST generate a six number code generator and validate it.

#### Scenario: OTP required
 
- GIVEN a user requests to generate a random code with the `/codes/generate` endpoint
- WHEN there's a call to generate a random code with the user's e-mail
- THEN the system generates a random six number code between 100000 and 999999
- AND checks on the `generated_codes` table to ensure the generated code is unique
- AND the system stores the generated code in the database with a timestamp into the `generated_codes` table
- and the system returns the generated code in the response

### Requirement: Validate Code - Success
 
- GIVEN the `/codes/validate` endpoint is called with the generated code and the e-mail as the body request
- WHEN the system checks that the code exists for that user 
- AND the code is valid
- AND it's between 5 minutes
- THEN the system returns a success response indicating that the code is valid

### Requirement: Validate Code - Failure - Code Not Found
 
- GIVEN the `/codes/validate` endpoint is called with the generated code and the e-mail as the body request
- WHEN the system checks that the code exists
- AND the code is not found in the database
- THEN the system returns a failure response indicating that the code is invalid

### Requirement: Validate Code - Failure - Expired Code

- GIVEN the `/codes/validate` endpoint is called with the generated code and the e-mail as the body request
- WHEN the system checks that the code exists
- AND the code is valid
- AND it's expired (more than 5 minutes)
- THEN the system returns a failure response indicating that the code is no longer valid

### Requirement: Purge Expired Codes

- GIVEN the system has a scheduled task that runs every five minutes
- WHEN the scheduled task executes
- THEN the system deletes all codes from the `generated_codes` table that are older than 5 minutes
