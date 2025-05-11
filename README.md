
**Auth Service 🚀**

This repository contains the **Authentication Service** for a larger cloud-based application. The service is built using **Java (Spring Boot)** and provides secure user authentication, registration, and authorization. It is fully Dockerized and integrated into a CI/CD pipeline for automated deployment.


**Features:**

* **User Authentication & Authorization (JWT-based)**
* **User Registration**
* **Dockerized Deployment**
* **CI/CD Pipeline with GitHub Actions + Docker Hub**
* **Deployed on AWS EC2**
* **Static Code Analysis via SonarCloud**
* **Secure Database Connection to AWS RDS (MySQL)**

---

**System Overview:**


![image](https://github.com/user-attachments/assets/19a22663-dab1-485d-a46d-2c54a0940c4f)


---

**Tech Stack:**

* **Java 17 (Spring Boot)**
* **MySQL (AWS RDS)**
* **Docker & Docker Hub**
* **GitHub Actions (CI/CD Pipeline)**
* **AWS EC2 (Ubuntu Server)**
* **SonarCloud (Code Analysis)**

---

**Docker Setup:**

The service is containerized using the following Dockerfile:

```
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/epol-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

**How to Build & Run Locally:**

1. **Build the Docker image:**

   ```
   docker build -t authservice:latest .
   ```

2. **Run the container:**

   ```
   docker run -d -p 8080:8080 --name authservice-container authservice:latest
   ```

---

**CI/CD Pipeline:**

The CI/CD pipeline is automated using **GitHub Actions**. When changes are pushed to the `main` branch:

* The code is checked out.
* SonarCloud performs a static code analysis.
* The Docker image is built and pushed to Docker Hub.
* The AWS EC2 instance (self-hosted runner) automatically pulls the latest image and redeploys the container.

---

