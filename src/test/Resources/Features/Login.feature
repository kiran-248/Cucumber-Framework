Feature: Login Functionality
  @smoke @regression @login
  Scenario Outline: Successful login with valid credentials
    Given User is on the login page
    When User enters valid "<username>" and "<password>"    And User clicks on the login button
    Then User should be navigated to the home page successfully
    Examples:
      | username  | password |
      | Admin     | admin123 |
      | Incorrect | Password |
