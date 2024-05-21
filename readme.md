# Hospital Management System

This project is a simple hospital management system implemented using Spring Boot with Hibernate and JPA, utilizing gRPC for communication. It allows for the storage and retrieval of information about patients.

## Features

- **Hospital Management**: Create, modify, and delete hospitals.
- **Patient Management**: Create, modify, and delete patients.
- **Patient Registration**: Register patients in multiple hospitals.
- **Patient Listing**: List all patients of a hospital.
- **Hospital Listing**: List all hospitals a patient has been registered in.

## Technology Stack

- **Java 17**
- **Spring Boot 2.6.13**
- **Hibernate & JPA**
- **gRPC**
- **H2 In-Memory Database**
- **Gradle**

## Setup and Running the Application

1. **Clone the Repository**
   ```sh
   git clone https://github.com/oguzkaanimamoglu/hospital-system.git
   cd hospital-system
   ```

2. **Build the Project**

    Ensure you have Java 17 and Gradle installed. Run the following command to build the project:
   ```sh
    ./gradlew build
   ```
3. **Run the Application**
    
    Use the following command to start the application:
    ```sh
    ./gradlew bootRun
    ```
4. **Access the Application**

    The Spring Boot application will start on http://localhost:8080 and gRPC server will start on http://localhost:9090

# Usage

'grpcurl' is the recommended tool for interacting with the gRPC server. For more info you can visit [grpcurl](https://github.com/fullstorydev/grpcurl) and can learn how to install it based on your system.

## Create a Hospital

```sh
grpcurl -plaintext -d "{\"name\": \"LMU Klinikum\"}" localhost:9090 HospitalService/CreateHospital 
```

## Create a Patient

```sh
grpcurl -plaintext -d "{\"name\": \"Lebron James\"}" localhost:9090 HospitalService/CreatePatient 
```


## Registering a Patient to a Hospital

```sh
grpcurl -plaintext -d "{\"patient_id\": 1, \"hospital_id\": 1}" localhost:9090 HospitalService/RegisterPatient
```

## List Patients of a Hospital
```sh
grpcurl -plaintext -d "{\"hospital_id\": 1}" localhost:9090 HospitalService/ListPatientsOfHospital
```

## List Hospitals of a Patient
```sh
grpcurl -plaintext -d "{\"patient_id\": 1}" localhost:9090 HospitalService/ListHospitalsOfPatient 
```

## Deleting Patients and Hospitals
```sh
grpcurl -plaintext -d "{\"id\": 1}" localhost:9090 HospitalService/DeletePatient
grpcurl -plaintext -d '{"id": 1}' localhost:9090 com.example.hospital.HospitalService/DeleteHospital 
```


## Modifying Hospitals and Patients

```sh
grpcurl -plaintext -d "{\"id\": 1, \"name\": \"Updated Hospital\"}" localhost:9090 HospitalService/ModifyHospital
grpcurl -plaintext -d "{\"id\": 1, \"name\": \"Lionel Messi\"}" localhost:9090 HospitalService/ModifyPatient 
```


## List all Hospitals and Patients

```sh
grpcurl -plaintext localhost:9090 HospitalService/ListAllHospitals  
grpcurl -plaintext localhost:9090 HospitalService/ListAllPatients
```

# Special Request: Average Age Report

To fulfill the director's special request for an overview of the average age of patients by sex and per month for the past 10 years, with a response time of less than 200ms, the following steps were implemented:

## 1. Data Modeling and Storage

### Database Schema

A relational database schema was designed to include tables for patients, visits, and hospitals. The schema is structured to ensure that all relevant data about patient visits is captured and can be efficiently queried.

### Indexing

Indexes were created on key columns such as visit date, sex, and hospital ID to optimize the performance of queries. Indexing these columns ensures that the database can quickly locate and retrieve the necessary data.

## 2. Data Aggregation and Preprocessing

### Pre-aggregated Data

Instead of processing data in real-time, the data is aggregated at the end of each month. This involves calculating the average age of patients grouped by sex and month. The aggregated data is then stored in a summary table designed for quick retrieval.

### Summary Table

A summary table is used to store the pre-aggregated monthly statistics. This table includes columns for hospital ID, year, month, sex, and average age of patients. By pre-aggregating the data, the need for complex real-time calculations is eliminated, thus improving query performance.

### Aggregation Job

A scheduled job (e.g., cron job or using a tool like Apache Airflow) is set up to run at the end of each month. This job calculates the necessary statistics and populates the summary table with the pre-aggregated data.

## 3. Real-Time Query

### Querying the Summary Table

To retrieve the required statistics in real-time, queries are executed against the pre-aggregated summary table. This allows for the quick fetching of data without the need for intensive computation.

### Caching Layer

A caching layer is implemented using a technology such as Redis or Memcached. The caching layer stores the results of the summary queries to further ensure that the response time stays well below 200ms. When a request is made, the system first checks the cache for the data before querying the database.

## 4. Application Layer

### REST API

A REST API endpoint is exposed to allow clients to retrieve the aggregated data. The API is built using a high-performance framework like Spring Boot. The service layer of the application includes caching logic to check for cached results before querying the database, ensuring minimal response times.

## 5. Performance Testing

### Load Testing

Load testing is conducted using tools like Apache JMeter or Gatling. These tests ensure that the API can handle the expected load while maintaining the specified response time of less than 200ms. Performance tests help identify any bottlenecks and ensure that the system is scalable and efficient.

## Summary

By pre-aggregating data, using efficient indexing, leveraging caching mechanisms, and carefully designing the data retrieval process, the system is able to meet the director's requirement for a fast and efficient overview of patient statistics. This approach ensures that the data can be retrieved quickly and reliably, even under high load conditions.







