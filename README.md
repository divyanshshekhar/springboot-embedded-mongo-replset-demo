# Spring Boot Embedded Mongo with Replica Set

This project defines a basic rest service that allows users to update records of a Person (name and email). It uses
mongodb as database.
<br/>
It also contains a Junit test for the `delete person API`. The test case first creates a Person in the database and
calls the PersonsService's delete method to delete the person, and checks if the person was actually deleted or not.
<br/>
To run the tests, flapdoodle's embedded mongo has been added as dependency with test scope.

## Understanding the demo project

1. Checkout the medium article
   at [https://dsgaur.medium.com/how-to-enable-replica-set-in-embbedded-mongo-with-spring-boot-ddeaa079c1c8](https://dsgaur.medium.com/how-to-enable-replica-set-in-embbedded-mongo-with-spring-boot-ddeaa079c1c8)
2. The source code should be easy to understand. Necessary comments are provided where needed.
3. Commit messages are descriptive. Check the full commit message (not only the first line). They explain the changes in
   paragraph.
3. The git commits are tagged so that you can jump to various stages of the implementation as explained in the article.
   The git tags are in following order:
    1. **basic-service-with-tests**: Basic service implementation (with Junit tests) without using mongo transactions.
    2. **mongo-txn-with-failing-tests**: Enabled mongo transactions in the service but now the test cases fails due to
       lack of replica set in embedded mongo
    3. **repl-set-without-journaling**: Enabled replica set using spring properties but journaling is not enabled
    4. **repl-set-with-journaling**: Defined custom `MongodConfig` to enable journaling
    5. **repl-set-initiate-and-wait-for-ready**: Defined methods in `UnitTestConfig` to initiate replica set and wait
       for it to get ready
    6. **cleanup-unnecessary-code**: Final version of the project after cleaning up unnecessary `@AutoConfigureBefore`
       and logging levels.

## How to run

### Run the service

1. Run mongo container on your local by running commands
    ```bash
    cd docker/mongodb
    docker compose build mongo
    docker compose up -d
    ```
2. Run following command from root project directory to generate application jar file:
    ```bash 
    mvn clean package -DskipTests
    ``` 
3. Run following command to start the application:
   ```bash
   java -jar target/spring-embedded-mongo-demo-1.0-SNAPSHOT.jar
   ```
4. Import the Postman Collection (`docs/postman_collection.json`) into your Postman and play with it!

### Run the unit tests

Run following command from root project directory:

```bash
mvn clean test
```

Note: Unit tests don't need running your application or the mondodb container. They are in fact "unit tests".
