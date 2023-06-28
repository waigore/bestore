# estore
A set of APIs for a demo order processing system. The APIs are divided into three 
domains: products (`/api/products`), discounts (`/api/discounts`) and orders (`/api/orders`).

## Running this project

This project uses Spring Boot 3.1.0 and requires Java 17 to build and run. 

Clone the repository and execute the below command to compile and start up the application.
Note that on first time run, gradle will take some time to download all required dependencies.

Linux:
```
./gradlew bootRun
```

Windows:
```
gradlew bootRun
```

This will launch an instance of the APIs listening on port 8080 on localhost. To make sure
the application is up and running, simply visit [here](http://localhost:8080) in your browser.

## Running the tests

You can execute the below command to run all tests for this project.

Linux:
```
./gradlew test
```

Windows:
```
gradlew test
```

In addition to executing all tests in the project, a test coverage report is generated at
`build/reports/jacoco/test/html/index.html` under your local `build` directory.

## Accessing the APIs

Please refer to the `E-Store.postman_collection` Postman collection included in the repository
for a sample of all supported API calls.