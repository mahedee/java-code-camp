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
