## Spring Boot Security SAML Sample with Auth0 as IDP ##

Run using the run script:

```bash
./run.sh
```
That will create a Docker container using `java version "1.8.0_77"` since the signature certificate provided by Auth0 can not be read with later versions
of Java for some strange reason.
If you're running `java version "1.8.0_77"` in your local machine you can run this app like any other Spring Boot App.