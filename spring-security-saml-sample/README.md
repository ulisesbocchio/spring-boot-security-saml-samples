## Spring Security SAML Sample with Spring Boot ##
This sample uses the plain old spring-security-saml library to add SP capabilities to a Spring Boot app, allowing it to authenticate against different IdPs.
The main purpose of this module is to expose the extensive configuration required to use Spring Security SAML, in comparison with the `spring-boot-security-saml` plugin for Spring Boot, that deals with all this complexities internally.

### Availabe IdPs ####

- [SSO Circle](http://www.ssocircle.com/en/)
- [OneLogin](https://www.onelogin.com/)
- [Ping One Clound] (https://www.pingidentity.com/en/products/pingone.html)
- [OKTA](https://www.okta.com)

### Credentials ###

Use the following credentials:

- *SSO Circle:* Register with [SSO Circle] (http://www.ssocircle.com/en/) and use those credentials to login in the application.
- *OneLogin:* The user must be created in your OneLogin account. See below.  
- *Ping One:* user: dough1234321@gmail.com pass: Test1234!
- *OKTA:* user: dough1234321@gmail.com pass: Test1234!

### OneLogin configuration ###

To use OneLogin with this sample application, you'll have to:
- Create an [OneLogin developers account](https://www.onelogin.com/developer-signup)
- Add a SAML Test Connector (IdP)
- Configure the OneLogin application with:
  - *RelayState:* You can use anything here.
  - *Audience:* localhost-demo
  - *Recipient:* http://localhost:8080/saml/SSO
  - *ACS (Consumer) URL Validator:* ^http://localhost:8080/saml/SSO.*$
  - *ACS (Consumer) URL:* http://localhost:8080/saml/SSO
  - *Single Logout URL:* http://localhost:8080/saml/SingleLogout
  - *Parameters:* You can add additional parameters like firstName, lastName.
- In the SSO tab:
  - *X.509 Certificate:* Copy-paste the existing X.509 PEM cerficate into idp-onelogin.xml (ds:X509Certificate).
  - *SAML Signature algorythm:* Use the SHA-256, although SHA-1 will still work.
  - *Issuer URL:* Replace the entityID in the idp-onelogin.xml with this value.
  - *SAML 2.0 Endpoint (HTTP):* Replace the location for the HTTP-Redirect and HTTP-POST binding in the idp-onelogin.xml with this value.
  - *SLO Endpoint (HTTP):* Replace the location for the HTTP-Redirect binding in the idp-onelogin.xml with this value.
