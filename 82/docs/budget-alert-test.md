# Budget Alert Email Feature Test Cases

This document outlines the test cases for the budget alert email functionality, which automatically sends email notifications to POA users when budget usage exceeds warning thresholds.

## Prerequisites

*   Backend application is running on `http://localhost:8081`.
*   MongoDB is running and accessible.
*   A POA user exists in the database with a valid email address (e.g., `yutonghe31@gmail.com`).
*   A patient exists in the database with the POA user as their `poaId`.
*   A budget exists for the patient with categories and sub-elements.
*   `SENDGRID_API_KEY` environment variable is set with a valid SendGrid API key.

## Test Cases

### Test Case 1: Budget Warning Alert (80% Usage)

**Objective**: Verify that a warning email is sent when budget usage reaches 80% of the allocated amount.

**Steps**:

1.  **Create a test POA user (if not already done):**
    ```bash
    curl -X POST http://localhost:8081/api/auth/register \
         -H "Content-Type: application/json" \
         -d '{"firstName": "Test", "lastName": "POA", "email": "yutonghe31@gmail.com", "password": "password123", "role": "POA"}'
    ```

2.  **Create a test patient with the POA user as their POA:**
    ```bash
    curl -X POST http://localhost:8081/api/patients \
         -H "Content-Type: application/json" \
         -d '{"firstName": "Test", "lastName": "Patient", "poaId": "POA_USER_ID", "dateOfBirth": "1990-01-01"}'
    ```
    *(Note: Replace `POA_USER_ID` with the actual ID of the POA user created in step 1.)*

3.  **Create a budget for the patient:**
    ```bash
    curl -X POST http://localhost:8081/api/budgets \
         -H "Content-Type: application/json" \
         -d '{
           "patientId": "PATIENT_ID",
           "totalBudget": 1000.0,
           "categories": [
             {
               "name": "Hygiene Products",
               "budgetAmount": 500.0,
               "subElements": [
                 {
                   "name": "Soap",
                   "subElementBudget": 100.0,
                   "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                 }
               ]
             }
           ]
         }'
    ```
    *(Note: Replace `PATIENT_ID` with the actual ID of the patient created in step 2.)*

4.  **Update monthly usage to trigger 80% warning (80% of 100 = 80):**
    ```bash
    curl -X PUT http://localhost:8081/api/budgets/PATIENT_ID/categories/CATEGORY_ID/sub-elements/SUB_ELEMENT_ID/monthly-usage \
         -H "Content-Type: application/json" \
         -d '{"month": 0, "amount": 80.0}'
    ```
    *(Note: Replace `PATIENT_ID`, `CATEGORY_ID`, and `SUB_ELEMENT_ID` with actual IDs from the budget created in step 3.)*

**Expected Result**:

*   API returns `HTTP 200 OK` with updated budget information.
*   A warning email is sent to `yutonghe31@gmail.com` with the subject "⚠️ CareTrack Budget Alert - WARNING: Budget Nearly Exceeded".
*   The email contains detailed budget information including:
    *   Patient name
    *   Category name
    *   Sub-element name
    *   Budget amount ($100.00)
    *   Current usage ($80.00)
    *   Usage percentage (80.0%)
*   The application logs show "Budget alert email sent to yutonghe31@gmail.com for WARNING level alert".

### Test Case 2: Budget Critical Alert (100% Usage)

**Objective**: Verify that a critical email is sent when budget usage reaches or exceeds 100% of the allocated amount.

**Steps**:

1.  **Perform Test Case 1** to set up the test environment.
2.  **Update monthly usage to trigger 100% critical alert (100% of 100 = 100):**
    ```bash
    curl -X PUT http://localhost:8081/api/budgets/PATIENT_ID/categories/CATEGORY_ID/sub-elements/SUB_ELEMENT_ID/monthly-usage \
         -H "Content-Type: application/json" \
         -d '{"month": 1, "amount": 20.0}'
    ```
    *(Note: This adds $20 to the existing $80, making total usage $100, which is 100% of the $100 budget.)*

**Expected Result**:

*   API returns `HTTP 200 OK` with updated budget information.
*   A critical email is sent to `yutonghe31@gmail.com` with the subject "🚨 CareTrack Budget Alert - CRITICAL: Budget Exceeded".
*   The email contains detailed budget information including:
    *   Patient name
    *   Category name
    *   Sub-element name
    *   Budget amount ($100.00)
    *   Current usage ($100.00)
    *   Usage percentage (100.0%)
*   The application logs show "Budget alert email sent to yutonghe31@gmail.com for CRITICAL level alert".

### Test Case 3: No Alert for Normal Usage

