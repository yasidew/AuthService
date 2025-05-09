````markdown
# Auth Service 🚀

This repository contains the **Authentication Service** for a larger cloud-based application. The service is built using **Java (Spring Boot)** and provides secure user authentication, registration, and authorization functionality. It is fully Dockerized and integrated into a CI/CD pipeline for automated deployment.

---

## 📋 Features

- 🔒 **User Authentication & Authorization** (JWT-based)
- 📝 **User Registration**
- 📦 **Dockerized Deployment**
- ⚙️ **CI/CD Pipeline** with GitHub Actions + Docker Hub
- ☁️ **Deployed on AWS EC2**
- 🔍 **Static Code Analysis** via SonarCloud
- 🔐 **Secure DB Connection** to AWS RDS (MySQL)

---

## 🖥️ System Overview

_Add your system architecture diagram here 👇_

![System Overview](path/to/your/diagram.png)

---

## ⚙️ Tech Stack

- **Java 17 (Spring Boot)**
- **MySQL (AWS RDS)**
- **Docker + Docker Hub**
- **GitHub Actions (CI/CD)**
- **AWS EC2 (Ubuntu)**
- **SonarCloud (Static Code Analysis)**

---

## 🐳 Docker Setup

The service is containerized using the following **Dockerfile:**

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/epol-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
````

---

### 📦 How to Build & Run Locally

1️⃣ **Build the Docker image:**

```bash
docker build -t authservice:latest .
```

2️⃣ **Run the container:**

```bash
docker run -d -p 8080:8080 --name authservice-container authservice:latest
```

---

## 🚀 CI/CD Pipeline

This project uses **GitHub Actions** for continuous integration and deployment:

* **On Push:**

  * Runs code checkout.
  * Performs SonarCloud scan.
  * Builds the Docker image.
  * Pushes the image to Docker Hub.

* **On Deploy:**

  * AWS EC2 instance (self-hosted runner) pulls the latest Docker image.
  * Stops the old container and runs the new one automatically.

---

