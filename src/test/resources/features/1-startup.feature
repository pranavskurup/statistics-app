@auto @startuptest
Feature: Check whether application starts without any error
  Scenario: Start application and run health check
    Given Start statistics application
    When Check health endpoint "http://localhost:5050/actuator/health"
    Then Validate health response
