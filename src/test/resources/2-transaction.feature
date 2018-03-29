@auto
Feature: Check transaction endpoint works fine
  Scenario: Check transaction endpoint
    Given Start statistics application

    When Post empty JSON request body to "http://localhost:5050/transactions"
    Then Validate transaction endpoint response for empty JSON request body

    When Post transaction with amount negative to "http://localhost:5050/transactions"
    Then Validate transaction endpoint response for transaction with amount negative

    When Post transaction with old timestamp to "http://localhost:5050/transactions"
    Then Validate transaction endpoint response for transaction with old timestamp

    When Post transaction with future timestamp with amount "24" to "http://localhost:5050/transactions"
    Then Validate transaction endpoint response for single transaction

    When Post single transaction with valid time and with amount "24" "http://localhost:5050/transactions"
    Then Validate transaction endpoint response for single transaction

    When Post single transaction with amount "24,12,16,34" "http://localhost:5050/transactions"
