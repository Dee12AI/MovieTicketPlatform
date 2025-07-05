# Deployment Structure for Movie Ticket Platform

This Spring Boot application can be deployed in several environments. Below is a recommended deployment structure for production and development:

## 1. Directory Layout

- `/src` - Source code (Java, resources)
- `/target` - Compiled JAR/WAR files (after build)
- `/config` - External configuration (application-prod.yml, secrets, etc.)
- `/logs` - Application logs
- `/docker` - Docker-related files (Dockerfile, docker-compose.yml)

## 2. Deployment Options

### A. Standalone JAR
- Build with: `mvn clean package`
- Deploy: `java -jar target/MovieTicketPlatform-1.0-SNAPSHOT.jar --spring.config.location=./config/application-prod.yml`

### B. Docker
- Place a `Dockerfile` in the project root:

```Dockerfile
FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
- Build: `docker build -t movieticket-platform .`
- Run: `docker run -d -p 8080:8080 -v /your/config:/config -v /your/logs:/logs movieticket-platform`

### C. Cloud (AWS, Azure, GCP)
- Deploy the JAR or Docker image to your preferred cloud service (Elastic Beanstalk, App Service, Cloud Run, etc.)
- Use environment variables or mounted config for secrets.

## 3. Externalized Configuration
- Store sensitive configs (DB, credentials) in `/config` or as environment variables.

## 4. Logs
- Configure Spring Boot to write logs to `/logs` for easier monitoring.

---

**Note:**
- For production, use a secure database and externalize all secrets.
- Consider using a reverse proxy (Nginx, Apache) in front of the app for SSL and load balancing.
