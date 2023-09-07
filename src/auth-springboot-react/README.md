## Note

- How to run application

* Create a database like: testdb
* Go to the directory whre pom.xml file exists
* Run mvn spring-boot:run
* Now see corresponding table is created already
* Now insert the following data in role table

```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

## References

- https://springjava.com/spring-boot/how-to-create-a-spring-boot-project-in-vs-code
- https://www.youtube.com/watch?v=CsgtYvlR7xk
- https://www.bezkoder.com/spring-boot-jwt-authentication/
- https://github.com/bezkoder/spring-boot-spring-security-jwt-authentication
- https://github.com/bezkoder/react-jwt-auth
- https://tariqul-islam-rony.medium.com/spring-boot-with-visual-studio-code-visual-studio-code-part-2-7943febb52f8
- [How to import and debug a Spring Boot Java Maven project in VS Code](https://www.youtube.com/watch?v=XJeT0ErXBHo)
- [Form Login and Basic Authentication in springdoc-openapi](https://www.baeldung.com/springdoc-openapi-form-login-and-basic-authentication)
- [Configure JWT Authentication for OpenAPI](https://www.baeldung.com/openapi-jwt-authentication)
- [Swagger 3 with Spring boot 3+ and Spring Security](https://www.youtube.com/watch?v=VYvqF-J2JFc)
