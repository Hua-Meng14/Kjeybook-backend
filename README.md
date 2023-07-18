# *Kjey Book (ខ្ចីសៀវភៅ)*

## Table of Contents

- [Project Introduction](#project-introduction)
- [Project Related Resources](#project-related-resources)
  - [Production Website](#production-website)
  - [Backend repo](#backend-repo)
- [Technologies](#technologies)
- [Project Setup](#project-setup)
- [Team Members](#team-members)

## Project Introduction

In this bootcamp project, our idea is to build a library lending system called "Kjey Book", where the borrower can access our library website to borrow without needed to go to library physically while requesting the book. They able to see the book which is available for borrowing, the book detail and searching for their own favorite book or authors.

Our website also comes with the Admin dashboard where the librarian that has the admin account can view the request from the user, approved or rejected the user's request and able to upload more books by their own on the website.

## Project Related Resources
  
### Production Website

  [![Production](https://img.shields.io/badge/Production%20Website-Click%20Here!-informational?style=flat&logo=vercel)](https://kjeybook.vercel.app/)

### Frontend repo

  [![Frontend Repo](https://img.shields.io/badge/Frontend%20Repo-Click%20Here!-informational?style=flat&logo=github)](https://github.com/incubation-center/B8-FullStack--Website--Group7)

## Technologies

  ![Java](https://img.shields.io/badge/-Java-000000?style=flat&logo=java)
  ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-38B2AC?style=flat&logo=spring-boot)
  ![Postgresql](https://img.shields.io/badge/-Postgresql-007ACC?style=flat&logo=postgresql)

## Project Setup

1. Clone the repo

  ```bash
  git clone git@github.com:Hua-Meng14/Kjeybook-backend.git
  ```

2. Update the `application.properties` file with your credentials:

  ```bash
  # PostgreSQL Database Configuration
  # spring.datasource.url=LOCALHOST_DATASOURCE_CONNECTION
  # spring.datasource.username=DATABASE_USERNAME
  # spring.datasource.password=DATABASE_PASSWORD
  
  # ElephantSQL Database Configuration
  spring.datasource.url=DATABASE_CONNECTION_STRING
  spring.datasource.username=DATABASE_USERNAME
  spring.datasource.password=DATABASE_PASSWORD

  # Hibernate properties
  spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  
  # Log level for root logger
  logging.level.root=DEBUG
  
  # Log level for specific packages or classes
  logging.level.org.springframework=DEBUG
  logging.level.com.bootcamp.bookrentalsystem=DEBUG
  
  spring.profiles.active=dev
  # Email configuration
  huameng.email.sender.host=smtp.gmail.com
  huameng.email.sender.debug=true
  huameng.email.sender.user=SYSTEM_EMAIL_ADDRESS
  huameng.email.sender.password=SYSTEM_EMAIL_ADDRESS_PASSWORD
  
  #Jwt Configuration
  application.security.jwt.secret-key=JWT_SECRETKEY
  # access token
  application.security.jwt.expiration=ACCESS_TOKEN_EXPIRATION
  # refresh token
  application.security.jwt.refresh-token.expiration=REFRESH_TOKEN_EXPIRATION
```

3. Build the application

  ```bash
  mvn clean package
  ```
4. Run the application

```bash
java -jar path/to/your/application.jar
```

6. Open [http://localhost:8080](http://localhost:8080/swagger-ui/index.html) with your browser to see the development site.
