Feature: Login to BookMyShow
  The feature will validate login functionality with valid and invalid mobile numbers.

  Background:
    Given User is on home page and select city "Ranchi"

  @ValidLogin
  Scenario Outline: Valid login with mobile and OTP
    When User is on Login Page
    And User enters mobile number "<phone>"
    And Clicks on Continue
    Then Verify Otp page 
    And back to login page
    And close popup
    
    Examples:
    |city  |phone     |
    |Ranchi|8709095528|
    |Patna |6200552705|

  @InvalidLogin
  Scenario Outline: Invalid login with mobile
    When User is on Login Page
    And User enters mobile number "<phone>"
    Then Verify message "<message>"
    And close popup
    
    Examples:
    |phone     |message              |
    |12345678  |Invalid mobile number|

  @UIValidation
  Scenario Outline: Verify all login UI elements are functional and visible
    When User is on Login Page
    Then Verify that mobile number field is visible
    And Verify that Continue button is not visible
    When User enters mobile number "<invalidphone>"
    Then verify continue button is visible but should disable
    When User enters mobile number "<validphone>"
    Then Verify continue button should enabled
    And close popup
    
    Examples:
    |validphone|invalidphone|
    |8709095528|1234567812  |
