@auto
Feature: Check statistics endpoint works fine
  Scenario: Check statistics endpoint
    Given Start statistics application
    When Check statistics endpoint "http://localhost:5050/statistics"
    Then Validate empty statistics response
    When Post transaction with amount negative to "http://localhost:5050/transactions"
    Then Validate empty statistics response
    When Post transaction with old timestamp to "http://localhost:5050/transactions"
    Then Validate empty statistics response
    When Post transaction with future timestamp with amount "24" to "http://localhost:5050/transactions"
    And Check statistics endpoint "http://localhost:5050/statistics"
    Then Validate empty statistics response
    When Post single transaction with valid time and with amount "24" "http://localhost:5050/transactions"
    And Check statistics endpoint "http://localhost:5050/statistics"
    Then Validate statistics endpoint response for transaction with amount "24"
    When Post single transaction with amount "24,12,16,34" "http://localhost:5050/transactions"
    And Check statistics endpoint "http://localhost:5050/statistics"
    Then Validate statistics endpoint response for multiple transaction with amounts "24,24,12,16,34"
#
#  Scenario: Check transaction endpoint
#    Given Start statistics application
#
#    When Check statistics endpoint "http://localhost:PORT/statistics"
#    Then Then Validate statistic response on startup
#
#    When Post transaction with amount negative to "http://localhost:PORT/transactions"
#    Then Validate statistics endpoint response for transaction with amount negative
#
#    When Post transaction with old timestamp to "http://localhost:PORT/transactions"
#    Then Validate statistics endpoint response for transaction with old timestamp
#
#    When Post transaction with future timestamp to "http://localhost:PORT/transactions"
#    Then Validate statistics endpoint response for transaction with future timestamp
#
#    When Post single transaction with valid time "http://localhost:PORT/transactions"
#    Then Validate statistics endpoint response for single transaction
#
#    When Post multiple transaction with valid time "http://localhost:PORT/transactions"
#    Then Validate statistics endpoint response for multiple transaction
#

    ##When Check statistics endpoint "http://localhost:PORT/statistics"
  ##Then Validate statistic response on startup