**Objective**: Verify that no email is sent when budget usage is below 80%.

**Steps**:

1.  **Create a new budget with a fresh sub-element:**
    ```bash
    curl -X POST http://localhost:8081/api/budgets \
         -H "Content-Type: application/json" \
         -d '{
           "patientId": "PATIENT_ID",
           "totalBudget": 1000.0,
           "categories": [
             {
               "name": "Food",
               "budgetAmount": 300.0,
               "subElements": [
                 {
                   "name": "Groceries",
                   "subElementBudget": 200.0,
                   "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                 }
               ]
             }
           ]
         }'
    ```

2.  **Update monthly usage to 50% (50% of 200 = 100):**
    ```bash
    curl -X PUT http://localhost:8081/api/budgets/PATIENT_ID/categories/CATEGORY_ID/sub-elements/SUB_ELEMENT_ID/monthly-usage \
         -H "Content-Type: application/json" \
         -d '{"month": 0, "amount": 100.0}'
    ```

**Expected Result**:

*   API returns `HTTP 200 OK` with updated budget information.
*   No email is sent to the POA user.
*   The application logs do not show any budget alert email messages.

### Test Case 4: Bulk Update with Alert

**Objective**: Verify that bulk monthly usage updates also trigger budget alerts.

**Steps**:

1.  **Create a budget with a sub-element (as in Test Case 1).**
2.  **Update monthly usage in bulk to trigger warning:**
    ```bash
    curl -X PUT http://localhost:8081/api/budgets/PATIENT_ID/categories/CATEGORY_ID/sub-elements/SUB_ELEMENT_ID/monthly-usage-bulk \
         -H "Content-Type: application/json" \
         -d '{"monthlyAmounts": [85.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}'
    ```
    *(Note: This sets January usage to $85, which is 85% of a $100 budget, triggering a warning.)*

**Expected Result**:

*   API returns `HTTP 200 OK` with updated budget information.
*   A warning email is sent to the POA user.
*   The application logs show the budget alert email was sent.

### Test Case 5: No POA User Email

**Objective**: Verify that no email is sent when the patient has no POA user or the POA user has no email.

**Steps**:

1.  **Create a patient without a POA:**
    ```bash
    curl -X POST http://localhost:8081/api/patients \
         -H "Content-Type: application/json" \
         -d '{"firstName": "Test", "lastName": "PatientNoPOA", "dateOfBirth": "1990-01-01"}'
    ```

2.  **Create a budget for this patient and update usage to trigger alert:**
    ```bash
    curl -X POST http://localhost:8081/api/budgets \
         -H "Content-Type: application/json" \
         -d '{
           "patientId": "PATIENT_NO_POA_ID",
           "totalBudget": 1000.0,
           "categories": [
             {
               "name": "Test Category",
               "budgetAmount": 100.0,
               "subElements": [
                 {
                   "name": "Test Item",
                   "subElementBudget": 100.0,
                   "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                 }
               ]
             }
           ]
         }'
    ```

3.  **Update usage to trigger alert:**
    ```bash
    curl -X PUT http://localhost:8081/api/budgets/PATIENT_NO_POA_ID/categories/CATEGORY_ID/sub-elements/SUB_ELEMENT_ID/monthly-usage \
         -H "Content-Type: application/json" \
         -d '{"month": 0, "amount": 90.0}'
    ```

**Expected Result**:

*   API returns `HTTP 200 OK` with updated budget information.
*   No email is sent (no POA user exists).
*   The application logs do not show any budget alert email messages.

## Email Content Verification

The budget alert emails should contain the following information:

### Warning Email (80-99% usage):
- **Subject**: "⚠️ CareTrack Budget Alert - WARNING: Budget Nearly Exceeded"
- **Content**: 
  - Greeting with POA user's first name
  - Patient name
  - Sub-element name that triggered the alert
  - Category name
  - Budget amount
  - Current usage amount
  - Usage percentage
  - Warning message about monitoring the budget

### Critical Email (100%+ usage):
- **Subject**: "🚨 CareTrack Budget Alert - CRITICAL: Budget Exceeded"
- **Content**:
  - Greeting with POA user's first name
  - Patient name
  - Sub-element name that triggered the alert
  - Category name
  - Budget amount
  - Current usage amount
  - Usage percentage
  - Critical message about immediate review required

## Notes

*   Budget alerts are only sent when updating monthly usage through the `updateMonthlyUsage` or `updateMonthlyUsageBulk` endpoints.
*   The alert thresholds are: 80% for warning, 100% for critical.
*   Email sending failures are logged but do not affect the budget update operation.
*   The system calculates total usage by summing all monthly usage amounts for the sub-element.
