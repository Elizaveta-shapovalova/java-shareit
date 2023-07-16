## Shareit :mag_right:

### _Introduction:_

The application allows users to share things and rate the quality of services. Only registered users can upload things to the platform, as well as rent them.

As for the technical part, the application is based on a microservice architecture: validation part, main part and database are different services that have their own clients to communicate with each other and connect using Docker.

### _Development:_
 
 -  Java 11
 -  To work with the database used: Hibernate framework , [Query](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/server/src/main/java/ru/practicum/shareit/item/repository/ItemRepository.java) for more complex requests.
 - [Mappers](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/server/src/main/java/ru/practicum/shareit/item/mapper/ItemMapper.java) have been moved to separate static classes.
 - For validation was used: Lombok with [Interfaces](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/gateway/src/main/java/ru/practicum/shareit/item/dto/ItemRequestDto.java) for separation of field validation and custom [Annotations](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/gateway/src/main/java/ru/practicum/shareit/annotation/CheckDateValidator.java).
 - A custom [Converter](https://github.com/Elizaveta-shapovalova/java-shareit/tree/main/server/src/main/java/ru/practicum/shareit/converter) was created in the Spring Congig level to avoid code duplication.
 - For testing app was used: JUnit, [Mockito](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/server/src/test/java/ru/practicum/shareit/booking/service/BookingServiceImlTest.java) and [Jackson](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/server/src/test/java/ru/practicum/shareit/booking/dto/BookingDtoTest.java).
 - Also, n-request to the database and to other modules were excluded.
 - [Configurations](https://github.com/Elizaveta-shapovalova/java-shareit/blob/main/server/src/main/java/ru/practicum/shareit/JpaAuditingConfiguration.java) have been moved to a separate class so that there is no conflict with Mockito.
 - Dependencies were connected using Maven.

---

### _Staсk:_

<div>
  <img src="https://github.com/devicons/devicon/blob/master/icons/java/java-original-wordmark.svg" title="Java" alt="Java" width="40" height="40"/>&nbsp;
  <img src="https://github.com/devicons/devicon/blob/master/icons/spring/spring-original-wordmark.svg" title="Spring" alt="Spring" width="40" height="40"/>&nbsp;
  <img src="https://github.com/devicons/devicon/blob/master/icons/postgresql/postgresql-original.svg" title="PostgreSQL" alt="PostgreSQL" width="40" height="40"/>&nbsp;
  <img src="https://github.com/devicons/devicon/blob/master/icons/apache/apache-original.svg" title="Maven" alt="Maven" width="40" height="40"/>&nbsp;
  <img src="https://github.com/devicons/devicon/blob/master/icons/docker/docker-original.svg?short_path=bbeaed2" title="Docker" alt="Docker" width="40" height="40"/>
</div>





