Feature: Configuration management

  Read and set configuration according to properties file.

  Scenario: Load Configuration from a file
    Given A configuration file named "test-config.properties"
    When I create an instance of Configuration
    Then I get the Configuration instance configured with file values.

